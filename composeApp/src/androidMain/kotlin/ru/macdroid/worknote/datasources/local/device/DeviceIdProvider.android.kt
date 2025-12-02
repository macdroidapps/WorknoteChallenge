package ru.macdroid.worknote.datasources.local.device

import android.content.Context
import android.provider.Settings

/**
 * Android реализация получения Device ID
 * Использует Settings.Secure.ANDROID_ID
 */
actual class DeviceIdProvider(private val context: Context) {
    actual fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "UnknownAndroidDevice"
        } catch (e: Exception) {
            "ErrorAndroidDevice"
        }
    }

    actual fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    actual fun getFormattedDeviceId(): String {
        return "${getDeviceId()}_${getAppVersion()}"
    }
}


