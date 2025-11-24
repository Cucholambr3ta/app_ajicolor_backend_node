package com.example.appajicolorgrupo4.data

/**
 * Categorías de productos disponibles en la aplicación.
 *
 * - SERIGRAFIA: Productos con serigrafía (solo adulto, tallas adulto, colores adulto)
 * - DTF: Direct to Film (permite elegir adulto o infantil)
 * - CORPORATIVA: Productos corporativos (solo adulto, tallas adulto, colores adulto)
 * - ACCESORIOS: Accesorios sin tallas (colores adulto)
 */
enum class CategoriaProducto(val displayName: String) {
    SERIGRAFIA("Serigrafía"),
    DTF("DTF"),
    CORPORATIVA("Corporativa"),
    ACCESORIOS("Accesorios");

    /**
     * Indica si esta categoría permite seleccionar tipo (adulto/infantil).
     * Solo DTF permite esta selección.
     */
    fun permiteSeleccionTipo(): Boolean {
        return this == DTF
    }

    /**
     * Indica si esta categoría usa tallas.
     * Los accesorios no usan tallas.
     */
    fun usaTallas(): Boolean {
        return this != ACCESORIOS
    }

    /**
     * Obtiene el tipo de producto por defecto para esta categoría.
     */
    fun tipoProductoDefault(): TipoProducto {
        return TipoProducto.ADULTO
    }
}
