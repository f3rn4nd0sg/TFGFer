package edu.pract5.tfgfer.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.pract5.tfgfer.AnimeApp
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.databinding.ActivitySearchBinding
import edu.pract5.tfgfer.ui.adapters.SearchAdapter
import edu.pract5.tfgfer.ui.animeDetail.AnimeDetailActivity
import edu.pract5.tfgfer.ui.main.MainActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    val vm: SearchViewModel by viewModels {
        SearchViewModelFactory(AnimeApp.getRepository(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.enterTransition = Slide(Gravity.END).apply {
            duration = 500
        }

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupBottomNavigation()
        setupSearchBar()
        observeViewModel()

        val query = intent.getStringExtra("query")
        if (!query.isNullOrEmpty()) {
            vm.search(query)
        } else {
            vm.searchWithFilters(statuses = listOf(1))
        }

        binding.fabSearch.setOnClickListener {
            SearchFilterDialog().show(supportFragmentManager, "search_filter")
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

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length >= 3) {
                    vm.search(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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
                R.id.nav_search -> true
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_search
    }
}




