package com.example.appajicolorgrupo4.navigation
//Respeta la organización por capas (navigation separado de viewmodels y screens).

//Representa los distintos tipos de eventos de navegación que pueden ocurrir en la app.
//Estos eventos son emitidos por el ViewModel y escuchados por la MainActivity.
sealed class NavigationEvent {
    // Navegar a un destino específico con opciones asegura que todos los posibles eventos de navegación están tipados y centralizados en un solo lugar.
    // El compilador te obliga a manejar todos los casos en un when.
    data class NavigateTo(//Recibe un Screen (tu sealed class de rutas tipadas).Permite configurar popUpToRoute, inclusive y singleTop.
        //Esto te da control total sobre el back stack y evita duplicados en la pila de navegación.
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver una pantalla
    data object PopBackStack : NavigationEvent() //Retrocede una pantalla en la pila

    // Navegar hacia arriba en la jerarquía
    data object NavigateUp : NavigationEvent()//Sube en la jerarquía de navegación (similar a back, pero pensado para grafos anidados).
}
//La clase NavigationEvent encapsula todos los posibles eventos de navegación de la aplicación.
//De esta forma, las pantallas no llaman directamente al NavController, sino que emiten intenciones al ViewModel.
//El ViewModel expone estos eventos como un flujo (SharedFlow) y la MainActivity los escucha para ejecutar la acción correspondiente en el NavController.
//Esto asegura un flujo de navegación desacoplado, tipado y fácil de mantener.