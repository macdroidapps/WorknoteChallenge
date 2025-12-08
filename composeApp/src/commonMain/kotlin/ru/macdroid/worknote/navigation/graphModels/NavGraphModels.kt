package ru.macdroid.worknote.navigation.graphModels

import kotlinx.serialization.Serializable

sealed class RootGraph() {
    @Serializable data object Chat : RootGraph()
}

sealed class ChatHost() {
    @Serializable data object Chat : ChatHost()
}