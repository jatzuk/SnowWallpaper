package dev.jatzuk.snowwallpaper.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.utilities.Logger.d
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class TexturesViewModel : ViewModel() {

    var textures: MutableLiveData<TextureCache>? = MutableLiveData(TextureCache.getInstance())

    fun updateTexture(textureType: TextureProvider.TextureType) {
        val textureCache = TextureCache.getInstance()
        textures?.value?.set(textureType, textureCache[textureType])
        textures?.postValue(textureCache)
    }

    override fun onCleared() {
        super.onCleared()
        textures = null
        d("view model on cleared called", "TexturesViewModel")
    }
}
