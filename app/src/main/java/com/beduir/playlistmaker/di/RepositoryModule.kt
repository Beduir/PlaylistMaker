package com.beduir.playlistmaker.di

import com.beduir.playlistmaker.search.data.SearchRepositoryImpl
import com.beduir.playlistmaker.search.domain.SearchRepository
import com.beduir.playlistmaker.settings.data.SettingsRepositoryImpl
import com.beduir.playlistmaker.settings.domain.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("settingsSharedPrefs")))
    }
    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }
}