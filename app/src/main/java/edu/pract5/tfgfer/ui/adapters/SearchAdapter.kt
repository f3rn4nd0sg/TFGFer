package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.databinding.ItemMediaBinding
import edu.pract5.tfgfer.model.busqueda.Media

class SearchAdapter(
    private val mediaList: List<Media>, // Lista de medios (animes)
    private val onItemClick: (Media) -> Unit // Acción al hacer clic
) : RecyclerView.Adapter<SearchAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.bind(media)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    inner class MediaViewHolder(private val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(media: Media) {
            binding.tvTitle.text = media.title // Título del media
            // Cargar la portada utilizando Glide
            Glide.with(binding.ivCover.context)
                .load(media.cover)
                .into(binding.ivCover)

            // Cuando el usuario hace clic en el item
            itemView.setOnClickListener {
                onItemClick(media) // Llamar la función de clic
            }
        }
    }
}
