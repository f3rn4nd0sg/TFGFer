package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.model.serie.FavoriteAnime

class FavoriteAnimeAdapter(
    private val onItemClick: (FavoriteAnime) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<FavoriteAnime, FavoriteAnimeAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_anime, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick, onDeleteClick)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.favoriteTitleTextView)
        private val type: TextView = view.findViewById(R.id.favoriteTypeTextView)
        private val status: TextView = view.findViewById(R.id.favoriteStatusTextView)
        private val cover: ImageView = view.findViewById(R.id.favoriteImageView)
        private val deleteButton: ImageButton = view.findViewById(R.id.favoriteDeleteButton)

        fun bind(
            item: FavoriteAnime,
            onItemClick: (FavoriteAnime) -> Unit,
            onDeleteClick: (String) -> Unit
        ) {
            title.text = item.title
            type.text = item.type
            status.text = item.status

            Glide.with(cover)
                .load(item.cover)
                .into(cover)

            itemView.setOnClickListener { onItemClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item.slug) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoriteAnime>() {
        override fun areItemsTheSame(oldItem: FavoriteAnime, newItem: FavoriteAnime): Boolean {
            return oldItem.slug == newItem.slug
        }

        override fun areContentsTheSame(oldItem: FavoriteAnime, newItem: FavoriteAnime): Boolean {
            return oldItem == newItem
        }
    }
}