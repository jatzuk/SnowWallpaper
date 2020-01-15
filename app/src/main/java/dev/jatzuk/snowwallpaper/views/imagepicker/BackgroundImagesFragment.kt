package dev.jatzuk.snowwallpaper.views.imagepicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.R

class BackgroundImagesFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        retainInstance = true
        arguments?.let { columnCount = it.getInt(ARG_COLUMN_COUNT) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_background_image_list, container, false)

        if (view is RecyclerView) {
            view.apply {
                setHasFixedSize(true)
                layoutManager =
                    if (columnCount < 2) LinearLayoutManager(context)
                    else GridLayoutManager(context, columnCount)

                adapter = BackgroundImagesAdapter(getPredefinedImages(), listener)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) listener = context
        else throw RuntimeException("$context must implement OnListFragmentInteractionListener")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getPredefinedImages() = listOf(
        BackgroundImage(R.drawable.background_image),
        BackgroundImage(R.drawable.background_image)
    )

//    private fun getBackgroundImageUri(resourceId: Int): String {
//        val uri = Uri.parse(
//            ContentResolver.SCHEME_ANDROID_RESOURCE +
//                    "://${resources.getResourcePackageName(resourceId)}" +
//                    "/${resources.getResourceTypeName(resourceId)}" +
//                    "/${resources.getResourceEntryName(resourceId)}"
//        )
//        return uri.path!!
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: BackgroundImage)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "columnCount"

        fun newInstance(columnCount: Int) = BackgroundImagesFragment().apply {
            arguments = Bundle().apply { putInt(ARG_COLUMN_COUNT, columnCount) }
        }
    }
}
