package com.example.appajicolorgrupo4.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.models.Post
import com.example.appajicolorgrupo4.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel que conecta la Lógica de Datos (Repository) con la Interfaz de Usuario (UI).
// HEREDA de 'ViewModel' para sobrevivir a cambios de configuración y tener acceso al
// 'viewModelScope'.
class PostViewModel(
        // 1. OBTENER ACCESO AL REPOSITORIO
        //    Inyectamos el repositorio en el constructor para facilitar el testing.
        //    Usamos un valor por defecto para no romper la instanciación actual.
        private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // 2. CREAR ESTADOS PARA LA UI
    //    Estos son los "canales" que la UI escuchará para saber cuándo actualizarse.
    //    Usamos el patrón: Mutable (privado) y StateFlow (público, de solo lectura).

    //    - Estado para la lista de posts:
    private val _posts = MutableStateFlow<List<Post>>(emptyList()) // Inicia como una lista vacía.
    val posts: StateFlow<List<Post>> = _posts // La UI observará este.

    //    - Estado para saber si estamos cargando datos:
    private val _isLoading = MutableStateFlow(false) // Inicia en 'false' (no estamos cargando).
    val isLoading: StateFlow<Boolean> =
            _isLoading // La UI observará este para mostrar/ocultar un spinner.

    // 3. CARGAR DATOS INICIALES
    //    El bloque 'init' se ejecuta una sola vez, cuando el ViewModel se crea.
    //    Es perfecto para ir a buscar los datos la primera vez que se abre la pantalla.
    init {
        fetchAllPosts()
    }

    // 4. DEFINIR ACCIONES
    // Estas son las funciones que la UI puede llamar para pedirle al ViewModel que haga algo.
    // Pide al Repositorio la lista completa de posts y actualiza los estados.
    fun fetchAllPosts() {
        // 'viewModelScope.launch' es OBLIGATORIO para llamar funciones 'suspend' de forma segura.
        // La corrutina se cancelará automáticamente si el usuario sale de la pantalla.
        viewModelScope.launch {

            // Ponemos el estado de carga en 'true' ANTES de la llamada de red.
            _isLoading.value = true

            try {
                // Pedimos los datos al repositorio. Esta es una llamada 'suspend'.
                val postList = repository.getAllPosts()

                // Si todo sale bien, actualizamos el estado '_posts' con la nueva lista.
                _posts.value = postList
            } catch (e: Exception) {
                // Si algo falla (ej. sin internet), lo registramos en el Logcat.
                Log.e("PostViewModel", "Error al obtener los posts: ${e.message}")
                // Aquí podríamos actualizar un estado de error para mostrar un mensaje al usuario.

            } finally {
                // Este bloque se ejecuta SIEMPRE, tanto si hubo éxito como si hubo error.
                // Es el lugar perfecto para asegurarnos de que el estado de carga vuelva a 'false'.
                _isLoading.value = false
            }
        }
    }
}
