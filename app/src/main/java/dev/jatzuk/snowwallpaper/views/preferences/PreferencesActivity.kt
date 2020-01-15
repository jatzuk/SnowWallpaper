package dev.jatzuk.snowwallpaper.views.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.viewpager2.widget.ViewPager2
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.util.AppBarTitleViewModel
import dev.jatzuk.snowwallpaper.views.imagepicker.*

private const val NUM_PAGES = 5

class PreferencesActivity :
    AppCompatActivity()/*FragmentActivity()*/,
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    BackgroundImagesFragment.OnListFragmentInteractionListener {

    private lateinit var appBarTitleViewModel: AppBarTitleViewModel
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.preferences_container, PreferencesFragment())
            .commit()

        appBarTitleViewModel = ViewModelProviders.of(this).get(AppBarTitleViewModel::class.java)
        appBarTitleViewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })

//        actionBar?.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.fragment_image_viewer)
//        viewPager = findViewById(R.id.pager)
//        val pagerAdapter = ScreenSlidePagerAdapter(this)
//        viewPager.apply {
//            adapter = pagerAdapter
//            setPageTransformer(ZoomOutPageTransformer())
//        }
    }

//    inner class ScreenSlidePagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
//        override fun getItemCount(): Int {
//            return NUM_PAGES
//        }
//
//        override fun createFragment(position: Int): Fragment {
//            return ScreenSlidePageFragment()
//        }
//    }

//    override fun onBackPressed() {
//        if (viewPager.currentItem == 0)
//            super.onBackPressed()
//        else viewPager.currentItem = viewPager.currentItem - 1
//    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        )
        fragment.apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.preferences_container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    override fun onListFragmentInteraction(item: BackgroundImage) {
//        supportFragmentManager.popBackStack()
//        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.preferences_container,
                ImageViewerFragment.newInstance(R.drawable.background_image)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "PreferencesActivity"
    }
}
