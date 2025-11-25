package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.data.ProductoResena
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.repository.RemoteProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar productos y sus reseñas
 */
class ProductoViewModel : ViewModel() {

    private val repository = RemoteProductoRepository(RetrofitInstance.api)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _resenas = MutableStateFlow<Map<String, List<ProductoResena>>>(emptyMap())
    val resenas: StateFlow<Map<String, List<ProductoResena>>> = _resenas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarProductosRemotos()
    }

    /**
     * Carga los productos desde el backend
     */
    fun cargarProductosRemotos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.obtenerProductos()
            result.onSuccess { lista ->
                _productos.value = lista
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo producto
     */
    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.crearProducto(producto)
            result.onSuccess { created ->
                val currentList = _productos.value.toMutableList()
                currentList.add(created)
                _productos.value = currentList
            }.onFailure { e ->
                _error.value = "Error creando producto: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Actualiza un producto existente
     */
    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.actualizarProducto(producto)
            result.onSuccess { updated ->
                val currentList = _productos.value.map { 
                    if (it.id == updated.id) updated else it 
                }
                _productos.value = currentList
            }.onFailure { e ->
                _error.value = "Error actualizando producto: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Elimina un producto
     */
    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.eliminarProducto(id)
            result.onSuccess {
                val currentList = _productos.value.filter { it.id != id }
                _productos.value = currentList
            }.onFailure { e ->
                _error.value = "Error eliminando producto: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Carga los productos (Legacy/Local)
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

