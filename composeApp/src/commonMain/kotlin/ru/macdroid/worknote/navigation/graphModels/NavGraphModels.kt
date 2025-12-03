package ru.macdroid.worknote.navigation.graphModels

import kotlinx.serialization.Serializable

sealed class RootGraph() {
    @Serializable data object Chat : RootGraph()
    @Serializable data object Weather : RootGraph()
    @Serializable data object Talk : RootGraph()
}

sealed class ChatHost() {
    @Serializable data object Chat : ChatHost()
}


sealed class WeatherHost() {
    @Serializable data object Weather : WeatherHost()
}

sealed class TalkHost() {
    @Serializable data object Talk : TalkHost()
}

