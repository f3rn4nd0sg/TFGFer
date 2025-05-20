package edu.pract5.tfgfer.ui.favorites

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import edu.pract5.tfgfer.AnimeApp
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.ui.adapters.FavoriteAnimeAdapter
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import kotlinx.coroutines.launch
import android.view.MenuItem


class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAnimeAdapter

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(AnimeApp.getRepository(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FavoriteAnimeAdapter(
            onItemClick = { favoriteAnime ->
                val intent = Intent(this, AnimeDetailActivity::class.java)
                intent.putExtra("anime_slug", favoriteAnime.slug)
                startActivity(intent)
            },
            onDeleteClick = { slug ->
                viewModel.removeFavorite(slug)
            }
        )

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.favoriteAnimes.collect { favorites ->
                adapter.submitList(favorites)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}