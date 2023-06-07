package com.beduir.playlistmaker.di

import com.beduir.playlistmaker.player.domain.PlayerInteractor
import com.beduir.playlistmaker.player.domain.PlayerInteractorImpl
import com.beduir.playlistmaker.search.domain.SearchInteractor
import com.beduir.playlistmaker.search.domain.SearchInteractorImpl
import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import com.beduir.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.beduir.playlistmaker.sharing.domain.SharingInteractor
import com.beduir.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }
}