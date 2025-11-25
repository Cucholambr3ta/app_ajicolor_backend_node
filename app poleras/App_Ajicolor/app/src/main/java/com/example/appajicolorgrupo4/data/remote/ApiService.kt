package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.models.Post
import com.example.appajicolorgrupo4.data.models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

/**
 * Interfaz que define las operaciones de la API utilizando Retrofit.
 */
interface ApiService {

    // ==================== AUTENTICACIÓN ====================
    
    @POST("api/v1/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserEntity>
    
    @POST("api/v1/usuarios/register")
    suspend fun register(@Body user: RegisterRequest): Response<UserEntity>
    
    // ==================== PRODUCTOS ====================
    
    @GET("api/v1/productos")
    suspend fun getProductos(): Response<List<Product>>
    
    @GET("api/v1/productos/{id}")
    suspend fun getProductoById(@Path("id") id: String): Response<Product>

    @POST("api/v1/productos")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("api/v1/productos/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Response<Product>

    @DELETE("api/v1/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Void>
    
    // ==================== POSTS (Para admin) ====================
    
    @GET("api/v1/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("api/v1/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Int): Response<Post>

    // ==================== PEDIDOS ====================

    @POST("api/v1/pedidos")
    suspend fun createOrder(@Body order: com.example.appajicolorgrupo4.data.models.Order): Response<com.example.appajicolorgrupo4.data.models.Order>

    @GET("api/v1/pedidos")
    suspend fun getAllOrders(): Response<List<com.example.appajicolorgrupo4.data.models.Order>>

    @GET("api/v1/pedidos/usuario/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: String): Response<List<com.example.appajicolorgrupo4.data.models.Order>>

    @PUT("api/v1/pedidos/{id}/estado")
    suspend fun updateOrderStatus(@Path("id") id: String, @Body status: Map<String, String>): Response<com.example.appajicolorgrupo4.data.models.Order>

    // ==================== PASSWORD RECOVERY ====================

    @POST("api/v1/usuarios/recover")
    suspend fun recoverPassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("api/v1/usuarios/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Response<Map<String, String>>
}

// DTOs para login/register
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val direccion: String?,
    val rol: String? = "USER"
)
