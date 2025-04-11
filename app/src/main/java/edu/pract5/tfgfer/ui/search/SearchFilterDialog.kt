package edu.pract5.tfgfer.ui.search

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import edu.pract5.tfgfer.databinding.DialogSearchFilterBinding
import edu.pract5.tfgfer.databinding.ItemFilterCheckboxBinding
import edu.pract5.tfgfer.model.busqueda.FilterItem

class SearchFilterDialog : DialogFragment() {
    private var _binding: DialogSearchFilterBinding? = null
    private val binding get() = _binding!!

    // Listas de filtros
    private val orderOptions = listOf(
        FilterItem("default", "Predeterminado", true),
        FilterItem("updated", "Más Actualizado"),
        FilterItem("added", "Más Añadido"),
        FilterItem("title", "Por Título"),
        FilterItem("rating", "Por Calificación")
    )

    private val typesList = listOf(
        FilterItem("tv", "TV"),
        FilterItem("movie", "Película"),
        FilterItem("ova", "OVA"),
        FilterItem("special", "Especial")
    )

    private val genresList = listOf(
        FilterItem("action", "Acción"),
        FilterItem("adventure", "Aventuras"),
        FilterItem("comedy", "Comedia"),
        FilterItem("drama", "Drama"),
        FilterItem("fantasy", "Fantasía"),
        FilterItem("sci_fi", "Ciencia Ficción"),
        FilterItem("horror", "Terror"),
        FilterItem("romance", "Romance"),
        FilterItem("shounen", "Shounen"),
        FilterItem("shoujo", "Shoujo")
    )

    private val statusList = listOf(
        FilterItem("on_air", "En emisión"),
        FilterItem("finished", "Terminado"),
        FilterItem("not_aired", "No emitido")
    )

    // Variables para mantener el estado seleccionado
    private var selectedOrder: String = "default"
    private val selectedTypes = mutableListOf<String>()
    private val selectedGenres = mutableListOf<String>()
    private val selectedStatuses = mutableListOf<String>()

    // Mapeo de estados a valores numéricos
    private val statusConversion = mapOf(
        "on_air" to 1,
        "finished" to 2,
        "not_aired" to 3
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSearchFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Filtrar búsqueda")
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.9).toInt() // 90% de la pantalla
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderOptions()
        setupRecyclerViews()
        setupButtons()
    }

    private fun setupOrderOptions() {
        binding.radioGroupOrder.setOnCheckedChangeListener { _, checkedId ->
            selectedOrder = when (checkedId) {
                binding.radioDefault.id -> "default"
                binding.radioUpdated.id -> "updated"
                binding.radioAdded.id -> "added"
                binding.radioTitle.id -> "title"
                binding.radioRating.id -> "rating"
                else -> "default"
            }
        }
    }

    private fun setupRecyclerViews() {
        // Configuración común para todos los RecyclerViews
        fun setupRecyclerView(recyclerView: RecyclerView, items: List<FilterItem>) {
            val layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }

            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = FilterAdapter(items) { item, isChecked ->
                if (isChecked) {
                    when (recyclerView) {
                        binding.rvTypes -> selectedTypes.add(item.id)
                        binding.rvGenres -> selectedGenres.add(item.id)
                        binding.rvStatuses -> selectedStatuses.add(item.id)
                    }
                } else {
                    when (recyclerView) {
                        binding.rvTypes -> selectedTypes.remove(item.id)
                        binding.rvGenres -> selectedGenres.remove(item.id)
                        binding.rvStatuses -> selectedStatuses.remove(item.id)
                    }
                }
            }
        }

        setupRecyclerView(binding.rvTypes, typesList)
        setupRecyclerView(binding.rvGenres, genresList)
        setupRecyclerView(binding.rvStatuses, statusList)
    }

    private fun setupButtons() {
        binding.btnApply.setOnClickListener {
            Log.d("SearchFilterDialog",
                "Filtros aplicados: " +
                        "order=$selectedOrder, " +
                        "types=$selectedTypes, " +
                        "genres=$selectedGenres, " +
                        "statuses=$selectedStatuses")

            // Convertir los estados seleccionados a sus valores numéricos
            val statusInts = if (selectedStatuses.isNotEmpty()) {
                selectedStatuses.mapNotNull { statusConversion[it] }
            } else {
                emptyList()
            }

            // Llamar al métod0  de búsqueda, enviando listas incluso si están vacías
            (activity as? SearchActivity)?.vm?.searchWithFilters(
                order = selectedOrder,
                types = selectedTypes.takeIf { it.isNotEmpty() },
                genres = selectedGenres.takeIf { it.isNotEmpty() },
                statuses = statusInts
            )

            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private inner class FilterAdapter(
        private val items: List<FilterItem>,
        private val onItemChecked: (FilterItem, Boolean) -> Unit
    ) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemFilterCheckboxBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFilterCheckboxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.checkboxItem.apply {
                text = item.name
                isChecked = item.selected
                setOnCheckedChangeListener { _, isChecked ->
                    item.selected = isChecked
                    onItemChecked(item, isChecked)
                }
            }
        }
        override fun getItemCount() = items.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
