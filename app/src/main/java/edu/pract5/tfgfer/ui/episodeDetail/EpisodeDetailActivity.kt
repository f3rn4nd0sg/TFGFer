package edu.pract5.tfgfer.ui.episodeDetail

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.data.RemoteDataSource
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import kotlinx.coroutines.launch

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var episodeUrl: String
    private lateinit var episodeSlug: String
    private lateinit var episodeNumber: String
    private lateinit var webView: WebView
    private lateinit var titleTextView: TextView
    private lateinit var serverTabsLayout: LinearLayout
    private lateinit var animeSlug: String
    private lateinit var videoContainer: FrameLayout

    // Variables para manejar la vista personalizada
    private var mCustomView: View? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var mOriginalOrientation: Int = 0
    private var mOriginalSystemUiVisibility: Int = 0

    private val vm: EpisodeDetailViewModel by viewModels {
        EpisodeDetailViewModelFactory(Repository(RemoteDataSource()), episodeSlug, episodeNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Inicializar las vistas
        titleTextView = findViewById(R.id.episode_title)
        webView = findViewById(R.id.webViewPlayer)
        serverTabsLayout = findViewById(R.id.serverTabs)
        videoContainer = findViewById(R.id.videoContainer)

        // Obtener la URL del episodio desde el intent
        episodeUrl = intent.getStringExtra("episode_data") ?: ""
        animeSlug = intent.getStringExtra("anime_slug") ?: ""

        // Extraer el número del episodio y el slug desde la URL
        extractEpisodeDetails()
        Log.d("EpisodeDetailActivity", "Episode Slug: $episodeSlug")

        // Agregar el OnClickListener al título del episodio
        titleTextView.setOnClickListener {
            val intent = Intent(this, AnimeDetailActivity::class.java)
            Log.d("AnimeDetailActivity", "Slug: $animeSlug")
            intent.putExtra("anime_slug", episodeSlug)  // Pasar el anime_slug
            startActivity(intent)
        }

        // Configurar WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = WebViewClient()

        // Configurar WebChromeClient para gestionar la pantalla completa correctamente
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

                // Añadir la vista personalizada al layout
                val decor = window.decorView as FrameLayout
                decor.addView(mCustomView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT))

                // Configurar orientación y banderas UI
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onHideCustomView() {
                // Restaurar vista original
                val decor = window.decorView as FrameLayout
                decor.removeView(mCustomView)
                mCustomView = null

                // Restaurar orientación y banderas UI
                window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
                requestedOrientation = mOriginalOrientation
                mCustomViewCallback?.onCustomViewHidden()
                mCustomViewCallback = null
            }
        }

        // Observar los datos del ViewModel
        lifecycleScope.launch {
            vm.episodeDetail.collect { episode ->
                if (episode != null) {
                    // Mostrar título del episodio
                    val episodeTitle = "${episode.data.title} - Episodio ${episode.data.number}"
                    titleTextView.text = episodeTitle
                    // Limpiar los botones anteriores
                    serverTabsLayout.removeAllViews()

                    // Crear botones para cada servidor
                    episode.data.servers.forEachIndexed { index, server ->
                        val serverButton: Button = Button(this@EpisodeDetailActivity).apply {
                            text = server.name // Usamos el nombre del servidor
                            setOnClickListener {
                                loadServerInWebView(server.embed)
                            }
                        }
                        serverTabsLayout.addView(serverButton)
                    }

                    // Opcional: cargar el primer servidor automáticamente
                    episode.data.servers.firstOrNull()?.let { firstServer ->
                        loadServerInWebView(firstServer.embed)
                    }
                }
            }
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_favorite -> {
                    // Acción de favorito
                    true
                }
                R.id.action_watched -> {
                    // Acción de visto
                    true
                }
                else -> false
            }
        }
    }

    // Función para extraer el número de episodio y el slug de la URL
    private fun extractEpisodeDetails() {
        val regex = "https://www3.animeflv.net/ver/([a-zA-Z0-9-]+)-(\\d+)".toRegex()
        val matchResult = regex.find(episodeUrl)

        matchResult?.let {
            episodeSlug = it.groupValues[1] // "shadowverse-flame"
            episodeNumber = it.groupValues[2] // "85"
        }
    }

    // Función para cargar el iframe del servidor en el WebView
    private fun loadServerInWebView(embedUrl: String) {
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