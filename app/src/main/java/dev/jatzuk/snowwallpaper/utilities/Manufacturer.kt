package dev.jatzuk.snowwallpaper.utilities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog
import dev.jatzuk.snowwallpaper.utilities.Logger.logging
import java.util.*

class Manufacturer private constructor(
    val name: String
) {

    var packageNames = arrayListOf<String>()
    var classNames = arrayListOf<String>()

    companion object {
        private const val TAG = "Manufacturer"

        private fun detectDeviceManufacturer(): Manufacturer? {
            logging("Trying to detect device manufacturer", TAG)
            return Manufacturer(Build.MANUFACTURER.toLowerCase(Locale.getDefault())).apply {
                when (name) {
                    "xiaomi" -> {
                        packageNames.add("com.miui.powerkeeper")
                        classNames.add("com.miui.powerkeeper.ui.HiddenAppsConfigActivity")
                    }
                    "letv" -> {
                        packageNames.add("com.letv.android.letvsafe")
                        classNames.add("com.letv.android.letvsafe.AutobootManageActivity")
                    }
                    "huawei" -> {
                        packageNames.add("com.huawei.systemmanager")
                        classNames.add("com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")

                        packageNames.add("com.huawei.systemmanager")
                        classNames.add("com.huawei.systemmanager.optimize.process.ProtectActivity")

                        packageNames.add("com.huawei.systemmanager")
                        classNames.add("com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")
                    }
                    "coloros" -> {
                        packageNames.add("com.coloros.safecenter")
                        classNames.add("com.coloros.safecenter.permission.startup.StartupAppListActivity")

                        packageNames.add("com.coloros.safecenter")
                        classNames.add("com.coloros.safecenter.startupapp.StartupAppListActivity")
                    }
                    "oppo" -> {
                        packageNames.add("com.oppo.safe")
                        classNames.add("com.oppo.safe.permission.startup.StartupAppListActivity")
                    }
                    "iqoo" -> {
                        packageNames.add("com.iqoo.secure")
                        classNames.add("com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")

                        packageNames.add("com.iqoo.secure")
                        classNames.add("com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")
                    }
                    "vivo" -> {
                        packageNames.add("com.vivo.permissionmanager")
                        classNames.add("com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
                    }
                    "samsung" -> {
                        packageNames.add("com.samsung.android.lool")
                        classNames.add("com.samsung.android.sm.ui.battery.BatteryActivity")
                    }
                    "htc" -> {
                        packageNames.add("com.htc.pitroad")
                        classNames.add("com.htc.pitroad.landingpage.activity.LandingPageActivity")
                    }
                    "asus" -> {
                        packageNames.add("com.asus.mobilemanager")
                        classNames.add("com.asus.mobilemanager.MainActivity")
                    }
                }
            }
        }

        fun tryOpenManufacturerBackgroundRestrictionSettings(context: Context) {
            val manufacturer = detectDeviceManufacturer()
            if (manufacturer == null) {
                logging("Device manufacturer settings app not detected", TAG)
                return
            }

            logging(
                "Device manufacturer presented in app settings, " +
                        "trying to start activity: ${manufacturer.packageNames}, " +
                        manufacturer.classNames,
                TAG
            )
            manufacturer.packageNames.size.let { size ->
                repeat(size) { i ->
                    val intent = Intent().apply {
                        component = ComponentName(
                            manufacturer.packageNames[i],
                            manufacturer.classNames[i]
                        )
                        putExtra("package_name", context.packageName)
                        putExtra("package_label", context.getText(R.string.app_name))
                    }

                    if (context.packageManager.resolveActivity(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        ) != null
                    ) {
                        logging(
                            "Starting activity ${manufacturer.packageNames[i]}, " +
                                    manufacturer.classNames[i], TAG
                        )
                        context.startActivity(intent)
                        return@let
                    }
                }

                logging("Matching activity not found", TAG)
            }
        }
    }
}

/*
"com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"
    "com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"

    "com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
    "com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"
    "com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"

    "com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"
    "com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"

    "com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"

    "com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
    "com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"

    "com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"

    "com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"

    "com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity"

    "com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity

* */
