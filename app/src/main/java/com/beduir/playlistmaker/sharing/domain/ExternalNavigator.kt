package com.beduir.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(email: String)
}