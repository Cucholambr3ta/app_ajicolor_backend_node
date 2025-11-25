package com.example.appajicolorgrupo4.data.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("_id") val id: String? = null,
    val numeroPedido: String,
    val usuario: String, // User ID
    val productos: List<OrderProduct>,
    val subtotal: Int,
    val impuestos: Int,
    val costoEnvio: Int,
    val total: Int,
    val direccionEnvio: String,
    val telefono: String,
    val metodoPago: String,
    val estado: String = "CONFIRMADO",
    val fechaCreacion: String? = null
)

data class OrderProduct(
    val producto: String, // Product ID
    val cantidad: Int,
    val precioUnitario: Int,
    val talla: String? = null,
    val color: String? = null
)
