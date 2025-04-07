package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.model.serie.AnimeData

class AnimeAdapter(
    private val animeList: List<AnimeData>,
    private val onItemClick: (AnimeData) -> Unit // Delegamos el clic al Activity/Fragment
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val cover: ImageView = view.findViewById(R.id.item_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.title.text = anime.title
        Glide.with(holder.itemView.context).load(anime.cover).into(holder.cover)

        holder.itemView.setOnClickListener { onItemClick(anime) }
    }

    override fun getItemCount() = animeList.size
}
