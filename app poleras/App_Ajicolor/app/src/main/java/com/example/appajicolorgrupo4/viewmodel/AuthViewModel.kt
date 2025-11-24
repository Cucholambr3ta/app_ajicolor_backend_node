package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appajicolorgrupo4.domain.validation.*
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager

// ----------------- ESTADOS DE UI (observable con StateFlow) -----------------

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val correoError: String? = null,
    val claveError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val isAdmin: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val confirm: String = "",

    val nombreError: String? = null,
    val correoError: String? = null,
    val claveError: String? = null,
    val direccionError: String? = null,
    val telefonoError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// ----------------- VIEWMODEL -----------------

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Flujos de estado para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- LOGIN: handlers y envío -----------------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(correo = value, correoError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(clave = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.correoError == null &&
                s.correo.isNotBlank() &&
                s.clave.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            // HARDCODED ADMIN CHECK
            if (s.correo.trim() == "admin@ajicolor.cl" && s.clave == "ajicolor") {
                // Create a fake admin user session
                val adminUser = com.example.appajicolorgrupo4.data.local.user.UserEntity(
                    id = 9999,
                    nombre = "Administrador",
                    correo = "admin@ajicolor.cl",
                    clave = "ajicolor",
                    telefono = "000000000",
                    direccion = "Oficina Central"
                )
                sessionManager.saveSession(adminUser)
                // We can use a special flag or just rely on the email in the UI to navigate
                _login.update { it.copy(isSubmitting = false, success = true, isAdmin = true, errorMsg = null) }
                return@launch
            }

            val result = repository.login(s.correo.trim(), s.clave)

            _login.update {
                if (result.isSuccess) {
                    // Guardar la sesión del usuario
                    result.getOrNull()?.let { user ->
                        sessionManager.saveSession(user)
                    }
                    it.copy(isSubmitting = false, success = true, isAdmin = false, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false, success = false, isAdmin = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nombre = filtered, nombreError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(correo = value, correoError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onDireccionChange(value: String) {
        _register.update {
            it.copy(direccion = value, direccionError = validateDireccion(value))
        }
        recomputeRegisterCanSubmit()
    }

    fun onTelefonoChange(value: String) {
        // Filtrar solo números
        val soloNumeros = value.filter { it.isDigit() }
        _register.update {
            it.copy(telefono = soloNumeros, telefonoError = validatePhoneDigitsOnly(soloNumeros))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(clave = value, claveError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.clave, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.clave, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nombreError, s.correoError, s.direccionError, s.telefonoError, s.claveError, s.confirmError).all { it == null }
        val filled = s.nombre.isNotBlank() && s.correo.isNotBlank() && s.direccion.isNotBlank() && s.telefono.isNotBlank() && s.clave.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            val result = repository.register(
                nombre = s.nombre.trim(),
                correo = s.correo.trim(),
                telefono = s.telefono.trim(),
                clave = s.clave,
                direccion = s.direccion.trim()
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}
