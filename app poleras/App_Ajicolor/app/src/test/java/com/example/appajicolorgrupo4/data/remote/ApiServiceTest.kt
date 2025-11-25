package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.models.Product
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `testLogin with valid credentials returns UserEntity`() = runTest {
        // Given
        val responseJson = """
            {
                "id": 1,
                "nombre": "Test User",
                "correo": "test@example.com",
                "telefono": "912345678",
                "clave": "",
                "direccion": "Test Address"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json")
        )

        val loginRequest = LoginRequest("test@example.com", "password123")

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(1, response.body()?.id)
        assertEquals("Test User", response.body()?.nombre)
        assertEquals("test@example.com", response.body()?.correo)
    }

    @Test
    fun `testLogin with invalid credentials returns 401`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("Contraseña incorrecta")
        )

        val loginRequest = LoginRequest("test@example.com", "wrongpassword")

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
    }

    @Test
    fun `testLogin with non-existent user returns 404`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Usuario no encontrado")
        )

        val loginRequest = LoginRequest("nonexistent@example.com", "password")

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun `testRegister with valid data returns UserEntity`() = runTest {
        // Given
        val responseJson = """
            {
                "id": 2,
                "nombre": "New User",
                "correo": "newuser@example.com",
                "telefono": "987654321",
                "clave": "",
                "direccion": "New Address"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json")
        )

        val registerRequest = RegisterRequest(
            nombre = "New User",
            email = "newuser@example.com",
            password = "password123",
            telefono = "987654321",
            direccion = "New Address"
        )

        // When
        val response = apiService.register(registerRequest)

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        assertNotNull(response.body())
        assertEquals(2, response.body()?.id)
        assertEquals("New User", response.body()?.nombre)
    }

    @Test
    fun `testRegister with existing email returns 409`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setBody("El email ya está registrado")
        )

        val registerRequest = RegisterRequest(
            nombre = "Test",
            email = "existing@example.com",
            password = "password"
        )

        // When
        val response = apiService.register(registerRequest)

        // Then
        assertFalse(response.isSuccessful)
        assertEquals(409, response.code())
    }

    @Test
    fun `testGetProductos returns list of products`() = runTest {
        // Given
        val responseJson = """
            [
                {
                    "id": 1,
                    "nombre": "Polera Test",
                    "descripcion": "Test producto",
                    "precio": 15000.0,
                    "stock": 10,
                    "color": "Negro",
                    "imagenUrl": "test.jpg",
                    "categoria": "Serigrafía"
                },
                {
                    "id": 2,
                    "nombre": "Jockey Test",
                    "descripcion": "Test jockey",
                    "precio": 8000.0,
                    "stock": 5,
                    "color": "Azul",
                    "imagenUrl": "jockey.jpg",
                    "categoria": "Accesorios"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.getProductos()

        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(2, response.body()?.size)
        assertEquals("Polera Test", response.body()?.get(0)?.nombre)
        assertEquals(15000.0, response.body()?.get(0)?.precio, 0.01)
    }

    @Test
    fun `testGetProductoById returns product`() = runTest {
        // Given
        val responseJson = """
            {
                "id": 1,
                "nombre": "Polera Test",
                "descripcion": "Test producto",
                "precio": 15000.0,
                "stock": 10,
                "color": "Negro",
                "imagenUrl": "test.jpg",
                "categoria": "Serigrafía"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json")
        )

        // When
val response = apiService.getProductoById("1")


        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
assertEquals("1", response.body()?.id)

        assertEquals("Polera Test", response.body()?.nombre)
    }

    @Test
    fun `testGetProductoById with non-existent id returns 404`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        // When
val response = apiService.getProductoById("999")


        // Then
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }
@Test
fun `testGetPosts returns list of posts`() = runTest {
    // Given
    val responseJson =
            """
            [
                {
                    "_id": "101",
                    "titulo": "Post Test",
                    "contenido": "Contenido Test",
                    "fechaCreacion": "2023-01-01T00:00:00.000Z"
                }
            ]
        """.trimIndent()

    mockWebServer.enqueue(
            MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
                    .addHeader("Content-Type", "application/json")
    )

    // When
    val response = apiService.getPosts()

    // Then
    assertTrue(response.isSuccessful)
    assertNotNull(response.body())
    assertEquals(1, response.body()?.size)
    assertEquals("Post Test", response.body()?.first()?.titulo)
}

@Test
fun `testCreateOrder returns created order`() = runTest {
    // Given
    val responseJson =
            """
            {
                "_id": "order123",
                "numeroPedido": "ORD-001",
                "usuario": "user123",
                "productos": [],
                "subtotal": 1000,
                "impuestos": 0,
                "costoEnvio": 0,
                "total": 1000,
                "direccionEnvio": "Test Address",
                "telefono": "123456789",
                "metodoPago": "EFECTIVO",
                "estado": "CONFIRMADO"
            }
        """.trimIndent()

    mockWebServer.enqueue(
            MockResponse()
                    .setResponseCode(201)
                    .setBody(responseJson)
                    .addHeader("Content-Type", "application/json")
    )

    val order =
            com.example.appajicolorgrupo4.data.models.Order(
                    numeroPedido = "ORD-001",
                    usuario = "user123",
                    productos = emptyList(),
                    subtotal = 1000,
                    impuestos = 0,
                    costoEnvio = 0,
                    total = 1000,
                    direccionEnvio = "Test Address",
                    telefono = "123456789",
                    metodoPago = "EFECTIVO"
            )

    // When
    val response = apiService.createOrder(order)

    // Then
    assertTrue(response.isSuccessful)
    assertNotNull(response.body())
    assertEquals("ORD-001", response.body()?.numeroPedido)
}

@Test
fun `testGetOrdersByUser returns list of orders`() = runTest {
    // Given
    val responseJson =
            """
            [
                {
                    "_id": "order123",
                    "numeroPedido": "ORD-001",
                    "usuario": "user123",
                    "productos": [],
                    "subtotal": 1000,
                    "impuestos": 0,
                    "costoEnvio": 0,
                    "total": 1000,
                    "direccionEnvio": "Test Address",
                    "telefono": "123456789",
                    "metodoPago": "EFECTIVO",
                    "estado": "CONFIRMADO"
                }
            ]
        """.trimIndent()

    mockWebServer.enqueue(
            MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
                    .addHeader("Content-Type", "application/json")
    )

    // When
    val response = apiService.getOrdersByUser("user123")

    // Then
    assertTrue(response.isSuccessful)
    assertNotNull(response.body())
    assertEquals(1, response.body()?.size)
    assertEquals("ORD-001", response.body()?.first()?.numeroPedido)
}

}
