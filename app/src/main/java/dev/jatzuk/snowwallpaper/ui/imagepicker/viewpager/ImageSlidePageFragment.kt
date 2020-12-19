package dev.jatzuk.snowwallpaper.ui.imagepicker.viewpager

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.databinding.FragmentScreenSlidePageBinding

class ImageSlidePageFragment : Fragment() {

    private var _binding: FragmentScreenSlidePageBinding? = null
    private val binding get() = _binding!!

    @DrawableRes
    private var imageId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { imageId = it.getInt(ARG_IMAGE_ID) }
        _binding = FragmentScreenSlidePageBinding.inflate(inflater, container, false)
        binding.imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), imageId))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = getString(R.string.pick_image)

//            view?.setOnTouchListener { view, event ->
//                if (event.action == MotionEvent.ACTION_UP) {
//                    if (isShowing) hide()
//                    else show()
//                }
//                !view.performClick()
//            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun removeMargin() {
        requireActivity().findViewById<FrameLayout>(R.id.preferences_container)!!.apply {
            val params = RelativeLayout.LayoutParams(layoutParams)
            params.topMargin = 0
            layoutParams = params
        }
    }

    private fun addMargin() {
        val typedValue = TypedValue()
        if (requireActivity().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                typedValue,
                true
            )
        ) {
            requireActivity().findViewById<FrameLayout>(R.id.preferences_container)!!.apply {
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
