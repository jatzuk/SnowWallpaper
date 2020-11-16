package dev.jatzuk.snowwallpaper

import android.app.Application
import dev.jatzuk.snowwallpaper.utilities.Logger

class SnowWallpaperApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Logger.initFirebaseAnalytics()
    }
}
