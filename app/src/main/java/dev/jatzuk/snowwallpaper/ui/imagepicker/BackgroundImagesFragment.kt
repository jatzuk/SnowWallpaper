package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.AbstractRecyclerAdapter

class BackgroundImagesFragment : Fragment() {

    private var columnCount = 2
    private var listener: AbstractRecyclerAdapter.OnViewHolderClick<BackgroundImage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        retainInstance = true // todo
        arguments?.let { columnCount = it.getInt(ARG_COLUMN_COUNT) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.background_image_list, container, false)

        if (view is RecyclerView) {
            view.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, columnCount)
                adapter = BackgroundImagesAdapter(
                    context,
                    getPredefinedImages(),
                    listener
                )
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val spacing = resources.getDimensionPixelSize(R.dimen.grid_layout_spacing)
                        outRect.apply {
                            left = spacing
                            right = spacing
                            bottom = spacing
                            top = spacing
                        }
                    }
                })
            }
        }

        return view
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AbstractRecyclerAdapter.OnViewHolderClick<*>)
            listener = context as AbstractRecyclerAdapter.OnViewHolderClick<BackgroundImage>
        else throw RuntimeException("$context must implement AbstractRecyclerAdapter.OnViewHolderClick<BackgroundImage>")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getPredefinedImages() = listOf(
        BackgroundImage(R.drawable.background_image),
        BackgroundImage(R.drawable.b0),
        BackgroundImage(R.drawable.b1),
        BackgroundImage(R.drawable.b2)
    )

    companion object {
        const val ARG_COLUMN_COUNT = "columnCount"

        fun newInstance(columnCount: Int) = BackgroundImagesFragment().apply {
            arguments = Bundle().apply { putInt(ARG_COLUMN_COUNT, columnCount) }
        }
    }
}
