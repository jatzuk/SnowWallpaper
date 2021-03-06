package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.os.Bundle
import android.view.*
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.databinding.FragmentImageViewerBinding
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.ImageSlidePageFragment
import dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager.pagetransformers.ZoomOutPageTransformer
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentImageViewerBinding? = null
    private val binding get() = _binding!!
    private lateinit var textureType: TextureProvider.TextureType
    private var startPosition = 0
    private lateinit var imagesIds: IntArray

    @DrawableRes
    private var imageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            textureType = TextureProvider.TextureType.values()[it.getInt(EXTRA_IMAGE_TYPE)]
            imagesIds = it.getIntArray(EXTRA_IMAGE_LIST_IDS)!!
            imageId = it.getInt(EXTRA_IMAGE_ID)
            startPosition = imagesIds.indexOf(imageId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageViewerBinding.inflate(inflater, container, false)
        binding.viewPager2.apply {
            adapter = ScreenSlidePagerAdapter(this@ViewPagerFragment)
            setCurrentItem(startPosition, false)
            setPageTransformer(ZoomOutPageTransformer())
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_image_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_background_image -> {
                val preferenceRepository = PreferenceRepository.getInstance(requireContext())

                when (textureType) {
                    TextureProvider.TextureType.SNOWFALL_TEXTURE -> {
                        preferenceRepository.setSnowfallTextureSavedPosition(binding.viewPager2.currentItem)
                    }
                    TextureProvider.TextureType.SNOWFLAKE_TEXTURE -> {
                        preferenceRepository.setSnowflakeTextureSavedPosition(binding.viewPager2.currentItem)
                    }
                    TextureProvider.TextureType.BACKGROUND_IMAGE -> {
                        preferenceRepository.setBackgroundImageSavedPosition(binding.viewPager2.currentItem)
                    }
                }

                TextureProvider.saveImage(
                    requireContext(),
                    textureType,
                    null,
                    imagesIds[binding.viewPager2.currentItem]
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = imagesIds.size

        override fun createFragment(position: Int): Fragment {
            return ImageSlidePageFragment.newInstance(imagesIds[position])
        }
    }

    companion object {
        private const val EXTRA_IMAGE_TYPE = "image_type"
        private const val EXTRA_IMAGE_ID = "extraImageId"
        private const val EXTRA_IMAGE_LIST_IDS = "extraImagesListIds"

        fun newInstance(
            textureType: TextureProvider.TextureType,
            resourcesIds: IntArray,
            @DrawableRes drawableId: Int
        ) = ViewPagerFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_IMAGE_TYPE, textureType.ordinal)
                putIntArray(EXTRA_IMAGE_LIST_IDS, resourcesIds)
                putInt(EXTRA_IMAGE_ID, drawableId)
            }
        }
    }
}
