package edu.pract5.tfgfer.model.busqueda

data class FilterItem(
    val id: String,      // Ej: "tv", "action", "on_air"
    val name: String,    // Ej: "TV", "Acción", "En emisión"
    var selected: Boolean = false
)