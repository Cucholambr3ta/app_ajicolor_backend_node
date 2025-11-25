package com.example.appajicolorgrupo4.data.models

/** Modelo de datos para Producto que coincide con la respuesta del Backend Node.js */
data class Product(
        val id: String,
        val nombre: String,
        val descripcion: String,
        val precio: Int,
        val categoria: String,
        val stock: Int,
        val imagenResId: Int? = null, // Mantenemos compatibilidad con imágenes locales por ahora
        val imagenUrl: String? = null,
        val tipoTalla: String? = null,
        val permiteTipoInfantil: Boolean = false,
        val coloresDisponibles: List<String>? = null,
        val rating: Float = 0f,
        val cantidadReviews: Int = 0
)
