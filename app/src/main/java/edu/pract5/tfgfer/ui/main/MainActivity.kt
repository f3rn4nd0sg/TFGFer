package edu.pract5.tfgfer.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.data.RemoteDataSource
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.databinding.ActivityMainBinding
import edu.pract5.tfgfer.model.latestEpisodes.LatestEpisodeItem
import edu.pract5.tfgfer.ui.adapters.LatestEpisodesAdapter
import edu.pract5.tfgfer.ui.episodeDetail.EpisodeDetailActivity
import edu.pract5.tfgfer.ui.search.SearchActivity
import edu.pract5.tfgfer.ui.search.SearchFilterDialog
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels {
        MainViewModelFactory(Repository(RemoteDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estamos en home
                    true
                }
                R.id.nav_favorites -> {
                    /*
                    showFavorites()
                    */

                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.currentLatestEpisodes.collect { latestEpisodes : List<LatestEpisodeItem> ->
                binding.recyclerView.adapter = LatestEpisodesAdapter(latestEpisodes) { episode : LatestEpisodeItem ->
                    val intent = Intent(this@MainActivity, EpisodeDetailActivity::class.java).apply {
                        putExtra("episode_data", episode.url)
                    }
                    startActivity(intent)
                }
            }
        }

/*
        lifecycleScope.launch {
            vm.favoriteEpisodes.collect { favorites ->
                // Actualizar la vista de favoritos si es necesario
            }
        }
        */

    }
/*
    private fun showFavorites() {
        lifecycleScope.launch {

            val favorites = vm.favoriteEpisodes
            /*
            binding.recyclerView.adapter = LatestEpisodesAdapter(favorites) { episode ->
                val intent = Intent(this@MainActivity, EpisodeDetailActivity::class.java).apply {
                    putExtra("episode_data", episode.url)
                }
                startActivity(intent)
            }
            */


            // Cambiar el título para indicar que estamos viendo favoritos
            findViewById<TextView>(R.id.toolbar).text = getString(R.string.favoritos)
        }
    */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(this@MainActivity, SearchActivity::class.java).apply {
                    putExtra("query", query)
                }
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        SearchFilterDialog().show(supportFragmentManager, "SearchFilterDialog")
    }
}