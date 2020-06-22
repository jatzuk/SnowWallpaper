package dev.jatzuk.snowwallpaper.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.data.preferences.TextureCache
import dev.jatzuk.snowwallpaper.utilities.TextureProvider

class TexturesViewModel : ViewModel() {

    val textures = MutableLiveData<HashMap<TextureProvider.TextureType, Bitmap?>>()
        get() {
            field.value = TextureCache.getInstance().hashMap
            return field
        }

    fun updateTexture(textureType: TextureProvider.TextureType) {
        textures.value?.set(textureType, TextureCache.getInstance()[textureType])
    }
}
