package dev.jatzuk.snowwallpaper.ui.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.utilities.Logger.logging

abstract class AbstractRecyclerAdapter<T : Any>(
    private val layoutResource: Int,
    protected val itemsList: List<T>,
    protected val listener: OnViewHolderClick<T>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val views = HashMap<String, View>()
    protected var parentViewGroup: ViewGroup? = null

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        view.tag = view.hashCode()
        if (parentViewGroup == null) {
            parentViewGroup = parent
            logging("parentViewGroup initialized!!!!!!!!!!!!!!!!!!!!!!!")
        }
        return ViewHolder(view)
    }

    final override fun getItemCount(): Int = itemsList.size

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(position, itemsList[position])
    }

    abstract fun getView(view: View): View

    abstract fun onBind(position: Int, listItem: T)

    protected fun bind(tag: String, viewId: Int): View {
        return views[tag]!!.findViewById(viewId)
    }

    private inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            this@AbstractRecyclerAdapter.views[itemView.tag.toString()] = itemView
            getView(itemView)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onClick(v, adapterPosition, itemsList[adapterPosition])
        }
    }

    interface OnViewHolderClick<T> {
        fun onClick(view: View?, position: Int, item: T)
    }

}
