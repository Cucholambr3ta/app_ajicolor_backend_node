package com.example.appajicolorgrupo4.data

/**
 * Representa un producto completo en el catálogo
 */
data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int, // Cambiado a Int para evitar decimales
    val categoria: CategoriaProducto,
    val imagenResId: Int, // Resource ID de la imagen
    val tallasDisponibles: List<Talla> = emptyList(),
    val coloresDisponibles: List<ColorInfo> = emptyList(),
    val tipoProducto: TipoProducto? = null, // Solo para DTF
    val stock: Int = 100,
    val calificacionPromedio: Float = 0f,
    val numeroResenas: Int = 0
) {
    /**
     * Verifica si el producto requiere selección de talla
     */
    fun requiereTalla(): Boolean = categoria.usaTallas()

    /**
     * Verifica si el producto requiere selección de color
     */
    fun requiereColor(): Boolean = categoria != CategoriaProducto.ACCESORIOS || coloresDisponibles.isNotEmpty()

    /**
     * Verifica si el producto permite seleccionar tipo (adulto/infantil)
     */
    fun permiteSeleccionTipo(): Boolean = categoria.permiteSeleccionTipo()

    /**
     * Formatea el precio con símbolo $
     * Ejemplo: 15000 -> "$15000"
     */
    fun precioFormateado(): String {
        return "$$precio"
    }
}

/**
 * Representa una reseña/review de un producto
 */
data class ProductoResena(
    val id: String,
    val productoId: String,
    val usuarioNombre: String,
    val calificacion: Int, // 0-5 estrellas
    val comentario: String,
    val imagenUrl: String? = null,
    val fecha: Long = System.currentTimeMillis()
)

/**
 * Configuración seleccionada para agregar un producto al carrito
 */
data class ProductoConfiguracion(
    val producto: Producto,
    val talla: Talla? = null,
    val color: ColorInfo? = null,
    val cantidad: Int = 1
) {
    /**
     * Valida que la configuración esté completa según los requisitos del producto
     */
    fun esValida(): Boolean {
        if (producto.requiereTalla() && talla == null) return false
        if (producto.requiereColor() && color == null) return false
        if (cantidad <= 0) return false
        return true
    }

    /**
     * Convierte la configuración a ProductoCarrito para agregarlo al carrito
     */
    fun toProductoCarrito(): ProductoCarrito {
        return ProductoCarrito(
            id = producto.id,
            nombre = producto.nombre,
            precio = producto.precio,
            cantidad = this.cantidad,
            talla = talla,
            color = color ?: ColorInfo("Sin color", androidx.compose.ui.graphics.Color.Gray, "#808080"),
            categoria = producto.categoria,
            imagenResId = producto.imagenResId
        )
    }
}

