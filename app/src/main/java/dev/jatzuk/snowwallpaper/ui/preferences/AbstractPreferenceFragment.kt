package dev.jatzuk.snowwallpaper.ui.preferences

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.XmlRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceLiveData
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.ui.imagepicker.TexturedAbstractDialogFragment
import dev.jatzuk.snowwallpaper.viewmodels.AppBarTitleViewModel

abstract class AbstractPreferenceFragment(
    @XmlRes private val xmlRes: Int
) : PreferenceFragmentCompat() {

    protected lateinit var appBarTitleViewModel: AppBarTitleViewModel
    protected lateinit var preferenceRepository: PreferenceRepository
    protected lateinit var textureCache: TextureCache
    protected lateinit var preferenceLiveData: PreferenceLiveData<Boolean>

    final override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xmlRes, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val color = provideBackgroundColor()
        if (color != Color.TRANSPARENT) view?.setBackgroundColor(color)

        val backgroundResId = provideBackground()
        backgroundResId?.let { view?.background = it }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            preferenceRepository = PreferenceRepository.getInstance(this)
            textureCache = TextureCache.getInstance()
            appBarTitleViewModel = ViewModelProvider(this).get(AppBarTitleViewModel::class.java)
        }
        setUp()
    }

    final override fun onResume() {
        super.onResume()
        attachObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachObserver()
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.addItemDecoration(
            Divider(resources.getDimension(R.dimen.preference_divider_height).toInt())
        )
        return recyclerView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        childFragmentManager.fragments[0].onActivityResult(requestCode, resultCode, data)
    }

    protected abstract fun setUp()

    protected open fun attachObserver() {}

    protected open fun detachObserver() {}

    protected fun startDialogFragment(dialogFragment: DialogFragment?) {
        dialogFragment?.let {
            it.setTargetFragment(
                childFragmentManager.findFragmentById(id),
                TexturedAbstractDialogFragment.SELECT_CUSTOM_IMAGE
            )
            childFragmentManager.beginTransaction()
                .add(it, dialogFragment::class.java.simpleName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
    }

    @ColorInt
    protected open fun provideBackgroundColor(): Int = Color.TRANSPARENT

    protected open fun provideBackground(): Drawable? = null

    protected inner class Divider(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.apply {
                top = spaceHeight / 2
                left = spaceHeight / 2
                right = spaceHeight / 2
                if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1)
                    bottom = spaceHeight / 2
            }
        }
    }
}
