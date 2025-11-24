package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appajicolorgrupo4.navigation.NavigationEvent
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel central para la navegación.
 *
 * Expone funciones que emiten eventos de tipo [NavigationEvent],
 * representando intenciones de navegación.
 * Las pantallas (UI) llaman a estas funciones en lugar de usar directamente NavController.
 * La MainActivity observa estos eventos y los traduce en acciones reales sobre NavController.
 * De esta forma se logra:
 *  - Separación de responsabilidades.
 *  - Navegación desacoplada y coherente con el patrón MVVM.
 */
class MainViewModel : ViewModel() {

    // Flujo interno mutable de eventos de navegación
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Flujo expuesto de solo lectura para que la UI observe
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    /**
     * Navegar a una ruta específica.
     * @param screen Ruta tipada definida en [Screen].
     */
    fun navigateTo(screen: Screen) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen))
        }
    }

    /**
     * Volver a la pantalla anterior en la pila de navegación.
     */
    fun navigateBack() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    /**
     * Navegar hacia arriba en la jerarquía de navegación.
     * Similar a back, pero pensado para grafos anidados.
     */
    fun navigateUp() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}
