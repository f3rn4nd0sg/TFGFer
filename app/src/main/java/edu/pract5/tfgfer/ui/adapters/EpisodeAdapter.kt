package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.pract5.tfgfer.databinding.ItemEpisodeBinding
import edu.pract5.tfgfer.model.serie.Episode

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onEpisodeClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
    }

    override fun getItemCount(): Int = episodes.size

    inner class EpisodeViewHolder(private val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: Episode) {
            binding.episodeUrl.text = "Episodio ${episode.number}: ${episode.slug}"
            binding.root.setOnClickListener {
                onEpisodeClick(episode)
            }
        }
    }
}


