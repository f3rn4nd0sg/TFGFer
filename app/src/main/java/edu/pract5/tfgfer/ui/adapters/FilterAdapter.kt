package edu.pract5.tfgfer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import edu.pract5.tfgfer.R
import edu.pract5.tfgfer.model.busqueda.FilterItem

class FilterAdapter(private val items: List<FilterItem>) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox.text = items[position].name
        holder.checkBox.isChecked = items[position].selected
    }

    override fun getItemCount() = items.size
}