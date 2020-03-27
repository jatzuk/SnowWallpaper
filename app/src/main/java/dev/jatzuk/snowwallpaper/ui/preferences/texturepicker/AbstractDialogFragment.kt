package dev.jatzuk.snowwallpaper.ui.preferences.texturepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.helpers.AbstractRecyclerAdapter
import dev.jatzuk.snowwallpaper.ui.helpers.CircleImageView
import dev.jatzuk.snowwallpaper.ui.imagepicker.ImageViewerFragment
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import kotlin.math.abs
import kotlin.math.max

abstract class AbstractDialogFragment(
    private val textureIds: Array<Int>,
    private val imageType: ImageProvider.ImageType
) : DialogFragment() {

    protected lateinit var preferenceRepository: PreferenceRepository
    private lateinit var positiveButton: Button
    private lateinit var textureAdapter: TextureAdapter<Drawable>
    private lateinit var viewPager: ViewPager2
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private val textureArray = ArrayList<Drawable>()
    private var viewPagerCurrentPosition = 0
    private var userPickedImage: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        textureIds.forEach { textureArray.add(ContextCompat.getDrawable(context!!, it)!!) }
        textureArray.add(ContextCompat.getDrawable(context!!, R.drawable.b0)!!)

        textureAdapter = TextureAdapter(
            textureArray,
            object : AbstractRecyclerAdapter.OnViewHolderClick<Drawable> {
                override fun onClick(view: View?, position: Int, item: Drawable) {
                    if (position == textureArray.lastIndex) startImagePickerIntent()
                    else {
                        val fragment = ImageViewerFragment.newInstance(textureIds[position])
                        childFragmentManager.beginTransaction()
                            .add(fragment, "").commit()
                    }
                }
            }
        )

        preferenceRepository = PreferenceRepository.getInstance(context!!)
        viewPagerCurrentPosition = provideTexturePositionLoadPosition()

        if (viewPagerCurrentPosition == textureArray.lastIndex) {
            val bitmap = loadUserTexture()
            bitmap?.let { notifyAdapter(it.toDrawable(resources)) }
        }

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                positiveButton.isEnabled = position != textureArray.lastIndex
                viewPagerCurrentPosition = position
                textureAdapter.getParentView()?.children?.forEachIndexed { index, view ->
                    val circleImageView =
                        view.findViewById<CircleImageView>(R.id.circle_image_view)
                    if (position != index || index == textureArray.lastIndex) {
                        circleImageView.disableStroke()
                    } else {
                        circleImageView.setStroke(
                            resources.getDimensionPixelSize(R.dimen.circle_image_view_stroke_width),
                            Color.GREEN
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!).run {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_picker_dialog, null).apply {
                findViewById<ViewPager2>(R.id.pager).run {
                    adapter = textureAdapter
                    orientation = ORIENTATION_HORIZONTAL
                    clipToPadding = false
                    clipChildren = false
                    offscreenPageLimit = 3

                    setCurrentItem(viewPagerCurrentPosition, false)

                    setPageTransformer { page, position ->
                        page.apply {
                            val minScale = 0.5f
                            val minAlpha = 0.3f
                            val pageWidth = width
                            val pageHeight = height
                            when {
                                position < -1 -> alpha = 0f
                                position <= 1 -> {
                                    val scaleFactor = max(minScale, 1 - abs(position))
                                    val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                                    val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                                    translationX =
                                        if (position < 0) horizontalMargin - verticalMargin / 2
                                        else horizontalMargin + verticalMargin / 2

                                    scaleX = scaleFactor
                                    scaleY = scaleFactor

                                    alpha = (minAlpha +
                                            (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                                }
                                else -> alpha = 0f
                            }
                        }

                        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.page_margin)
                        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
                        val offset = position * -(2 * offsetPx + pageMarginPx)
                        page.translationX = offset
                    }

                    viewPager = this
                }
            }

            setView(view)
            setTitle("Pick snowfall texture")
            setPositiveButton("Select this") { _, _ -> storeSelectedImage() }
            setNegativeButton("Dismiss") { _, _ -> }
            create()
        }
    }

    override fun onStart() {
        super.onStart()
//        dialog!!.window!!.setLayout(1080, 1920 / 2)
        viewPager.registerOnPageChangeCallback(onPageChangeCallback)

        dialog?.let { positiveButton = (it as AlertDialog).getButton(Dialog.BUTTON_POSITIVE) }
    }

    private fun startImagePickerIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        parentFragment?.startActivityForResult(intent, SELECT_CUSTOM_IMAGE)
    }

    private fun storeSelectedImage() {
        provideTexturePositionSavePosition(viewPagerCurrentPosition)

        ImageProvider.saveImage(
            context!!,
            imageType,
            textureArray[viewPagerCurrentPosition].toBitmap()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                SELECT_CUSTOM_IMAGE -> {
                    data?.let {
                        val cr = context?.contentResolver
                        val stringType = cr?.getType(it.data!!)
                        if (stringType?.substringBefore("/") == "image") {
                            val drawable = getBitmapFromUri(it.data!!).toDrawable(resources)
                            notifyAdapter(drawable)
                        } else {
                            Toast.makeText(
                                context,
                                "selected item is not an image", // todo
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun loadUserTexture(): Bitmap? = ImageProvider.loadTexture(context!!, imageType)

    @Suppress("DEPRECATION")
    private fun getBitmapFromUri(uri: Uri): Bitmap =
        MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)

    private fun notifyAdapter(drawable: Drawable) {
        if (userPickedImage == null) {
            textureArray.add(viewPagerCurrentPosition, drawable)
            textureAdapter.notifyItemInserted(viewPagerCurrentPosition)
        } else {
            textureArray[viewPagerCurrentPosition - 1] = drawable
            textureAdapter.notifyItemChanged(viewPagerCurrentPosition - 1)
            viewPager.currentItem = viewPagerCurrentPosition - 1
        }

        userPickedImage = drawable
    }

    abstract fun provideTexturePositionSavePosition(position: Int)

    abstract fun provideTexturePositionLoadPosition(): Int

    companion object {
        const val SELECT_CUSTOM_IMAGE = 1
    }
}
