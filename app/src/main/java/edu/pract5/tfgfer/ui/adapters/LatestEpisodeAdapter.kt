package edu.pract5.tfgfer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.databinding.ItemLatestEpisodeBinding
import edu.pract5.tfgfer.model.latestEpisodes.EpisodeData

class LatestEpisodesAdapter(
    private val episodesList: List<EpisodeData>,
    private val onItemClick: (EpisodeData) -> Unit // Callback de clic
) : RecyclerView.Adapter<LatestEpisodesAdapter.EpisodeViewHolder>() {

    inner class EpisodeViewHolder(private val binding: ItemLatestEpisodeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: EpisodeData) {
            binding.itemTitle.text = episode.title
            binding.itemNumber.text = "Capítulo ${episode.number}"
            Glide.with(binding.itemCover.context).load(episode.cover).into(binding.itemCover)

            // Manejamos el clic en el episodio
            binding.root.setOnClickListener { onItemClick(episode) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemLatestEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(episodesList[position])
    }

    override fun getItemCount(): Int = episodesList.size
}
