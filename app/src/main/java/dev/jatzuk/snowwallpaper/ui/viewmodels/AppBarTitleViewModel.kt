package dev.jatzuk.snowwallpaper.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppBarTitleViewModel : ViewModel() {

    val title = MutableLiveData<String>()
}
