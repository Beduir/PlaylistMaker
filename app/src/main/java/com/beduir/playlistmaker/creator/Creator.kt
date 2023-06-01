package com.beduir.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.player.data.PlayerImpl
import com.beduir.playlistmaker.player.domain.PlayerInteractor
import com.beduir.playlistmaker.player.domain.PlayerInteractorImpl
import com.beduir.playlistmaker.search.data.SearchRepositoryImpl
import com.beduir.playlistmaker.search.data.TrackHistoryImpl
import com.beduir.playlistmaker.search.data.network.RetrofitNetworkClient
import com.beduir.playlistmaker.search.domain.SearchInteractor
import com.beduir.playlistmaker.search.domain.SearchInteractorImpl
import com.beduir.playlistmaker.search.presentation.SearchActivity
import com.beduir.playlistmaker.settings.data.SettingsRepositoryImpl
import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import com.beduir.playlistmaker.settings.domain.SettingsRepository
import com.beduir.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.beduir.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.beduir.playlistmaker.sharing.domain.ExternalNavigator
import com.beduir.playlistmaker.sharing.domain.SharingInteractor
import com.beduir.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.gson.Gson

object Creator {
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            trackHistory = getTrackHistory(context),
            repository = SearchRepositoryImpl(RetrofitNetworkClient(context))
        )
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerImpl())
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = getExternalNavigator(context)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(getSettingsSharedPrefs(context))
    }

    private fun getSettingsSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(App.SETTINGS_PREFERENCES, Application.MODE_PRIVATE)
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun getTrackHistory(context: Context): TrackHistoryImpl {
        return TrackHistoryImpl(getSearchSharedPrefs(context), Gson())
    }

    private fun getSearchSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SearchActivity.SEARCH_PREFERENCES, Application.MODE_PRIVATE
        )
    }
}