package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.os.Bundle
import android.view.*
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.ImageSlidePageFragment
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.pagetransformers.ZoomOutPageTransformer
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class ViewPagerFragment : Fragment() {

    @DrawableRes
    private var imageId = 0
    private var position = 0
    private lateinit var imagesIds: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            imagesIds = it.getIntArray(EXTRA_IMAGE_LIST_IDS)!!
            imageId = it.getInt(EXTRA_IMAGE_ID)
            position = imagesIds.indexOf(imageId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_image_viewer,
            container,
            false
        )

        view.findViewById<ViewPager2>(R.id.pager).apply {
            adapter = ScreenSlidePagerAdapter(this@ViewPagerFragment)
            setCurrentItem(position, false)
            setPageTransformer(ZoomOutPageTransformer())
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_image_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_background_image -> {
                PreferenceRepository.getInstance(context!!)
                    .setSnowfallTextureSavedPosition(position)

                ImageProvider.saveImage(
                    context!!,
                    ImageProvider.ImageType.BACKGROUND_IMAGE,
                    null,
                    imagesIds[position]
                )

                parentFragmentManager.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = imagesIds.size

        override fun createFragment(position: Int): Fragment {
            return ImageSlidePageFragment.newInstance(imagesIds[position])
        }
    }

    companion object {
        private const val EXTRA_IMAGE_ID = "extraImageId"
        private const val EXTRA_IMAGE_LIST_IDS = "extraImagesListIds"

        fun newInstance(resourcesIds: IntArray, @DrawableRes drawableId: Int) =
            ViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(EXTRA_IMAGE_LIST_IDS, resourcesIds)
                    putInt(EXTRA_IMAGE_ID, drawableId)
                }
            }
    }
}
