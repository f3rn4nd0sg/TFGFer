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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import edu.pract5.tfgfer.AnimeApp
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var webView: WebView
    private lateinit var serverTabsLayout: LinearLayout
    private lateinit var videoContainer: FrameLayout

    private lateinit var episodeUrl: String
    private lateinit var episodeSlug: String
    private lateinit var episodeNumber: String
    private lateinit var animeSlug: String

    private var mCustomView: View? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var mOriginalSystemUiVisibility = 0
    private var mOriginalOrientation = 0

    private val vm: EpisodeDetailViewModel by viewModels {
        val repository = AnimeApp.getRepository(application)
        EpisodeDetailViewModelFactory(repository, animeSlug, episodeNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Inicializar vistas
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

        // Configurar toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Al hacer clic en el título, ir a la página del anime
        titleTextView.setOnClickListener {
            if (animeSlug.isNotEmpty()) {
                val intent = Intent(this@EpisodeDetailActivity, AnimeDetailActivity::class.java).apply {
                    putExtra("anime_slug", animeSlug)
                }
                startActivity(intent)
            }
        }

        // Configurar WebView
        setupWebView()

        // Observar datos del episodio
        lifecycleScope.launch {
            vm.episodeDetail.collectLatest { episode ->
                if (episode.success) {
                    // Mostrar título del episodio
                    titleTextView.text = "${episode.data.title} - Episodio ${episode.data.number}"

                    // Limpiar los botones anteriores
                    serverTabsLayout.removeAllViews()

                    // Registrar la cantidad de servidores disponibles
                    Log.d("EpisodeDetailActivity", "Servidores originales: ${episode.data.servers.size}")

                    // Crear botones para cada servidor de la API original
                    episode.data.servers.forEachIndexed { index, server ->
                        val button = Button(this@EpisodeDetailActivity).apply {
                            text = server.name
                            setOnClickListener {
                                loadServerInWebView(server.embed)
                            }
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                marginEnd = 8
                            }
                        }
                        serverTabsLayout.addView(button)
                    }

                    // Si hay servidores disponibles, cargar el primero automáticamente
                    if (episode.data.servers.isNotEmpty()) {
                        loadServerInWebView(episode.data.servers[0].embed)
                    }
                }
            }
        }
    }

    private fun setupWebView() {
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

                val decor = window.decorView as FrameLayout
                decor.addView(mCustomView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ))

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false)
                    val controller = window.insetsController
                    controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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
    }

    private fun loadServerInWebView(embedUrl: String) {
        Log.d("EpisodeDetailActivity", "Cargando servidor: $embedUrl")
        webView.loadUrl(embedUrl)
    }

    private fun extractEpisodeDetails() {
        // Extraer número de episodio y slug desde la URL del episodio
        // Formato típico: /ver/one-piece-tv-1
        val parts = episodeUrl.split("/").lastOrNull()?.split("-") ?: listOf()

        if (parts.isNotEmpty()) {
            episodeNumber = parts.lastOrNull() ?: "1"

            // Si no hay slug específico del anime, usar el proporcionado
            if (animeSlug.isEmpty()) {
                // Reconstruir el slug sin el número de episodio
                episodeSlug = parts.dropLast(1).joinToString("-")
                animeSlug = episodeSlug
            } else {
                episodeSlug = animeSlug
            }
        } else {
            episodeNumber = "1"
            episodeSlug = animeSlug
        }
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}

