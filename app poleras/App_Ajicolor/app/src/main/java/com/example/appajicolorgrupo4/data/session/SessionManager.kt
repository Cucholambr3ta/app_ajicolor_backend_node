package com.example.appajicolorgrupo4.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.appajicolorgrupo4.data.local.user.UserEntity

/**
 * Gestiona la sesión del usuario usando SharedPreferences.
 * Almacena los datos del usuario logueado.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_session",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
    }

    /**
     * Guarda la sesión del usuario
     */
    fun saveSession(user: UserEntity) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.nombre)
            putString(KEY_USER_EMAIL, user.correo)
            putString(KEY_USER_PHONE, user.telefono)
            putString(KEY_USER_ADDRESS, user.direccion)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Obtiene el usuario de la sesión actual
     */
    fun getCurrentUser(): UserEntity? {
        if (!isLoggedIn()) return null

        val id = prefs.getLong(KEY_USER_ID, 0L)
        val nombre = prefs.getString(KEY_USER_NAME, "") ?: ""
        val correo = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val telefono = prefs.getString(KEY_USER_PHONE, "") ?: ""
        val direccion = prefs.getString(KEY_USER_ADDRESS, "") ?: ""

        return if (id > 0) {
            UserEntity(id = id, nombre = nombre, correo = correo, telefono = telefono, clave = "", direccion = direccion)
        } else null
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Cierra la sesión y limpia todos los datos
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /**
     * Actualiza los datos del usuario en la sesión
     */
    fun updateSession(user: UserEntity) {
        saveSession(user)
    }

    /**
     * Guarda la URI de la foto de perfil
     */
    fun saveProfileImageUri(uri: String?) {
        prefs.edit().apply {
            putString(KEY_PROFILE_IMAGE_URI, uri)
            apply()
        }
    }

    /**
     * Obtiene la URI de la foto de perfil guardada
     */
    fun getProfileImageUri(): String? {
        return prefs.getString(KEY_PROFILE_IMAGE_URI, null)
    }

    /**
     * Elimina la foto de perfil
     */
    fun clearProfileImage() {
        prefs.edit().apply {
            remove(KEY_PROFILE_IMAGE_URI)
            apply()
        }
    }
}
