package com.kecsot.leitnerbox.view.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.leitnerbox.R
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment : AbstractFragment() {

    private val ASSET_FILE_PATH_PREFIX = "file:///android_asset/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onInitView() {
        setTitle(R.string.fragment_help_title)

        fragment_help_webview.run {
            settings.apply {
                loadsImagesAutomatically = true
                domStorageEnabled = true
                setSupportZoom(false)
                allowFileAccess = true
                setAppCacheEnabled(true)
            }
            loadUrl(this@HelpFragment.getUrl())
        }
    }

    override fun onSubscribeReactiveXObservables() {
    }

    override fun onSubscribeViewModelObservables() {
    }

    private fun getUrl(): String {
        return ASSET_FILE_PATH_PREFIX + getString(R.string.file_html_helper)
    }

}