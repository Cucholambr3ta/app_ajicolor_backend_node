package com.example.appajicolorgrupo4.ui.state

/**
 * Errores de validación por campo para el formulario de Usuario.
 * Usa null o cadena vacía cuando no hay error.
 */
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val direccion: String? = null,
    val aceptaTerminos: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(nombre, correo, telefono, clave, confirmarClave, direccion, aceptaTerminos).any { !it.isNullOrBlank() }
}
