package com.example.appajicolorgrupo4.domain.validation

/**
 * Funciones de validación para formularios
 */

// Valida email
fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "El correo es obligatorio"
        !email.contains("@") -> "Correo inválido"
        !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> "Formato de correo inválido"
        else -> null
    }
}

// Valida nombre (solo letras y espacios)
fun validateNameLettersOnly(name: String): String? {
    return when {
        name.isBlank() -> "El nombre es obligatorio"
        name.length < 2 -> "El nombre debe tener al menos 2 caracteres"
        !name.all { it.isLetter() || it.isWhitespace() } -> "El nombre solo puede contener letras"
        else -> null
    }
}

// Valida teléfono (solo dígitos)
fun validatePhoneDigitsOnly(phone: String): String? {
    return when {
        phone.isBlank() -> "El teléfono es obligatorio"
        phone.length < 8 -> "El teléfono debe tener al menos 8 dígitos"
        phone.length > 15 -> "El teléfono no puede tener más de 15 dígitos"
        !phone.all { it.isDigit() } -> "El teléfono solo puede contener números"
        else -> null
    }
}

// Valida contraseña segura
fun validateStrongPassword(password: String): String? {
    return when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        password.length < 8 -> "Se recomienda al menos 8 caracteres para mayor seguridad"
        else -> null
    }
}

// Valida confirmación de contraseña
fun validateConfirm(password: String, confirm: String): String? {
    return when {
        confirm.isBlank() -> "Debes confirmar la contraseña"
        confirm != password -> "Las contraseñas no coinciden"
        else -> null
    }
}

// Valida dirección
fun validateDireccion(direccion: String): String? {
    return when {
        direccion.isBlank() -> "La dirección es obligatoria"
        direccion.length < 5 -> "La dirección debe tener al menos 5 caracteres"
        else -> null
    }
}

