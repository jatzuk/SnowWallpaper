package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.os.Bundle
import android.view.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.ImageSlidePageFragment
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.pagetransformers.ZoomOutPageTransformer
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

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
        setHasOptionsMenu(true)
        retainInstance = true

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

        viewPager = v.findViewById(R.id.pager)
        viewPager.apply {
            adapter = ScreenSlidePagerAdapter(this@ImageViewerFragment)
            viewPager.setCurrentItem(position, false)
            setPageTransformer(ZoomOutPageTransformer())
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.images)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_image_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_background_image -> {
//                val resultMessage =
//                    if (ImageProvider.saveBackgroundImage(
//                        context!!,
//                        resourceId = imagesIds[viewPager.currentItem]
//                    )) "success" else
//                    "we have some problems storing image" // todo
//                Toast.makeText(
//                    context,
//                    resultMessage,
//                    Toast.LENGTH_SHORT
//                ).show()
                ImageProvider.saveImage(
                    context!!,
                    null,
                    ImageProvider.ImageType.BACKGROUND_IMAGE,
                    imagesIds[viewPager.currentItem]
                )
                activity?.supportFragmentManager?.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return imagesIds.size
        }

        override fun createFragment(position: Int): Fragment {
            return ImageSlidePageFragment.newInstance(imagesIds[position])
        }
    }

    companion object {
        private const val EXTRA_IMAGE_ID = "extraImageId"

        fun newInstance(@DrawableRes resourceId: Int) =
            ImageViewerFragment().apply {
                arguments = Bundle().apply { putInt(EXTRA_IMAGE_ID, resourceId) }
            }
    }
}
