package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private val userDao: UserDao = mockk()

    @Before
    fun setUp() {
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `login returns success when credentials are correct`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password"
        val user = UserEntity(1, "Test User", email, "123456789", password, "Address")
        coEvery { userDao.getUserByEmail(email) } returns user

        // When
        val result = userRepository.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `login returns failure when password is incorrect`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "wrongpassword"
        val user = UserEntity(1, "Test User", email, "123456789", "password", "Address")
        coEvery { userDao.getUserByEmail(email) } returns user

        // When
        val result = userRepository.login(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Contraseña incorrecta", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login returns failure when user not found`() = runTest {
        // Given
        val email = "nonexistent@test.com"
        coEvery { userDao.getUserByEmail(email) } returns null

        // When
        val result = userRepository.login(email, "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Usuario no encontrado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register returns success when user is created`() = runTest {
        // Given
        val nombre = "New User"
        val correo = "new@test.com"
        val telefono = "123456789"
        val clave = "password"
        val direccion = "Address"
        val userId = 1L
        
        coEvery { userDao.getUserByEmail(correo) } returns null
        coEvery { userDao.insert(any()) } returns userId

        // When
        val result = userRepository.register(nombre, correo, telefono, clave, direccion)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userId, result.getOrNull())
        coVerify { userDao.insert(any()) }
    }

    @Test
    fun `register returns failure when email already exists`() = runTest {
        // Given
        val nombre = "New User"
        val correo = "existing@test.com"
        val telefono = "123456789"
        val clave = "password"
        val direccion = "Address"
        val existingUser = UserEntity(1, "Existing", correo, telefono, clave, direccion)
        
        coEvery { userDao.getUserByEmail(correo) } returns existingUser

        // When
        val result = userRepository.register(nombre, correo, telefono, clave, direccion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El correo ya está registrado", result.exceptionOrNull()?.message)
    }
}
