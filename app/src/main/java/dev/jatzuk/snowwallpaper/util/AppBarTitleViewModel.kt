package dev.jatzuk.snowwallpaper.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppBarTitleViewModel : ViewModel() {
    val title = MutableLiveData<String>()
}
