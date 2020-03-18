package dev.jatzuk.snowwallpaper.ui.preferences.texturepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.AbstractRecyclerAdapter
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import kotlin.math.abs
import kotlin.math.max

class PickerDialogFragment : DialogFragment() {

    private lateinit var textureAdapter: TextureAdapter<Int>
    private lateinit var viewPager: ViewPager2

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        arguments?.let {
//            return super.onCreateDialog(savedInstanceState)
//        }

        return AlertDialog.Builder(context!!).run {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_picker_dialog, null)

            textureAdapter = TextureAdapter(
                context,
                listOf(R.drawable.texture_snowflake, R.drawable.texture_snowfall, R.drawable.b1),
                object : AbstractRecyclerAdapter.OnViewHolderClick<Int> {
                    override fun onClick(view: View?, position: Int, item: Int) {
                        logging("$item on $position for $view clicked")
                    }
                }
            )
            view.findViewById<ViewPager2>(R.id.pager).run {
                adapter = textureAdapter
                orientation = ORIENTATION_HORIZONTAL
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 3

                setPageTransformer { page, position ->
                    page.apply {
                        val minScale = 0.5f
                        val minAlpha = 0.3f
                        val pageWidth = width
                        val pageHeight = height
                        when {
                            position < -1 -> alpha = 0f
                            position <= 1 -> {
                                val scaleFactor = max(minScale, 1 - abs(position))
                                val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                                val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                                translationX =
                                    if (position < 0) horizontalMargin - verticalMargin / 2
                                    else horizontalMargin + verticalMargin / 2

                                scaleX = scaleFactor
                                scaleY = scaleFactor

                                alpha = (minAlpha +
                                        (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha)))
                            }
                            else -> alpha = 0f
                        }
                    }

                    val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.page_margin)
                    val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
                    val offset = position * -(2 * offsetPx + pageMarginPx)
                    page.translationX = offset
                }

                viewPager = this
            }

            setView(view)
            setTitle("Alert dialog")
            setMessage("message")
            setPositiveButton("positive") { dialog, which ->
                dismiss()
            }
            setNegativeButton("negative") { dialog, which ->
                dismiss()
            }

            create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onStart() {
        super.onStart()
//        dialog!!.window!!.setLayout(1080, 1920 / 2)
    }
}
