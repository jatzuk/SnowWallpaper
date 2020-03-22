package dev.jatzuk.snowwallpaper.ui.preferences.texturepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.helpers.AbstractRecyclerAdapter
import dev.jatzuk.snowwallpaper.ui.helpers.CircleImageView
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import kotlin.math.abs
import kotlin.math.max

class PickerDialogFragment : DialogFragment() {

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var textureAdapter: TextureAdapter<Int>
    private lateinit var viewPager: ViewPager2
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private val predefinedTextureList =
        listOf(
            R.drawable.texture_snowflake,
            R.drawable.texture_snowfall,
            R.drawable.b0
        )
    private var viewPager2Position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        textureAdapter = TextureAdapter(
            predefinedTextureList,
            object : AbstractRecyclerAdapter.OnViewHolderClick<Int> {
                override fun onClick(view: View?, position: Int, item: Int) {

                    if (position == predefinedTextureList.lastIndex) {
                        startIntent()
                    }
                }
            }
        )

        preferenceRepository = PreferenceRepository.getInstance(context!!)
        viewPager2Position = preferenceRepository.getSnowfallTextureSavedPosition()
        if (viewPager2Position == predefinedTextureList.lastIndex) {
            loadUserTexture() // todo parent view not initialized at this moment
        }

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                textureAdapter.getParentView()?.children?.forEachIndexed { index, view ->
                    val circleImageView =
                        view.findViewById<CircleImageView>(R.id.circle_image_view)
                    if (position != index) circleImageView.disableStroke()
                    else circleImageView.setStroke(10f, Color.GREEN)
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

                    setCurrentItem(viewPager2Position, false)

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
            setPositiveButton("Select this") { _, _ ->
                viewPager2Position = viewPager.currentItem
                preferenceRepository.setSnowfallTextureSavedPosition(viewPager2Position)
                if (viewPager2Position != predefinedTextureList.lastIndex) {
                    storeSelectedImage()
                    dismiss()
                } else {
//                    for custom click on view
                }
            }
            setNegativeButton("Dismiss") { _, _ -> dismiss() }
            create()
        }
    }

    override fun onStart() {
        super.onStart()
//        dialog!!.window!!.setLayout(1080, 1920 / 2)
        viewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    private fun startIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        parentFragment?.startActivityForResult(intent, SELECT_CUSTOM_SNOWFALL_TEXTURE)
    }

    private fun storeSelectedImage() {
        // todo pos != lastIndex (for imagepicker view)
        if (viewPager2Position != predefinedTextureList.lastIndex) {
            ImageProvider.saveImage(
                context!!,
                null,
                ImageProvider.ImageType.SNOWFALL_TEXTURE,
                predefinedTextureList[viewPager2Position]
            )
        } else {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                SELECT_CUSTOM_SNOWFALL_TEXTURE -> {
                    data?.let {
                        val cr = context?.contentResolver
                        val stringType = cr?.getType(it.data!!)
                        if (stringType?.substringBefore("/") == "image") {
                            ImageProvider.saveImage(
                                context!!,
                                it.data!!,
                                ImageProvider.ImageType.SNOWFALL_TEXTURE
                            )
                            updateViewPagerPreview(it.data!!)
                        } else {
                            Toast.makeText(
                                context,
                                "", // todo
                                Toast.LENGTH_SHORT
                            ).show()
                        }//todo(not image)
                    }
                }
            }
        }
    }

    private fun loadUserTexture() {
        val bitmap = ImageProvider.loadTexture(context!!, ImageProvider.ImageType.SNOWFALL_TEXTURE)
        setBitmapForCurrentViewHolder(bitmap!!)
    }

    private fun updateViewPagerPreview(uri: Uri) {
//        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val source = ImageDecoder.createSource(context!!.contentResolver, uri)
//            ImageDecoder.decodeBitmap(source)
//        } else {
//            @Suppress("DEPRECATION")
//            MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
//        }

        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        setBitmapForCurrentViewHolder(bitmap!!)
    }

    private fun setBitmapForCurrentViewHolder(bitmap: Bitmap) {
        val circleImageView = textureAdapter.getParentView()?.getChildAt(viewPager2Position)
            ?.findViewById<CircleImageView>(R.id.circle_image_view)
        circleImageView?.setPreviewImage(
            bitmap.toDrawable(resources),
            resources.getDimensionPixelSize(R.dimen.texture_picker_image_size)
        )
    }

    companion object {
        const val SELECT_CUSTOM_SNOWFALL_TEXTURE = 1
    }
}
