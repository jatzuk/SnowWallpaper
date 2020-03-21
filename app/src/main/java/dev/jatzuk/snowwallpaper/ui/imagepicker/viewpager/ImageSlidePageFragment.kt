package dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
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
        arguments?.let { imageId = it.getInt(ARG_POSITION) }
        val v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        val imageView = v.findViewById<ImageView>(R.id.circle_image_view)
        imageView.setImageResource(imageId) // todo(setDrawable)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    companion object {
        private const val TAG = "ImageSlidePageFragment"
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int) = ImageSlidePageFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSITION, position) }
        }
    }
}
