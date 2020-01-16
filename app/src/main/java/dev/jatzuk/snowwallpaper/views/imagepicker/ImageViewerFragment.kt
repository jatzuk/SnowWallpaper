package dev.jatzuk.snowwallpaper.views.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.views.imagepicker.viewpager.ImageSlidePageFragment

class ImageViewerFragment : Fragment() { // view pager2 content home

    @DrawableRes
    private var imageId = 0
    private var position = 0
    private lateinit var viewPager: ViewPager2
    private val imagesIds = listOf(
        R.drawable.background_image,
        R.drawable.b0,
        R.drawable.b1,
        R.drawable.b2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        retainInstance = true

        arguments?.let {
            imageId = it.getInt(EXTRA_IMAGE_ID)
            position = imagesIds.indexOf(imageId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(
            R.layout.fragment_image_viewer /*fragment_screen_slide_page*/,
            container,
            false
        )

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

    override fun onResume() {
        super.onResume()
        viewPager.postDelayed({ viewPager.currentItem = position }, 50)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.images)
    }

    inner class ScreenSlidePagerAdapter(f: Fragment) : FragmentStateAdapter(f) {

        override fun getItemCount(): Int {
            return imagesIds.size
        }

        override fun createFragment(position: Int): Fragment {
            viewPager.currentItem = imagesIds[position]
            return ImageSlidePageFragment.newInstance(imagesIds[position])
        }
    }

    companion object {
        private const val EXTRA_IMAGE_ID = "extraImageId"
        private const val EXTRA_IMAGE_POSITION = "extraImagePosition"

        fun newInstance(@DrawableRes resourceId: Int) =
            ImageViewerFragment().apply {
                arguments = Bundle().apply { putInt(EXTRA_IMAGE_ID, resourceId) }
            }
    }
}
