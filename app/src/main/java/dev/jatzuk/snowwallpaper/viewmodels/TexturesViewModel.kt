package dev.jatzuk.snowwallpaper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache

class TexturesViewModel : ViewModel() {

    private var textures: MutableLiveData<TextureCache>? = null

    fun getTextures(textureCache: TextureCache): LiveData<TextureCache> = synchronized(this) {
        return textures ?: MutableLiveData(textureCache).also { textures = it }
    }
}
