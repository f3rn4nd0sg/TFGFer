package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.databinding.ItemMediaBinding
import edu.pract5.tfgfer.model.busqueda.Media

// Adaptador de RecyclerView para mostrar una lista de medios (animes) en la pantalla de búsqueda.
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
            // Vincular los datos del media con las vistas del item
            binding.tvTitle.text = media.title // Título del media

            // Cargar la portada utilizando Glide
            Glide.with(binding.ivCover.context)
                .load(media.cover)
                .into(binding.ivCover)

            // Establecer el tipo de media
            binding.tvType.text = media.type

            // Cambiar el fondo del tipo de media según el valor de 'type'
            when (media.type) {
                "Anime" -> {
                    // Establecer el fondo para 'anime'
                    binding.tvType.setBackgroundResource(R.drawable.bg_tv)
                }
                "OVA" -> {
                    // Establecer el fondo para 'manga'
                    binding.tvType.setBackgroundResource(R.drawable.bg_ova)
                }
                else -> {
                    // Fondo por defecto si el tipo no es reconocido
                    binding.tvType.setBackgroundResource(R.drawable.bg_movie)
                }
            }

            // Cuando el usuario hace clic en el item
            itemView.setOnClickListener {
                onItemClick(media) // Llamar la función de clic
            }
        }
    }
}

