package com.example.appajicolorgrupo4.data

/**
 * Clase sellada que representa las tallas disponibles para productos
 */
sealed class Talla(val valor: String, val displayName: String) {

    // Tallas de Adulto
    data object S : Talla("S", "S")
    data object M : Talla("M", "M")
    data object L : Talla("L", "L")
    data object XL : Talla("XL", "XL")
    data object XXL : Talla("2XL", "2XL")
    data object XXXL : Talla("3XL", "3XL")

    // Tallas Infantiles
    data object T2 : Talla("2", "2")
    data object T4 : Talla("4", "4")
    data object T6 : Talla("6", "6")
    data object T8 : Talla("8", "8")
    data object T10 : Talla("10", "10")
    data object T12 : Talla("12", "12")
    data object T14 : Talla("14", "14")
    data object T16 : Talla("16", "16")

    companion object {
        /**
         * Obtiene la lista de tallas para adultos
         */
        fun tallasAdulto(): List<Talla> = listOf(S, M, L, XL, XXL, XXXL)

        /**
         * Obtiene la lista de tallas infantiles
         */
        fun tallasInfantil(): List<Talla> = listOf(T2, T4, T6, T8, T10, T12, T14, T16)

        /**
         * Obtiene las tallas según el tipo de producto
         */
        fun porTipo(tipo: TipoProducto): List<Talla> {
            return when (tipo) {
                TipoProducto.ADULTO -> tallasAdulto()
                TipoProducto.INFANTIL -> tallasInfantil()
            }
        }

        /**
         * Obtiene las tallas según la categoría del producto
         * Para DTF debe especificarse el tipo
         */
        fun porCategoria(categoria: CategoriaProducto, tipo: TipoProducto = TipoProducto.ADULTO): List<Talla> {
            return when (categoria) {
                CategoriaProducto.SERIGRAFIA, CategoriaProducto.CORPORATIVA -> tallasAdulto()
                CategoriaProducto.DTF -> porTipo(tipo)
                CategoriaProducto.ACCESORIOS -> emptyList() // Accesorios no usan tallas
            }
        }
    }
}

