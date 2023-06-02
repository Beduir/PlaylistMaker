package com.beduir.playlistmaker.sharing.domain.impl

import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.sharing.domain.ExternalNavigator
import com.beduir.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return App.getInstance().getString(R.string.share_url)
    }

    private fun getSupportEmailData(): String {
        return App.getInstance().getString(R.string.support_mail)
    }

    private fun getTermsLink(): String {
        return App.getInstance().getString(R.string.user_agreement_url)
    }
}