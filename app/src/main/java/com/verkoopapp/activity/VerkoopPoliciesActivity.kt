package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.private_policy_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class VerkoopPoliciesActivity: AppCompatActivity() {
    private var comingFrom: Int = 0
    private var webURl: String? = null
    private var errorMssg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.private_policy_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setData()

    }

    private fun setData() {
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        when (comingFrom) {
            1 -> {
                tvHeaderLoc.text = getString(R.string.about_carousell)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/about"
            }
            2 -> {
                tvHeaderLoc.text = getString(R.string.term_of_services)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/termsOfConditions"
            }
            3 -> {
                tvHeaderLoc.text = getString(R.string.privacy_policy)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/privacyPolicy"
            }
            4 -> {
                tvHeaderLoc.text = getString(R.string.contact_us)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/contactUs"
            }
            5 -> {
                tvHeaderLoc.text = getString(R.string.help_center)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/helpCenter"
            }
            6 -> {
                tvHeaderLoc.text = getString(R.string.date_privacy_settings)
                webURl = "http://verkoopadmin.com/VerkoopApp/app/privacySettings"
            }
        }
        if (Utils.isOnline(this)) {
            pbProgress.visibility = View.VISIBLE
            setWebView()
        } else {
            pbProgress.visibility = View.GONE
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setWebView() {
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.javaScriptEnabled=true
        webURl?.let { webView.loadUrl(it) }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                pbProgress.visibility = View.VISIBLE
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                pbProgress.visibility = View.GONE
            }
        }

    }
}