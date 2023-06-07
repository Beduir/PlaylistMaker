package com.beduir.playlistmaker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.beduir.playlistmaker.di.dataModule
import com.beduir.playlistmaker.di.interactorModule
import com.beduir.playlistmaker.di.repositoryModule
import com.beduir.playlistmaker.di.viewModelModule
import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {
    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val settingsInteractor: SettingsInteractor = get()
        switchTheme(settingsInteractor.getThemeSettings().darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}