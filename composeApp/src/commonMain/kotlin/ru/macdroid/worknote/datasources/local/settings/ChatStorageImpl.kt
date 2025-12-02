package ru.macdroid.worknote.datasources.local.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ru.macdroid.worknote.features.s05_e01_chat.domain.repositories.ChatStorage

class ChatStorageImpl(
    private val settings: Settings
) : ChatStorage {

    companion object Companion {
        private const val KEY_USER_LOGIN = "user_login"
    }

    override fun saveUserLogin(login: String) {
        settings[KEY_USER_LOGIN] = login
    }
}

