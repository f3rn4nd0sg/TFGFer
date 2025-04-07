package edu.pract5.tfgfer.ui.episodeDetail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.data.RemoteDataSource
import kotlinx.coroutines.launch
import android.content.pm.ActivityInfo
import android.util.Log
import com.google.android.material.appbar.MaterialToolbar
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity


class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var episodeUrl: String
    private lateinit var episodeSlug: String
    private lateinit var episodeNumber: String
    private lateinit var webView: WebView
    private lateinit var titleTextView: TextView
    private lateinit var serverTabsLayout: LinearLayout
    private lateinit var animeSlug: String

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

        // Configurar WebChromeClient para detectar pantalla completa
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: android.view.View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                // Forzar la orientación en pantalla completa
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                // Volver a la orientación vertical al salir de pantalla completa
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
}