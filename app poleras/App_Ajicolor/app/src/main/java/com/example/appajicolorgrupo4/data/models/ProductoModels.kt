package com.example.appajicolorgrupo4.data.models

import androidx.annotation.DrawableRes

/**
 * Categorías de productos disponibles en la tienda
 */
enum class CategoriaProducto {
    SERIGRAFIA,
    DTF,
    CORPORATIVA,
    ACCESORIOS
}

/**
 * Tipos de talla disponibles
 */
enum class TipoTalla {
    ADULTO,
    INFANTIL
}

/**
 * Tallas para adultos
 */
enum class TallaAdulto(val nombre: String) {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("2XL"),
    XXXL("3XL")
}

/**
 * Tallas para niños
 */
enum class TallaInfantil(val nombre: String) {
    T2("2"),
    T4("4"),
    T6("6"),
    T8("8"),
    T10("10"),
    T12("12"),
    T14("14"),
    T16("16")
}

/**
 * Modelo de datos para un producto
 */
data class Producto(
    val id: String,
    val nombre: String,
    val categoria: CategoriaProducto,
    @DrawableRes val imagenResId: Int,
    val descripcion: String,
    val precio: Int, // Cambiado a Int para evitar decimales
    val tipoTalla: TipoTalla? = null, // null para accesorios
    val permiteTipoInfantil: Boolean = false, // true solo para DTF
    val coloresDisponibles: List<String>? = null, // Códigos HEX de colores disponibles
    val stock: Int = 100, // Stock disponible por defecto
    val rating: Float = 0f, // Calificación promedio (0-5)
    val cantidadReviews: Int = 0 // Número de reviews
) {
    /**
     * Formatea el precio con el símbolo de peso chileno
     */
    fun precioFormateado(): String {
        return "$$precio"
    }
}

/**
 * Item de configuración de producto para agregar al carrito
 */
data class ProductoConfiguracion(
    val producto: Producto,
    val tipoTallaSeleccionado: TipoTalla? = null, // Para DTF
    val tallaAdulto: TallaAdulto? = null,
    val tallaInfantil: TallaInfantil? = null,
    val colorSeleccionado: String? = null, // Código HEX
    val cantidad: Int = 1
) {
    /**
     * Calcula el subtotal de este item
     */
    fun calcularSubtotal(): Int = producto.precio * cantidad

    /**
     * Obtiene la talla seleccionada como string
     */
    fun obtenerTallaString(): String? {
        return tallaAdulto?.nombre ?: tallaInfantil?.nombre
    }

    /**
     * Formatea el subtotal con el símbolo de peso
     */
    fun subtotalFormateado(): String {
        return "$${calcularSubtotal()}"
    }
}

/**
 * Repositorio de productos de ejemplo
 */
object ProductoRepository {

    /**
     * Lista de productos disponibles en la tienda
     * Nota: Actualizar los IDs de recursos (R.drawable.xxx) cuando agregues las imágenes reales
     */
    fun obtenerProductos(): List<Producto> {
        return listOf(
            // Productos de Serigrafía - Usando poleras con diseños de bandas
            Producto(
                id = "SERI001",
                nombre = "Polera Red Hot Chili Peppers",
                categoria = CategoriaProducto.SERIGRAFIA,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_red_hot_chili_peppers,
                descripcion = "Polera diseño Red Hot Chili Peppers\n\n**Material:** Algodón 100%",
                precio = 15000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = false
            ),
            Producto(
                id = "SERI002",
                nombre = "Polera Tool",
                categoria = CategoriaProducto.SERIGRAFIA,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_tool,
                descripcion = "Polera diseño Tool\n\n**Material:** Algodón 100%",
                precio = 15000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = false
            ),
            Producto(
                id = "SERI003",
                nombre = "Polera Deftones",
                categoria = CategoriaProducto.SERIGRAFIA,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_deftones,
                descripcion = "Polera diseño Deftones\n\n**Material:** Algodón 100%",
                precio = 15000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = false
            ),

            // Productos DTF
            Producto(
                id = "DTF001",
                nombre = "Polera Faith No More",
                categoria = CategoriaProducto.DTF,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_faith_no_more,
                descripcion = "Polera diseño Faith No More\n\n**Material:** Algodón",
                precio = 18000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = true // DTF permite adulto e infantil
            ),
            Producto(
                id = "DTF002",
                nombre = "Polera Incubus",
                categoria = CategoriaProducto.DTF,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_incubus,
                descripcion = "Polera diseño Incubus\n\n**Material:** Algodón",
                precio = 18000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = true
            ),

            // Productos Corporativos
            Producto(
                id = "CORP001",
                nombre = "Polera System of a Down",
                categoria = CategoriaProducto.CORPORATIVA,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_system_of_a_down,
                descripcion = "Polera diseño System of a Down\n\n**Material:** Algodón",
                precio = 20000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = false
            ),
            Producto(
                id = "CORP002",
                nombre = "Polera Rage Against The Machine",
                categoria = CategoriaProducto.CORPORATIVA,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.polera_rage_against_the_machine,
                descripcion = "Polera diseño Rage Against The Machine\n\n**Material:** Algodón",
                precio = 20000,
                tipoTalla = TipoTalla.ADULTO,
                permiteTipoInfantil = false
            ),

            // Accesorios
            Producto(
                id = "ACC001",
                nombre = "Jockey Genérico",
                categoria = CategoriaProducto.ACCESORIOS,
                imagenResId = com.example.appajicolorgrupo4.R.drawable.jockey,
                descripcion = "Jockey genérico",
                precio = 8000,
                tipoTalla = null, // Accesorios no tienen talla
                permiteTipoInfantil = false
            )
        )
    }

    /**
     * Busca un producto por su ID
     */
    fun obtenerProductoPorId(id: String): Producto? {
        return obtenerProductos().find { it.id == id }
    }

    /**
     * Obtiene productos por categoría
     */
    fun obtenerProductosPorCategoria(categoria: CategoriaProducto): List<Producto> {
        return obtenerProductos().filter { it.categoria == categoria }
    }

    /**
     * Busca productos por nombre
     */
    fun buscarProductos(query: String): List<Producto> {
        return obtenerProductos().filter {
            it.nombre.contains(query, ignoreCase = true) ||
            it.descripcion.contains(query, ignoreCase = true)
        }
    }
}

