package com.beduir.playlistmaker.di

import com.beduir.playlistmaker.player.data.PlayerImpl
import com.beduir.playlistmaker.player.domain.Player
import com.beduir.playlistmaker.search.data.NetworkClient
import com.beduir.playlistmaker.search.data.TrackHistoryImpl
import com.beduir.playlistmaker.search.data.network.RetrofitNetworkClient
import com.beduir.playlistmaker.search.domain.TrackHistory
import com.beduir.playlistmaker.settings.data.SettingsStorageImpl
import com.beduir.playlistmaker.settings.domain.SettingsStorage
import com.beduir.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.beduir.playlistmaker.sharing.domain.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
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
    single<SettingsStorage> {
        SettingsStorageImpl(androidContext())
    }
    single<TrackHistory> {
        TrackHistoryImpl(androidContext(), get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(androidContext())
    }
}