package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.data.ProductoResena
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para gestionar productos y sus reseñas
 */
class ProductoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _resenas = MutableStateFlow<Map<String, List<ProductoResena>>>(emptyMap())
    val resenas: StateFlow<Map<String, List<ProductoResena>>> = _resenas.asStateFlow()

    /**
     * Carga los productos (en producción vendría de una API o BD)
     */
    fun cargarProductos(listaProductos: List<Producto>) {
        _productos.value = listaProductos
    }

    /**
     * Obtiene un producto por su ID
     */
    fun obtenerProducto(id: String): Producto? {
        return _productos.value.find { it.id == id }
    }

    /**
     * Obtiene las reseñas de un producto
     */
    fun obtenerResenas(productoId: String): List<ProductoResena> {
        return _resenas.value[productoId] ?: emptyList()
    }

    /**
     * Agrega una nueva reseña a un producto
     */
    fun agregarResena(resena: ProductoResena) {
        val resenasActuales = _resenas.value.toMutableMap()
        val resenasProducto = resenasActuales[resena.productoId]?.toMutableList() ?: mutableListOf()
        resenasProducto.add(0, resena) // Agregar al inicio
        resenasActuales[resena.productoId] = resenasProducto
        _resenas.value = resenasActuales

        // Actualizar calificación promedio del producto
        actualizarCalificacionProducto(resena.productoId)
    }

    /**
     * Actualiza la calificación promedio de un producto
     */
    private fun actualizarCalificacionProducto(productoId: String) {
        val resenasProducto = obtenerResenas(productoId)
        if (resenasProducto.isEmpty()) return

        val promedio = resenasProducto.map { it.calificacion }.average().toFloat()

        _productos.value = _productos.value.map { producto ->
            if (producto.id == productoId) {
                producto.copy(
                    calificacionPromedio = promedio,
                    numeroResenas = resenasProducto.size
                )
            } else {
                producto
            }
        }
    }

    /**
     * Calcula el promedio de calificaciones de un producto
     */
    fun calcularPromedioCalificacion(productoId: String): Float {
        val resenasProducto = obtenerResenas(productoId)
        if (resenasProducto.isEmpty()) return 0f
        return resenasProducto.map { it.calificacion }.average().toFloat()
    }
}

