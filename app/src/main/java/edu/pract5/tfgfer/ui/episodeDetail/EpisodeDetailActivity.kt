package edu.pract5.tfgfer.ui.episodeDetail

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.AnimeApp
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import kotlinx.coroutines.launch

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var episodeUrl: String
    private lateinit var episodeSlug: String
    private lateinit var episodeNumber: String
    private lateinit var animeSlug: String

    private lateinit var webView: WebView
    private lateinit var titleTextView: TextView
    private lateinit var serverTabsLayout: LinearLayout
    private lateinit var videoContainer: FrameLayout

    private lateinit var vm: EpisodeDetailViewModel

    private var mCustomView: View? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var mOriginalOrientation: Int = 0
    private var mOriginalSystemUiVisibility: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Vistas
        titleTextView = findViewById(R.id.episode_title)
        webView = findViewById(R.id.webViewPlayer)
        serverTabsLayout = findViewById(R.id.serverTabs)
        videoContainer = findViewById(R.id.videoContainer)

        // Intent extras
        episodeUrl = intent.getStringExtra("episode_data") ?: ""
        animeSlug = intent.getStringExtra("anime_slug") ?: ""

        Log.d("EpisodeDetail", "episodeUrl: $episodeUrl")
        Log.d("EpisodeDetail", "animeSlug: $animeSlug")

        extractEpisodeDetails()

        Log.d("EpisodeDetail", "episodeSlug: $episodeSlug")
        Log.d("EpisodeDetail", "episodeNumber: $episodeNumber")

        val repository = AnimeApp.getRepository(application)
        vm = ViewModelProvider(
            this,
            EpisodeDetailViewModelFactory(repository, episodeSlug, episodeNumber)
        )[EpisodeDetailViewModel::class.java]

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        titleTextView.setOnClickListener {
            val intent = Intent(this, AnimeDetailActivity::class.java)
            intent.putExtra("anime_slug", episodeSlug)
            startActivity(intent)
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (mCustomView != null) {
                    onHideCustomView()
                    return
                }
                mCustomView = view
                mOriginalSystemUiVisibility = window.decorView.systemUiVisibility
                mOriginalOrientation = requestedOrientation
                mCustomViewCallback = callback

                val decor = window.decorView as FrameLayout
                decor.addView(mCustomView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ))

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false)
                    val controller = window.insetsController
                    controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller?.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            )
                }

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onHideCustomView() {
                val decor = window.decorView as FrameLayout
                decor.removeView(mCustomView)
                mCustomView = null
                window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
                requestedOrientation = mOriginalOrientation
                mCustomViewCallback?.onCustomViewHidden()
                mCustomViewCallback = null
            }
        }

        // Observar datos del ViewModel
        lifecycleScope.launch {
            vm.episodeDetail.collect { episode ->
                Log.d("EpisodeDetail", "episodeDetail response: $episode")

                titleTextView.text = "${episode.data.title} - Episodio ${episode.data.number}"
                serverTabsLayout.removeAllViews()

                episode.data.servers.forEach { server ->
                    Log.d("EpisodeDetail", "Adding server button: ${server.name}, embed: ${server.embed}")

                    val serverButton = Button(this@EpisodeDetailActivity).apply {
                        text = server.name
                        setOnClickListener { loadServerInWebView(server.embed) }
                    }
                    serverTabsLayout.addView(serverButton)
                }

                episode.data.servers.firstOrNull()?.let {
                    loadServerInWebView(it.embed)
                }
            }
        }
    }

    private fun extractEpisodeDetails() {
        val regex = "https://www3.animeflv.net/ver/([a-zA-Z0-9-]+)-(\\d+)".toRegex()
        val matchResult = regex.find(episodeUrl)
        if (matchResult != null) {
            episodeSlug = matchResult.groupValues[1]
            episodeNumber = matchResult.groupValues[2]
            Log.d("EpisodeDetail", "extractEpisodeDetails match: slug=$episodeSlug, number=$episodeNumber")
        } else {
            episodeSlug = ""
            episodeNumber = ""
            Log.w("EpisodeDetail", "extractEpisodeDetails failed to parse episodeUrl: $episodeUrl")
        }
    }

    private fun loadServerInWebView(embedUrl: String) {
        Log.d("EpisodeDetail", "Loading embed URL in WebView: $embedUrl")

        val iframeHtml = """
            <html>
            <body style="margin:0;padding:0;background:black;">
                <iframe width="100%" height="100%" 
                    src="$embedUrl"
                    frameborder="0" allowfullscreen allow="autoplay"></iframe>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL(null, iframeHtml, "text/html", "UTF-8", null)
    }

    override fun onBackPressed() {
        if (mCustomView != null) {
            webView.webChromeClient?.onHideCustomView()
        } else {
            super.onBackPressed()
        }
    }
}

