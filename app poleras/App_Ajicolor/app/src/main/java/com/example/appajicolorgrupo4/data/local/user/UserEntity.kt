package com.example.appajicolorgrupo4.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity declara una tabla SQLite manejada por Room.
// tableName = "users" define el nombre exacto de la tabla.
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)    // Clave primaria autoincremental
    val id: Long = 0L,

    val nombre: String,                 // Nombre completo del usuario
    val correo: String,                 // Correo (idealmente único a nivel de negocio)
    val telefono: String = "",          // Teléfono del usuario
    val clave: String,                  // Contraseña (para demo; en prod usar hash)
    val direccion: String = ""          // Dirección del usuario
)