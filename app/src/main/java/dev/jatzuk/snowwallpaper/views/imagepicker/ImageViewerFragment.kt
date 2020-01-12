package dev.jatzuk.snowwallpaper.views.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.views.imagepicker.viewpager.ImageSlidePageFragment

class ImageViewerFragment : Fragment() { // view pager2 content home

    @DrawableRes
    private var imageResourceId = -1
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        retainInstance = true

//        arguments?.let {
//            imageResourceId = it.getInt(EXTRA_IMAGE_VIEW)
//        } ?: throw RuntimeException("no image provided")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_image_viewer /*fragment_screen_slide_page*/, container, false)

//        val webView = v.findViewById<WebView>(R.id.web_view)
//        webView.apply {
//            settings.builtInZoomControls = true
//            loadUrl("file:///android_res/drawable/background_image.jpg")
//        }

        viewPager = v.findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.apply {
            adapter = pagerAdapter
//            setPageTransformer(ZoomOutPageTransformer())
        }

        return v
    }

    inner class ScreenSlidePagerAdapter(f: Fragment): FragmentStateAdapter(f) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
         return ImageSlidePageFragment.newInstance(position)
        }
    }

    companion object {
        const val EXTRA_IMAGE_VIEW = "extraImageView"

        fun newInstance(@DrawableRes resourceId: Int) = ImageViewerFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_IMAGE_VIEW, resourceId) }
        }
    }
}
