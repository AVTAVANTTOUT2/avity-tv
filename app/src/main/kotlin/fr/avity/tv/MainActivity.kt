package fr.avity.tv

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

private const val HOME_URL = "https://tv.avity.fr"
private const val USER_AGENT = "AvityTV/1.0"
private const val SCALE_PERCENT = 60

class MainActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUi()

        webView = findViewById<View>(R.id.webview) as WebView
        webView.configureSettings()
        webView.webViewClient = AvityWebViewClient()
        webView.webChromeClient = AvityWebChromeClient()

        if (savedInstanceState == null) {
            webView.loadUrl(HOME_URL)
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUi()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_BACK -> handleBackNavigation()
            KeyEvent.KEYCODE_MENU -> {
                webView.reload()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.onPause()
        webView.destroy()
        super.onDestroy()
    }

    private fun handleBackNavigation(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            moveTaskToBack(true)
            true
        }
    }

    private fun hideSystemUi() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.configureSettings() {
        val prefs = CookieManager.getInstance()
        prefs.setAcceptThirdPartyCookies(this, true)

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            userAgentString = "$userAgentString $USER_AGENT"
            mediaPlaybackRequiresUserGesture = false
            allowContentAccess = true
            allowFileAccess = false
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        setInitialScale(SCALE_PERCENT)

        isFocusableInTouchMode = true
        isFocusable = true
        requestFocus()
    }

    private inner class AvityWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            view.setInitialScale(SCALE_PERCENT)
            view.evaluateJavascript(
                "document.querySelector('meta[name=viewport]')?.setAttribute('content'," +
                    "'width=device-width, initial-scale=0.6');" +
                    "document.body.style.zoom='0.6';",
                null
            )
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            val errorHtml = buildErrorPage(
                title = "Erreur de connexion",
                message = "Impossible de charger $failingUrl\n\n$description"
            )
            view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null)
        }
    }

    private inner class AvityWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            // Loading progress indicator — reserved for future use
        }
    }

    private fun buildErrorPage(title: String, message: String): String {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$title</title>
                <style>
                    body {
                        background: #000;
                        color: #fff;
                        font-family: -apple-system, Roboto, sans-serif;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        height: 100vh;
                        margin: 0;
                        text-align: center;
                    }
                    .container { max-width: 600px; padding: 40px; }
                    h1 { font-size: 28px; margin-bottom: 16px; }
                    p { font-size: 16px; color: #aaa; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>$title</h1>
                    <p>${message.replace("\n", "<br>")}</p>
                    <p>Appuyez sur <strong>MENU</strong> pour r&eacute;essayer.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
