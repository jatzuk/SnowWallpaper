package dev.jatzuk.snowwallpaper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.utilities.ImageProvider
import dev.jatzuk.snowwallpaper.utilities.TextureCache

class TexturesViewModel : ViewModel() {

    private var textures: MutableLiveData<TextureCache>? = null

    fun getTextures(): LiveData<TextureCache> = synchronized(this) {
        return textures ?: MutableLiveData(ImageProvider.getTextureCache()).also { textures = it }
    }
}
