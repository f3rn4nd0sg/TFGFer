package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.model.latestEpisodes.LatestEpisodeItem

class LatestEpisodesAdapter(
    private val episodes: List<LatestEpisodeItem>,
    private val onItemClick: (LatestEpisodeItem) -> Unit
) :
    RecyclerView.Adapter<LatestEpisodesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImageView: ImageView = itemView.findViewById(R.id.item_cover)
        val numberTextView: TextView = itemView.findViewById(R.id.item_number)
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_latest_episode, parent, false) // Use your layout file
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]

        // Set the data to the views
        holder.numberTextView.text =  ("Episodio " + episode.number.toString()) // Assuming you have a 'number' property
        holder.titleTextView.text = episode.title // Assuming you have a 'title' property
        // Load the image using a library like Glide or Picasso
        // Example with Glide:
         Glide.with(holder.coverImageView.context)
             .load(episode.cover) // Assuming you have an 'imageUrl' property
             .into(holder.coverImageView)

        holder.itemView.setOnClickListener {
            onItemClick(episode)
        }
    }

    override fun getItemCount(): Int = episodes.size
}