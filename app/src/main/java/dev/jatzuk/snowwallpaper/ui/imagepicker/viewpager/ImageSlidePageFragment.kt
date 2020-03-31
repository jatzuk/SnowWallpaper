package dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.jatzuk.snowwallpaper.R

class ImageSlidePageFragment : Fragment() {

    @DrawableRes
    private var imageId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { imageId = it.getInt(ARG_IMAGE_ID) }
        val v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        v.findViewById<ImageView>(R.id.image_view).apply {
            setImageDrawable(ContextCompat.getDrawable(context!!, imageId))
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        removeMargin()
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = getString(R.string.pick_image_menu_text)

            view?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (isShowing) hide()
                    else show()
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addMargin()
    }

    private fun removeMargin() {
        activity!!.findViewById<FrameLayout>(R.id.preferences_container)!!.apply {
            val params = RelativeLayout.LayoutParams(layoutParams)
            params.topMargin = 0
            layoutParams = params
        }
    }

    private fun addMargin() {
        val typedValue = TypedValue()
        if (activity!!.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            activity!!.findViewById<FrameLayout>(R.id.preferences_container)!!.apply {
                val params = RelativeLayout.LayoutParams(layoutParams)
                params.topMargin =
                    TypedValue.complexToDimensionPixelSize(
                        typedValue.data,
                        resources.displayMetrics
                    )
                layoutParams = params
            }
        }
    }

    companion object {
        private const val TAG = "ImageSlidePageFragment"
        private const val ARG_IMAGE_ID = "imageId"

        fun newInstance(position: Int) = ImageSlidePageFragment().apply {
            arguments = Bundle().apply { putInt(ARG_IMAGE_ID, position) }
        }
    }
}
