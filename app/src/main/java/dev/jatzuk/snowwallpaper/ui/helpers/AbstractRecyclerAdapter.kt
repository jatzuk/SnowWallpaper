package dev.jatzuk.snowwallpaper.ui.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractRecyclerAdapter<T : Any>(
    private val layoutResource: Int,
    protected val itemsList: List<T>,
    protected val listener: OnViewHolderClick<T>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val views = HashMap<Int, View>()
    protected var parentViewGroup: ViewGroup? = null

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        view.tag = view.hashCode()
        views[view.tag as Int] = view
        if (parentViewGroup == null) parentViewGroup = parent
        return ViewHolder(view)
    }

    final override fun getItemCount(): Int = itemsList.size

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(position, itemsList[position])
    }

    abstract fun getView(view: View): View

    abstract fun onBind(position: Int, listItem: T)

    protected fun bind(tag: Int, viewId: Int): View {
        return views[tag]!!.findViewById(viewId)
    }

    private inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
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
