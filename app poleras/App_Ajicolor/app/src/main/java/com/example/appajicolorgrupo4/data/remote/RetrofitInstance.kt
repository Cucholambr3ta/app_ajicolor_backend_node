package com.example.appajicolorgrupo4.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton para crear y gestionar la instancia de Retrofit.
// Al ser un 'object', Kotlin asegura que solo existirá una única instancia de RetrofitInstance
// durante todo el ciclo de vida de la aplicación, evitando la creación de múltiples
// objetos Retrofit que consumirían recursos innecesariamente.

object RetrofitInstance {
    // Usa esta IP si pruebas en un dispositivo físico (tu PC y celular deben estar en la misma
    // Wi-Fi)
    private const val BASE_URL = "http://192.168.1.93:8080/"
    // private const val BASE_URL = "http://10.0.2.2:8080/" // Usa esta para el Emulador

    // El bloque de código dentro de 'by lazy' solo se ejecutará la primera vez que se acceda
    // a la propiedad 'api'. En accesos posteriores, se devolverá el valor ya creado.
    // Exponemos la instancia de la API para que pueda ser usada por el Repositorio
    private val loggingInterceptor = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
