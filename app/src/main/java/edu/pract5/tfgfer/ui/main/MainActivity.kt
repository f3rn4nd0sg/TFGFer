package edu.pract5.tfgfer.ui.main


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import edu.pract5.tfgfer.data.RemoteDataSource
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.databinding.ActivityMainBinding
import edu.pract5.tfgfer.ui.LatestEpisodesAdapter
import edu.pract5.tfgfer.ui.episodeDetail.EpisodeDetailActivity
import kotlinx.coroutines.launch

//TODO, esta será la pagina principal igual toca migrar a fragment para más comodidad
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels {
        MainViewModelFactory(Repository(RemoteDataSource()))
        //TODO si uso bdd local, habría que añadirlo aquí
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO crear layout principal, enlazarlo con la actividad y crear viewModel
        binding = ActivityMainBinding.inflate(layoutInflater)


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        lifecycleScope.launch {
            vm.currentLatestEpisodes.collect { latestEpisodes ->
                recyclerView.adapter = LatestEpisodesAdapter(latestEpisodes) { episode ->
                    val intent = Intent(this@MainActivity, EpisodeDetailActivity::class.java).apply {
                        putExtra("episode_data", episode.url) // Pasamos url del episodio, solo necesitamos el nombre de este
                    }
                    startActivity(intent)
                }
            }
        }

    }

}