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
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import kotlin.math.abs
import kotlin.math.max

class PickerDialogFragment : DialogFragment() {

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var textureAdapter: TextureAdapter<Drawable>
    private lateinit var viewPager: ViewPager2
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private lateinit var predefinedTextureList: ArrayList<Drawable>
    private var viewPagerCurrentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        predefinedTextureList = arrayListOf(
            ContextCompat.getDrawable(context!!, R.drawable.texture_snowflake)!!,
            ContextCompat.getDrawable(context!!, R.drawable.texture_snowfall)!!,
            ContextCompat.getDrawable(context!!, R.drawable.b0)!!
        )

        textureAdapter = TextureAdapter(
            predefinedTextureList,
            object : AbstractRecyclerAdapter.OnViewHolderClick<Drawable> {
                override fun onClick(view: View?, position: Int, item: Drawable) {

                    if (position == predefinedTextureList.lastIndex) {
                        startIntent()
                    }
                }
            }
        )

        preferenceRepository = PreferenceRepository.getInstance(context!!)
        viewPagerCurrentPosition = preferenceRepository.getSnowfallTextureSavedPosition()
        if (viewPagerCurrentPosition == predefinedTextureList.lastIndex) loadUserTexture()

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                viewPagerCurrentPosition = position
                textureAdapter.getParentView()?.children?.forEachIndexed { index, view ->
                    val circleImageView =
                        view.findViewById<CircleImageView>(R.id.circle_image_view)
                    if (position != index || index == predefinedTextureList.lastIndex)
                        circleImageView.disableStroke()
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
            setPositiveButton("Select this") { _, _ ->
//                viewPager2Position = viewPager.currentItem
                if (viewPagerCurrentPosition != predefinedTextureList.lastIndex) {
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
        preferenceRepository.setSnowfallTextureSavedPosition(viewPagerCurrentPosition)
        if (viewPagerCurrentPosition != predefinedTextureList.lastIndex) {
            ImageProvider.saveImage(
                context!!,
                ImageProvider.ImageType.SNOWFALL_TEXTURE,
                predefinedTextureList[viewPagerCurrentPosition].toBitmap()
            )
            //todo no success msg
        } else {
            Toast.makeText(
                context,
                "cant save this icon",
                Toast.LENGTH_SHORT
            ).show() // todo
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
                            updateItemPreview(it.data!!)
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
        insertBitmapToTextureList(bitmap!!)
    }

    private fun updateItemPreview(uri: Uri) {
        @Suppress("DEPRECATION")
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        insertBitmapToTextureList(bitmap)
    }

    private fun insertBitmapToTextureList(bitmap: Bitmap) {
        predefinedTextureList.add(viewPagerCurrentPosition, bitmap.toDrawable(resources))
        textureAdapter.notifyItemInserted(viewPagerCurrentPosition)
    }

    companion object {
        const val SELECT_CUSTOM_SNOWFALL_TEXTURE = 1
    }
}
