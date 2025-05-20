package edu.pract5.tfgfer.ui.animeDetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import edu.pract5.tfgfer.ui.adapters.EpisodeAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import edu.pract5.tfgfer.AnimeApp
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.model.serie.Episode
import edu.pract5.tfgfer.ui.episodeDetail.EpisodeDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var recyclerViewEpisodes: RecyclerView
    private lateinit var animeCover: ImageView
    private lateinit var animeTitle: TextView
    private lateinit var animeStatus: TextView
    private lateinit var animeSynopsis: TextView
    private lateinit var genreChipGroup: ChipGroup
    private lateinit var toolbar: MaterialToolbar
    private lateinit var menuItem: MenuItem
    private var isFavorite = false

    private lateinit var animeSlug: String

    private val vm: AnimeDetailViewModel by viewModels {
        val repository = AnimeApp.getRepository(application)
        AnimeDetailViewModelFactory(repository, animeSlug)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anime_main_layout)

        // Inicializar vistas
        recyclerViewEpisodes = findViewById(R.id.recycler_episodes)
        animeCover = findViewById(R.id.anime_cover)
        animeTitle = findViewById(R.id.anime_title)
        animeStatus = findViewById(R.id.anime_status)
        animeSynopsis = findViewById(R.id.anime_synopsis)
        genreChipGroup = findViewById(R.id.anime_genres)
        toolbar = findViewById(R.id.toolbar)

        // Configurar toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Obtener el slug del anime desde el intent
        animeSlug = intent.getStringExtra("anime_slug") ?: ""
        Log.d("AnimeDetailActivity", "Slug: $animeSlug")

        // Configurar RecyclerView
        recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)

        // Observar los datos del ViewModel
        lifecycleScope.launch {
            vm.animeDetail.collectLatest { anime ->
                if (anime.success) {
                    // Cargar imagen
                    Glide.with(this@AnimeDetailActivity)
                        .load(anime.data.cover)
                        .into(animeCover)

                    // Mostrar datos
                    animeTitle.text = anime.data.title
                    animeStatus.text = anime.data.status
                    animeSynopsis.text = anime.data.synopsis

                    // Mostrar géneros como chips
                    genreChipGroup.removeAllViews()
                    anime.data.genres.forEach { genre ->
                        val chip = Chip(this@AnimeDetailActivity)
                        chip.text = genre
                        genreChipGroup.addView(chip)
                    }

                    // Mostrar episodios
                    setupEpisodesAdapter(anime.data.episodes)
                }
            }
        }

        // Observar estado de favorito
        lifecycleScope.launch {
            vm.isFavorite.collect { favorite ->
                isFavorite = favorite
                if (::menuItem.isInitialized) {
                    updateFavoriteIcon()
                }
            }
        }
    }

    private fun setupEpisodesAdapter(episodes: List<Episode>) {
        val adapter = EpisodeAdapter(episodes) { episode ->
            val intent = Intent(this@AnimeDetailActivity, EpisodeDetailActivity::class.java).apply {
                putExtra("episode_data", episode.url)
                putExtra("anime_slug", animeSlug)
            }
            startActivity(intent)
        }
        recyclerViewEpisodes.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.anime_detail_menu, menu)
        menuItem = menu.findItem(R.id.action_favorite)
        updateFavoriteIcon()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_favorite -> {
                vm.toggleFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateFavoriteIcon() {
        menuItem.setIcon(
            if (isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border
        )
    }
}






