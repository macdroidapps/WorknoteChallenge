package ru.macdroid.worknote.features.s05_e01_chat.domain.repositories


interface ChatStorage {
    fun saveUserLogin(login: String)
}