package dev.jatzuk.snowwallpaper.views.imagepicker.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import dev.jatzuk.snowwallpaper.R

class ImageSlidePageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        val imageView = v.findViewById<ImageView>(R.id.image_view)
        imageView.setImageResource(R.drawable.background_image)
        return v
    }

    companion object {
        fun newInstance(position: Int) =
            ImageSlidePageFragment()
    }
}
