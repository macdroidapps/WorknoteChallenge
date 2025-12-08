package ru.macdroid.worknote.features.chat.domain.repositories


interface ChatStorage {
    fun saveUserLogin(login: String)
}