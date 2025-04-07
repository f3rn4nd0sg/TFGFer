package edu.pract5.tfgfer.ui.animeDetail

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.data.RemoteDataSource
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.ui.adapters.EpisodeAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import edu.pract5.tfgfer.ui.episodeDetail.EpisodeDetailActivity

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var recyclerViewEpisodes: RecyclerView
    private lateinit var animeCover: ImageView
    private lateinit var animeTitle: TextView
    private lateinit var animeStatus: TextView
    private lateinit var animeSynopsis: TextView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var animeSlug: String

    private val vm: AnimeDetailViewModel by viewModels {
        AnimeDetailViewModelFactory(Repository(RemoteDataSource()), animeSlug)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anime_main_layout)

        // Obtener el slug del intent
        animeSlug = intent.getStringExtra("anime_slug") ?: ""

        // Iniciar vistas
        toolbar = findViewById(R.id.toolbar)
        recyclerViewEpisodes = findViewById(R.id.recycler_episodes)
        animeCover = findViewById(R.id.anime_cover)
        animeTitle = findViewById(R.id.anime_title)
        animeStatus = findViewById(R.id.anime_status)
        animeSynopsis = findViewById(R.id.anime_synopsis)

        // Configurar Toolbar como ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)




        lifecycleScope.launch {
            vm.animeDetail.collect { anime ->
                animeTitle.text = anime.data.title
                animeStatus.text = anime.data.status
                animeSynopsis.text = anime.data.synopsis

                Glide.with(this@AnimeDetailActivity).load(anime.data.cover).into(animeCover)

                val chipGroup = findViewById<ChipGroup>(R.id.anime_genres)

                anime.data.genres.forEach { genre ->
                    val chip = Chip(this@AnimeDetailActivity)
                    chip.text = genre
                    chip.isCheckable = false
                    chipGroup.addView(chip)
                }

                recyclerViewEpisodes.adapter = EpisodeAdapter(anime.data.episodes) { episode ->
                    val intent = Intent(this@AnimeDetailActivity, EpisodeDetailActivity::class.java)
                    intent.putExtra("episode_data", episode.url)
                    intent.putExtra("episode_slug", episode.slug)
                    intent.putExtra("episode_number", episode.number.toString())
                    startActivity(intent)
                }
            }
        }
    }
}






