package dev.jatzuk.snowwallpaper.views.imagepicker.viewpager

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.util.Logger.logging

class ImageSlidePageFragment : Fragment() {

    @DrawableRes
    private var imageId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        arguments?.let { imageId = it.getInt(ARG_POSITION) }
        val v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        val imageView = v.findViewById<ImageView>(R.id.image_view)
        imageView.setImageResource(imageId) // todo(setDrawable)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = getString(R.string.pick_image_menu_text)

            view?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    logging("DOwN", TAG)
                    if (isShowing) hide()
                    else show()
                }
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_image_picker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_background_image -> {
                activity?.supportFragmentManager?.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
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
