package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.ProductoCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CarritoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoCarrito>>(emptyList())
    val productos: StateFlow<List<ProductoCarrito>> = _productos.asStateFlow()

    companion object {
        const val COSTO_ENVIO_NORMAL = 5000
        const val MONTO_ENVIO_GRATIS = 20000
        private const val IVA_PERCENT = 0.19
    }

    val subtotal: StateFlow<Int> = _productos.map { productos ->
        productos.sumOf { it.subtotal() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val iva: StateFlow<Int> = subtotal.map { sub ->
        (sub * IVA_PERCENT).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val calificaEnvioGratis: StateFlow<Boolean> = subtotal.map { it >= MONTO_ENVIO_GRATIS }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val costoEnvio: StateFlow<Int> = calificaEnvioGratis.map { gratis ->
        if (gratis) 0 else COSTO_ENVIO_NORMAL
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), COSTO_ENVIO_NORMAL)

    val montoFaltante: StateFlow<Int> = subtotal.map { sub ->
        (MONTO_ENVIO_GRATIS - sub).coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MONTO_ENVIO_GRATIS)

    val total: StateFlow<Int> = combine(subtotal, iva, costoEnvio) { s, i, c ->
        s + i + c
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun agregarProducto(producto: ProductoCarrito) {
        val productosActuales = _productos.value.toMutableList()
        val productoExistente = productosActuales.find {
            it.id == producto.id && it.talla == producto.talla && it.color.nombre == producto.color.nombre
        }

        if (productoExistente != null) {
            val index = productosActuales.indexOf(productoExistente)
            productosActuales[index] = productoExistente.copy(
                cantidad = productoExistente.cantidad + producto.cantidad
            )
        } else {
            productosActuales.add(producto)
        }
        _productos.value = productosActuales
    }

    fun eliminarProducto(producto: ProductoCarrito) {
        _productos.value = _productos.value.filter { it != producto }
    }

    fun actualizarCantidad(producto: ProductoCarrito, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarProducto(producto)
            return
        }
        _productos.value = _productos.value.map {
            if (it == producto) {
                it.copy(cantidad = nuevaCantidad)
            } else {
                it
            }
        }
    }

    fun limpiarCarrito() {
        _productos.value = emptyList()
    }

    fun cantidadTotal(): Int {
        return _productos.value.sumOf { it.cantidad }
    }
}
