package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.helpers.AbstractRecyclerAdapter

class BackgroundImagesAdapter<T : Any>(
    private val context: Context,
    items: List<T>,
    clickListener: OnViewHolderClick<T>? = null
) : AbstractRecyclerAdapter<T>(R.layout.background_image_list_item, items, clickListener) {

    private lateinit var imageView: ImageView

    override fun getView(view: View): View {
        imageView = bind(view.tag.toString(), R.id.circle_image_view) as ImageView
        return view
    }

    override fun onBind(position: Int, listItem: T) {
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                (listItem as BackgroundImage).resourceId
            )
        )
    }
}
