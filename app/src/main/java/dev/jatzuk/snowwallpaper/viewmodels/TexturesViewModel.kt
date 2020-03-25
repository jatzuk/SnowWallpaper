package dev.jatzuk.snowwallpaper.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jatzuk.snowwallpaper.utilities.ImageProvider

class TexturesViewModel : ViewModel() {

    private val textures: MutableLiveData<HashMap<ImageProvider.ImageType, Bitmap?>> by lazy {
//        MutableLiveData<HashMap<ImageProvider.ImageType, Bitmap?>>().also { loadTextures() }
        loadTextures()
    }

    fun getTextures(): LiveData<HashMap<ImageProvider.ImageType, Bitmap?>> = textures

    //    todo
    private fun loadTextures(): MutableLiveData<HashMap<ImageProvider.ImageType, Bitmap?>> {
        val snowfallTexture =
            ImageProvider.getBitmapFromCache(ImageProvider.ImageType.SNOWFALL_TEXTURE)
        val snowflakeTexture =
            ImageProvider.getBitmapFromCache(ImageProvider.ImageType.SNOWFLAKE_TEXTURE)
        val backgroundImageTexture =
            ImageProvider.getBitmapFromCache(ImageProvider.ImageType.BACKGROUND_IMAGE)

        val map = HashMap<ImageProvider.ImageType, Bitmap?>()
        map[ImageProvider.ImageType.SNOWFALL_TEXTURE] = snowfallTexture
        map[ImageProvider.ImageType.SNOWFLAKE_TEXTURE] = snowflakeTexture
        map[ImageProvider.ImageType.BACKGROUND_IMAGE] = backgroundImageTexture
        return MutableLiveData(map)
    }
}
