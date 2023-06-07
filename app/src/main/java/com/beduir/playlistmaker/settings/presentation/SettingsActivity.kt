package com.beduir.playlistmaker.settings.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beduir.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()
    private val router: SettingsRouter by lazy { SettingsRouter(this) }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_button)
        val shareButton = findViewById<TextView>(R.id.share)
        val supportButton = findViewById<TextView>(R.id.support)
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        val darkThemeSwitch = findViewById<Switch>(R.id.dark_theme)

        viewModel.observeDarkTheme().observe(this) {
            darkThemeSwitch.isChecked = it
        }

        backButton.setOnClickListener {
            router.goBack()
        }

        shareButton.setOnClickListener {
            viewModel.onShareButtonClicked()
        }

        supportButton.setOnClickListener {
            viewModel.onSupportButtonClicked()
        }

        userAgreementButton.setOnClickListener {
            viewModel.onUserAgreementButtonClicked()
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.onDarkThemeSwitchClicked(checked)
        }
    }
}