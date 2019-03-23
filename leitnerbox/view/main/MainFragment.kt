package com.kecsot.leitnerbox.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.leitnerbox.R
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : AbstractFragment() {

    private val PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=TRPC7WV3UGNYS&source=url"
    private val GOOGLEPLAY_URL = "https://play.google.com/store/apps/details?id=com.kecsot.leitnerbox2"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        showActionBar()
    }

    override fun onInitView() {
        setTitle(R.string.app_name)
        setBackButtonVisible(false)
    }

    override fun onSubscribeReactiveXObservables() {
        fragment_main_decks_button
            .clicks()
            .subscribe {
                navigateToDeckList()
            }
            .addToCompositeDisposable()

        fragment_main_help_button
            .clicks()
            .subscribe {
                navegateToHelp()
            }
            .addToCompositeDisposable()

        fragment_main_support_button
            .clicks()
            .subscribe {
                navigateToSupportUs()
            }
            .addToCompositeDisposable()

        fragment_main_rateus_button
            .clicks()
            .subscribe {
                navigateToRateUs()
            }
            .addToCompositeDisposable()
    }

    private fun navigateToDeckList() {
        val direction = MainFragmentDirections.actionMainFragmentToDeckListFragment()
        navigation.navigate(direction)
    }

    private fun navegateToHelp() {
        val direction = MainFragmentDirections.actionMainFragmentToHelpFragment()
        navigation.navigate(direction)
    }

    private fun navigateToSupportUs() {
        openUrl(PAYPAL_URL)
    }

    private fun navigateToRateUs() {
        openUrl(GOOGLEPLAY_URL)
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}