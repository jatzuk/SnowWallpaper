package dev.jatzuk.snowwallpaper.ui.imagepicker

import android.graphics.Bitmap
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.ui.helpers.AbstractRecyclerAdapter
import dev.jatzuk.snowwallpaper.ui.helpers.CircleImageView

class TextureAdapter<T : Any>(
    items: List<T>,
    clickListener: OnViewHolderClick<T>? = null
) : AbstractRecyclerAdapter<T>(R.layout.texture_list_item, items, clickListener) {

    private lateinit var circleImageView: CircleImageView

    override fun getView(view: View): View {
        circleImageView = bind(view.tag as Int, R.id.circle_image_view) as CircleImageView
        return view
    }

    override fun onBind(position: Int, listItem: T) {
        circleImageView.run {
            setPreviewImage(
                (listItem as Bitmap).toDrawable(resources),
                resources.getDimensionPixelSize(R.dimen.texture_picker_image_size)
            )
        }
    }

    fun getParentView() = parentViewGroup
}
