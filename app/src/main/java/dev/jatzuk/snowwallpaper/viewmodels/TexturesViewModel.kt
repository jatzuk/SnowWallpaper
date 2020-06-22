package dev.jatzuk.snowwallpaper.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class TexturesViewModel : ViewModel() {

    val textures = MutableLiveData<TextureCache>(TextureCache.getInstance())

    fun updateTexture(textureType: TextureProvider.TextureType) {
        val textureCache = TextureCache.getInstance()
        textures.value?.set(textureType, textureCache[textureType])
        textures.postValue(textureCache)
    }
}
