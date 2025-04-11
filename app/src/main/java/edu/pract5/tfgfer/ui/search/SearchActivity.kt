package edu.pract5.tfgfer.ui.search

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.data.RemoteDataSource
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.databinding.ActivitySearchBinding
import edu.pract5.tfgfer.ui.adapters.SearchAdapter
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import edu.pract5.tfgfer.ui.main.MainActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    val vm: SearchViewModel by viewModels {
        SearchViewModelFactory(Repository(RemoteDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.enterTransition = Slide(Gravity.END).apply {
            duration = 500 // Duración de la animación
        }

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        observeViewModel()
        setupBottomNavigation()
        // 🔍 Si no se está buscando por texto, cargar por defecto los "onAir"
        val query = intent.getStringExtra("query")
        if (!query.isNullOrEmpty()) {
            vm.search(query)
        } else {
            // Estado 1 = En emisión
            vm.searchWithFilters(statuses = listOf(1))
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun observeViewModel() {
        vm.searchResults.observe(this) { results ->
            Log.d("SearchActivity", "Resultados de búsqueda: $results")
            binding.recyclerView.adapter = SearchAdapter(results) { anime ->
                Log.d("SearchActivity", "Anime clicado: ${anime.slug}")

                val intent = Intent(this, AnimeDetailActivity::class.java)
                intent.putExtra("anime_slug", anime.slug)
                startActivity(intent)
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_favorites -> {
                    // TODO: ir a la de favoritos
                    true
                }
                R.id.nav_search -> {
                    // Ya estamos aquí
                    true
                }
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_search
    }


    // Infla el menú en la toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_toolbar_menu, menu)
        return true
    }

    // Maneja clicks del botón de la toolbar (filtro)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                SearchFilterDialog().show(supportFragmentManager, "FilterDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}



