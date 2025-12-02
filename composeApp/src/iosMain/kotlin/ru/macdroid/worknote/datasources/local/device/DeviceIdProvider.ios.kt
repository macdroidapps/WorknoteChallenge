package ru.macdroid.worknote.datasources.local.device

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUUID
import platform.UIKit.UIDevice

/**
 * iOS реализация получения Device ID
 * Использует UIDevice.identifierForVendor или генерирует и сохраняет UUID
 */
actual class DeviceIdProvider {
    
    private companion object {
        const val DEVICE_ID_KEY = "device_id_storage_key"
    }
    
    actual fun getDeviceId(): String {
        val userDefaults = NSUserDefaults.standardUserDefaults
        
        // Проверяем сохранённый ID
        val savedId = userDefaults.stringForKey(DEVICE_ID_KEY)
        if (savedId != null) {
            return savedId
        }
        
        // Пробуем получить identifierForVendor
        val vendorId = UIDevice.currentDevice.identifierForVendor?.UUIDString
        if (vendorId != null) {
            userDefaults.setObject(vendorId, DEVICE_ID_KEY)
            return vendorId
        }
        
        // Генерируем новый UUID
        val newId = NSUUID().UUIDString
        userDefaults.setObject(newId, DEVICE_ID_KEY)
        return newId
    }

    actual fun getAppVersion(): String {
        val infoDict = NSUserDefaults.standardUserDefaults
        val appVersion = infoDict.stringForKey("CFBundleShortVersionString") ?: "1.0.0"
        return appVersion
    }

    actual fun getFormattedDeviceId(): String {
        return "${getDeviceId()}_${getAppVersion()}"
    }
}

