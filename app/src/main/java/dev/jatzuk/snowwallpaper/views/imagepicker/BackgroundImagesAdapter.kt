package dev.jatzuk.snowwallpaper.views.imagepicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.views.imagepicker.BackgroundImagesFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.background_image_list_item.view.*

class BackgroundImagesAdapter(
    private val images: List<BackgroundImage>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<BackgroundImagesAdapter.ViewHolder>() {

    private val onClickListener = View.OnClickListener {
        val item = it.tag as BackgroundImage
        // Notify the active callbacks interface (the activity, if the fragment is attached to
        // one) that an item has been selected.
        listener?.onListFragmentInteraction(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.background_image_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = images[position]
        holder.imageView.setImageResource(item.resourceId) // todo

        holder.view.apply {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = images.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.image_view
    }
}
