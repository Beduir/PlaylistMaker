package com.beduir.playlistmaker.di

import android.app.Application
import android.content.SharedPreferences
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.player.data.PlayerImpl
import com.beduir.playlistmaker.player.domain.Player
import com.beduir.playlistmaker.search.data.NetworkClient
import com.beduir.playlistmaker.search.data.TrackHistoryImpl
import com.beduir.playlistmaker.search.data.network.RetrofitNetworkClient
import com.beduir.playlistmaker.search.domain.TrackHistory
import com.beduir.playlistmaker.search.presentation.SearchActivity
import com.beduir.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.beduir.playlistmaker.sharing.domain.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<Player> {
        PlayerImpl()
    }
    single<Gson> {
        Gson()
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
    single<SharedPreferences>(named("settingsSharedPrefs")) {
        androidContext().getSharedPreferences(App.SETTINGS_PREFERENCES, Application.MODE_PRIVATE)
    }
    single<SharedPreferences>(named("historySharedPrefs")) {
        androidContext().getSharedPreferences(
            SearchActivity.SEARCH_PREFERENCES,
            Application.MODE_PRIVATE
        )
    }
    single<TrackHistory> {
        TrackHistoryImpl(get(named("historySharedPrefs")), get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(androidContext())
    }
}