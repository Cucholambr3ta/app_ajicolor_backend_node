package com.example.appajicolorgrupo4.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Factory helper para crear ViewModels que requieren Application
 */
class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UsuarioViewModel::class.java) -> {
                UsuarioViewModel(application) as T
            }
            modelClass.isAssignableFrom(PedidosViewModel::class.java) -> {
                PedidosViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

/**
 * Helper Composable para obtener UsuarioViewModel con el factory correcto
 */
@Composable
fun usuarioViewModel(): UsuarioViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = AppViewModelFactory(context.applicationContext as Application)
    )
}

/**
 * Helper Composable para obtener PedidosViewModel con el factory correcto
 */
@Composable
fun pedidosViewModel(): PedidosViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = AppViewModelFactory(context.applicationContext as Application)
    )
}

