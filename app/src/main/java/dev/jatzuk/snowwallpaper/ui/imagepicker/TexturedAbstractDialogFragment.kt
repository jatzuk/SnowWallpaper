package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.helpers.AbstractRecyclerAdapter
import dev.jatzuk.snowwallpaper.ui.helpers.CircleImageView
import dev.jatzuk.snowwallpaper.utilities.TextureProvider
import kotlin.math.abs
import kotlin.math.max

abstract class TexturedAbstractDialogFragment(
    private val textureIds: IntArray,
    private val textureType: TextureProvider.TextureType
) : DialogFragment() {

    protected lateinit var preferenceRepository: PreferenceRepository
    private lateinit var viewPager: ViewPager2
    private var textureAdapter: TextureAdapter<Bitmap>? = null
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private val textureArray = ArrayList<Bitmap>()
    private var viewPagerCurrentPosition = 0
    private lateinit var neuralButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceRepository = PreferenceRepository.getInstance(requireContext())
        viewPagerCurrentPosition = getTextureSavedPosition()

        textureIds.forEach {
            textureArray.add((ContextCompat.getDrawable(requireContext(), it))!!.toBitmap())
        }

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPagerCurrentPosition = position
                textureAdapter?.getParentView()?.children?.forEachIndexed { index, view ->
                    val circleImageView = view.findViewById<CircleImageView>(R.id.circle_image_view)
                    if (position == index) {
                        circleImageView.setStroke(
                            resources.getDimensionPixelSize(R.dimen.circle_image_view_stroke_width)
                        )
                    } else {
                        circleImageView.disableStroke()
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        textureAdapter = TextureAdapter(
            textureArray,
            object : AbstractRecyclerAdapter.OnViewHolderClick<Bitmap> {
                override fun onClick(view: View?, position: Int, item: Bitmap) {
                    if (position < textureIds.size) startImageViewerFragment()
                }
            }
        )

        return AlertDialog.Builder(requireContext(), R.style.DefaultAlertDialog).run {
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

                                    val zTranslation = (minAlpha +
                                            (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                                    alpha = zTranslation

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        translationZ = zTranslation
                                    }
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

                retainInstance = true

                if (viewPagerCurrentPosition == textureIds.size) {
                    loadUserTexture()?.let { notifyAdapter(it) }
                }
            }

            setView(view)
            setTitle(getString(R.string.pick_image))
            setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ -> storeSelectedImage() }
            setNegativeButton(getString(R.string.dialog_negative_button)) { _, _ -> dismiss() }
            // work around from letting android destroy dialog on image load
            setNeutralButton(getString(R.string.dialog_add_custom_image_button)) { _, _ -> }
            create()
        }
    }

    override fun onStart() {
        super.onStart()
        viewPager.registerOnPageChangeCallback(onPageChangeCallback)

        requireDialog().run {
            neuralButton = (this as AlertDialog).getButton(Dialog.BUTTON_NEUTRAL).also {
                it.setOnClickListener { startImagePickerIntent() }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textureAdapter = null
    }

    private fun startImagePickerIntent() {
        parentFragment?.startActivityForResult(
            Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
            SELECT_CUSTOM_IMAGE
        )
    }

    private fun startImageViewerFragment() {
        val fragment = ViewPagerFragment.newInstance(
            textureType,
            textureIds,
            textureIds[viewPagerCurrentPosition]
        )
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.preferences_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun storeSelectedImage() {
        setTextureSavedPosition(viewPagerCurrentPosition)

        TextureProvider.saveImage(
            requireContext(),
            textureType,
            textureArray[viewPagerCurrentPosition]
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == SELECT_CUSTOM_IMAGE) {
            data?.let {
                val stringType = context?.contentResolver?.getType(it.data!!)
                if (stringType?.substringBefore("/") == "image") {
                    val bitmap = getBitmapFromUri(it.data!!)
                    for (i in textureIds.size until textureArray.size) {
                        if (bitmap.rowBytes == textureArray[i].rowBytes) return
                    }
                    notifyAdapter(bitmap)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.item_is_not_an_image),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadUserTexture(): Bitmap? =
        TextureProvider.loadTexture(requireContext(), textureType)

    @Suppress("DEPRECATION")
    private fun getBitmapFromUri(uri: Uri): Bitmap =
        MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)

    private fun notifyAdapter(bitmap: Bitmap) {
        textureArray.add(bitmap)
        viewPagerCurrentPosition = textureArray.lastIndex
        textureAdapter?.notifyItemInserted(viewPagerCurrentPosition)
        viewPager.setCurrentItem(viewPagerCurrentPosition, false)
    }

    abstract fun setTextureSavedPosition(position: Int)

    abstract fun getTextureSavedPosition(): Int

    companion object {
        const val SELECT_CUSTOM_IMAGE = 1
    }
}
