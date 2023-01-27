package com.beduir.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_button)
        val shareButton = findViewById<TextView>(R.id.share)
        val supportButton = findViewById<TextView>(R.id.support)
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        var darkThemeSwitch = findViewById<Switch>(R.id.dark_theme)

        darkThemeSwitch.isChecked = getDarkThemeState()

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.share_url));
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this.getString(R.string.support_mail)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.support_mail_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.support_mail_body))
            startActivity(shareIntent)
        }

        userAgreementButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW)
            shareIntent.data = Uri.parse(this.getString(R.string.user_agreement_url))
            startActivity(shareIntent)
        }

        darkThemeSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }
    }

    private fun getDarkThemeState(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_YES == Configuration.UI_MODE_NIGHT_YES
    }
}