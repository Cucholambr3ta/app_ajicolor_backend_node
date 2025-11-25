package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PasswordRecoveryState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val recoveryCode: String? = null // Solo para testing
)

class PasswordRecoveryViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitInstance.api

    private val _state = MutableStateFlow(PasswordRecoveryState())
    val state: StateFlow<PasswordRecoveryState> = _state

    fun solicitarRecuperacion(email: String) {
        if (email.isBlank()) {
            _state.value = _state.value.copy(
                errorMsg = "El email no puede estar vacío"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMsg = null
            )

            try {
                val response = apiService.recoverPassword(mapOf("email" to email))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        recoveryCode = body?.get("recoveryCode") // Solo para testing
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMsg = errorBody
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = e.message ?: "Error de conexión"
                )
            }
        }
    }

    fun resetearContrasena(email: String, recoveryCode: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val response = apiService.resetPassword(mapOf(
                    "email" to email,
                    "recoveryCode" to recoveryCode,
                    "newPassword" to newPassword
                ))

                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Código inválido o expirado"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMsg = errorBody
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = e.message ?: "Error de conexión"
                )
            }
        }
    }

    fun resetState() {
        _state.value = PasswordRecoveryState()
    }
}
