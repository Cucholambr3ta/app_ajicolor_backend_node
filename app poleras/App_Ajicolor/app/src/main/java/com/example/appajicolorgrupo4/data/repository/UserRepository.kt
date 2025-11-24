package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun register(nombre: String, correo: String, telefono: String, clave: String, direccion: String): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Verificar si el correo ya existe
                if (userDao.getUserByEmail(correo) != null) {
                    return@withContext Result.failure(Exception("El correo ya está registrado"))
                }

                // Crear y guardar el nuevo usuario
                val newUser = UserEntity(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    clave = clave, // En una app real, hashear la contraseña
                    direccion = direccion
                )
                val userId = userDao.insert(newUser)
                Result.success(userId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun login(correo: String, clave: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmail(correo)
                if (user == null) {
                    Result.failure(Exception("Usuario no encontrado"))
                } else if (user.clave != clave) { // Comparación directa (insegura)
                    Result.failure(Exception("Contraseña incorrecta"))
                } else {
                    Result.success(user)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.update(user)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteAllUsers() {
        withContext(Dispatchers.IO) {
            userDao.deleteAll()
        }
    }
}
