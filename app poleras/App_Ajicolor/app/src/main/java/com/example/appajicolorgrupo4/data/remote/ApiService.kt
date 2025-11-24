package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.models.Post
import com.example.appajicolorgrupo4.data.models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz que define las operaciones de la API utilizando Retrofit.
 * Cada método en esta interfaz corresponde a un endpoint de la API.
 * Retrofit se encargará de generar la implementación de esta interfaz
 * para realizar las llamadas de red reales.
 */
interface ApiService {

    // ==================== AUTENTICACIÓN ====================
    
    /**
     * Login de usuario
     * POST /api/v1/usuarios/login
     */
    @POST("api/v1/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserEntity>
    
    /**
     * Registro de nuevo usuario
     * POST /api/v1/usuarios/register
     */
    @POST("api/v1/usuarios/register")
    suspend fun register(@Body user: RegisterRequest): Response<UserEntity>
    
    // ==================== PRODUCTOS ====================
    
    /**
     * Obtiene lista de productos
     * GET /api/v1/productos
     */
    @GET("api/v1/productos")
    suspend fun getProductos(): Response<List<Product>>
    
    /**
     * Obtiene un producto por ID
     * GET /api/v1/productos/{id}
     */
    @GET("api/v1/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<Product>
    
    // ==================== POSTS (Para admin) ====================
    
    /**
     * Obtiene la lista completa de todas las publicaciones (posts) desde la API.
     * La anotación @GET especifica el tipo de petición HTTP y el endpoint relativo.
     * @return Un objeto Response que contiene una lista de objetos Post.
     * Usar Response<T> nos permite verificar el código de estado HTTP y manejar errores
     */
    @GET("api/v1/posts")  // Asumiendo que posts es parte de algún microservicio
    suspend fun getPosts(): Response<List<Post>>

    /**
     * Obtiene una única publicación por su ID.
     * El valor {id} en la URL será reemplazado dinámicamente por el parámetro
     * del método anotado con @Path.
     *
     * @param postId El ID de la publicación que se desea obtener.
     * @return Un objeto Response que contiene un único objeto Post.
     */
    @GET("api/v1/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Int): Response<Post>

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
