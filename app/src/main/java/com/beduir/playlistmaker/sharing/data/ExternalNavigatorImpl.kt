package com.beduir.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(openIntent)
    }

    override fun openEmail(email: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(
                Intent.EXTRA_SUBJECT,
                App.getInstance().getString(R.string.support_mail_subject)
            )
            putExtra(Intent.EXTRA_TEXT, App.getInstance().getString(R.string.support_mail_body))
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(emailIntent)
    }
}