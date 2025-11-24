package com.example.appajicolorgrupo4.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import com.example.appajicolorgrupo4.ui.state.UsuarioUiState
import com.example.appajicolorgrupo4.ui.state.UsuarioErrores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val pedidoRepository: PedidoRepository
    private val sessionManager: SessionManager

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    private val _registroResultado = MutableStateFlow<String?>(null)
    val registroResultado: StateFlow<String?> = _registroResultado.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _updateResultado = MutableStateFlow<String?>(null)
    val updateResultado: StateFlow<String?> = _updateResultado.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = UserRepository(database.userDao())
        pedidoRepository = PedidoRepository(database.pedidoDao())
        sessionManager = SessionManager(application)
        cargarPerfil()
    }

    fun actualizaNombre(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun actualizaCorreo(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun actualizaTelefono(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(telefono = soloNumeros, errores = it.errores.copy(telefono = null)) }
    }

    fun actualizaClave(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun actualizaConfirmarClave(valor: String) {
        _estado.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }
    }

    fun actualizaDireccion(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun actualizaAceptaTerminos(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null)) }
    }

    fun registrarUsuario(onSuccess: () -> Unit) {
        if (!validarFormulario()) {
            return
        }

        val estadoActual = _estado.value
        viewModelScope.launch {
            val resultado = repository.register(
                nombre = estadoActual.nombre,
                correo = estadoActual.correo,
                telefono = estadoActual.telefono,
                clave = estadoActual.clave,
                direccion = estadoActual.direccion
            )

            resultado.onSuccess { userId ->
                _registroResultado.value = "Usuario registrado exitosamente"
                _estado.update { UsuarioUiState() } // Limpiar formulario
                onSuccess()
            }.onFailure { error ->
                _registroResultado.value = error.message ?: "Error al registrar"
                _estado.update {
                    it.copy(
                        errores = it.errores.copy(
                            correo = if (error.message?.contains("ya está registrado") == true)
                                error.message else null
                        )
                    )
                }
            }
        }
    }

    fun limpiarMensajeRegistro() {
        _registroResultado.value = null
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            val user = sessionManager.getCurrentUser()
            if (user != null) {
                _currentUser.value = user
                _estado.update {
                    it.copy(
                        nombre = user.nombre,
                        correo = user.correo,
                        telefono = user.telefono,
                        direccion = user.direccion
                    )
                }
                _profileImageUri.value = sessionManager.getProfileImageUri()
            }
        }
    }

    fun saveSession(user: UserEntity) {
        sessionManager.saveSession(user)
        _currentUser.value = user
        cargarPerfil()
    }

    fun activarEdicion() {
        _isEditMode.value = true
    }

    fun cancelarEdicion() {
        _isEditMode.value = false
        cargarPerfil()
    }

    fun guardarCambiosPerfil(onSuccess: () -> Unit) {
        if (!validarFormularioPerfil()) {
            return
        }

        val currentUserId = _currentUser.value?.id
        val currentPassword = _currentUser.value?.clave

        if (currentUserId == null || currentPassword == null) {
            _updateResultado.value = "Error: No hay sesión activa"
            return
        }

        val estadoActual = _estado.value
        val updatedUser = UserEntity(
            id = currentUserId,
            nombre = estadoActual.nombre,
            correo = estadoActual.correo,
            telefono = estadoActual.telefono,
            clave = currentPassword,
            direccion = estadoActual.direccion
        )

        viewModelScope.launch {
            val resultado = repository.updateUser(updatedUser)
            resultado.onSuccess {
                sessionManager.updateSession(updatedUser)
                _currentUser.value = updatedUser
                _updateResultado.value = "Perfil actualizado exitosamente"
                _isEditMode.value = false
                onSuccess()
            }.onFailure { error ->
                _updateResultado.value = error.message ?: "Error al actualizar perfil"
            }
        }
    }

    fun cerrarSesion() {
        sessionManager.clearSession()
        _currentUser.value = null
        _estado.update { UsuarioUiState() }
        _isEditMode.value = false
        _profileImageUri.value = null
    }

    fun clearAllData(onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllUsers()
            pedidoRepository.deleteAllPedidos()
            sessionManager.clearSession()
            _currentUser.value = null
            _estado.update { UsuarioUiState() }
            onFinished()
        }
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun limpiarMensajeActualizacion() {
        _updateResultado.value = null
    }

    fun guardarFotoPerfil(uri: String?) {
        _profileImageUri.value = uri
        sessionManager.saveProfileImageUri(uri)
    }

    fun eliminarFotoPerfil() {
        _profileImageUri.value = null
        sessionManager.clearProfileImage()
    }

    private fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        var valido = true
        val errores = UsuarioErrores().copy(
            nombre = if (estadoActual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = if (estadoActual.correo.isBlank() || !estadoActual.correo.contains("@")) "Correo inválido" else null,
            telefono = if (estadoActual.telefono.isBlank()) "El teléfono es obligatorio" else if (estadoActual.telefono.length < 8) "El teléfono debe tener al menos 8 dígitos" else null,
            clave = if (estadoActual.clave.length < 6) "La clave debe tener al menos 6 caracteres" else null,
            confirmarClave = if (estadoActual.confirmarClave.isBlank()) "Debe confirmar la contraseña" else if (estadoActual.clave != estadoActual.confirmarClave) "Las contraseñas no coinciden" else null,
            aceptaTerminos = if (!estadoActual.aceptaTerminos) "Debes aceptar los términos" else null
        )

        if (errores.hayErrores) {
            valido = false
        }

        _estado.update { it.copy(errores = errores) }
        return valido
    }

    private fun validarFormularioPerfil(): Boolean {
        val estadoActual = _estado.value
        var valido = true
        val errores = UsuarioErrores().copy(
            nombre = if (estadoActual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = if (estadoActual.correo.isBlank() || !estadoActual.correo.contains("@")) "Correo inválido" else null,
            telefono = if (estadoActual.telefono.isBlank()) "El teléfono es obligatorio" else if (estadoActual.telefono.length < 8) "El teléfono debe tener al menos 8 dígitos" else null,
            direccion = if (estadoActual.direccion.isBlank()) "La dirección es obligatoria" else null
        )

        if (errores.hayErrores) {
            valido = false
        }

        _estado.update { it.copy(errores = errores) }
        return valido
    }
}
