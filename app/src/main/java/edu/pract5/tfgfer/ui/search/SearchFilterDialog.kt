package edu.pract5.tfgfer.ui.search

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import edu.pract5.tfgfer.databinding.DialogSearchFilterBinding

class SearchFilterDialog : DialogFragment() {
    private lateinit var binding: DialogSearchFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchFilterBinding.inflate(inflater, container, false)
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
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lógica para aplicar los filtros
        binding.btnApply.setOnClickListener {
            // Orden de búsqueda seleccionado
            val order = when (binding.radioGroupOrder.checkedRadioButtonId) {
                binding.radioDefault.id -> "default"
                binding.radioUpdated.id -> "updated"
                binding.radioAdded.id -> "added"
                binding.radioTitle.id -> "title"
                binding.radioRating.id -> "rating"
                else -> "default"
            }

            // Recopilación de tipos seleccionados (TV, Película, etc.)
            val types = mutableListOf<String>().apply {
                if (binding.checkboxTv.isChecked) add("tv")
                if (binding.checkboxMovie.isChecked) add("movie")
                if (binding.checkboxSpecial.isChecked) add("special")
                if (binding.checkboxOva.isChecked) add("ova")
            }.takeIf { it.isNotEmpty() }

            // Recopilación de géneros seleccionados
            val genres = mutableListOf<String>().apply {
                if (binding.checkboxAction.isChecked) add("action")
                if (binding.checkboxComedy.isChecked) add("comedy")
                if (binding.checkboxDrama.isChecked) add("drama")
                if (binding.checkboxFantasy.isChecked) add("fantasy")
            }

            // Recopilación de estados seleccionados
            val statuses = mutableListOf<Int>().apply {
                if (binding.checkboxOnAir.isChecked) add(1)  // Estado "En emisión"
                if (binding.checkboxFinished.isChecked) add(2)  // Estado "Terminado"
                if (binding.checkboxNotAired.isChecked) add(3)  // Estado "No emitido"
            }

            // Llamar al métod0 de la actividad para realizar la búsqueda con filtros
            (activity as? SearchActivity)?.vm?.searchWithFilters(
                order = order,
                types = types,
                genres = genres,
                statuses = statuses
            )
            Log.d("SearchFilterDialog", "Filtros aplicados: order=$order, types=$types, genres=$genres, statuses=$statuses")

            dismiss()  // Cerrar el diálogo
        }

        // Acción al cancelar los filtros
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}


