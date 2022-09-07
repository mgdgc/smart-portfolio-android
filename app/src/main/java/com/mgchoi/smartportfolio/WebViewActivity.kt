package com.mgchoi.smartportfolio

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URL = "url"

        fun openAsCustomTab(context: Context, url: String) {
            Uri.parse(url)?.let {
                // Set theme
                val colorScheme = CustomTabColorSchemeParams.Builder()
                colorScheme.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary))
                colorScheme.setNavigationBarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_primary_variant
                    )
                )
                colorScheme.setSecondaryToolbarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_secondary
                    )
                )

                // Chrome Custom Tabs builder
                val builder = CustomTabsIntent.Builder()
                builder.setDefaultColorSchemeParams(colorScheme.build())
                // Launch intent
                val intent = builder.build()
                intent.launchUrl(context, it)
            }
        }
    }

    private lateinit var binding: ActivityWebViewBinding
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar settings
        setSupportActionBar(binding.toolbarWeb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // Initializations
        initData()
        initWebView()
    }

    private fun initData() {
        val url = intent?.getStringExtra(EXTRA_URL)

        if (url != null) {
            this.url = url
            supportActionBar?.title = url
        } else {
            Snackbar.make(binding.webView, R.string.web_url_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close) {
                    this.finish()
                }
                .show()
        }
    }

    private fun initWebView() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        url?.let {
            binding.webView.loadUrl(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_web, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            R.id.action_copy -> {
                true
            }
            R.id.action_open_web -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else super.onBackPressed()
    }
}