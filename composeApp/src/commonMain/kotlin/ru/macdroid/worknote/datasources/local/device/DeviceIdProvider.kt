package ru.macdroid.worknote.datasources.local.device

/**
 * Провайдер для получения уникального идентификатора устройства
 * Работает на всех платформах (Android, iOS, Desktop, Web)
 */
expect class DeviceIdProvider {
    /**
     * Получить уникальный идентификатор устройства
     * 
     * Android: Settings.Secure.ANDROID_ID
     * iOS: identifierForVendor или сохранённый UUID
     */
    fun getDeviceId(): String
    fun getAppVersion(): String
    fun getFormattedDeviceId(): String
}


