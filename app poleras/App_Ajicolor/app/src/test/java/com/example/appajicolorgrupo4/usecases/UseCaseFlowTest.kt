package com.example.appajicolorgrupo4.usecases

import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.FakePedidoRepository
import com.example.appajicolorgrupo4.viewmodel.FakeProductoRepository
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.ProductoViewModel
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseFlowTest {
    
    private lateinit var carritoViewModel: CarritoViewModel
    private lateinit var productoViewModel: ProductoViewModel
    private lateinit var pedidosViewModel: PedidosViewModel
    private lateinit var authViewModel: AuthViewModel
    
    private lateinit var fakeProductoRepository: FakeProductoRepository
    private lateinit var fakePedidoRepository: FakePedidoRepository
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    
    private lateinit var testDispatcher: TestDispatcher

    init {
        println("DEBUG: UseCaseFlowTest instance created")
    }

    @Before
    fun setup() {
        println("DEBUG: setup running")
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeProductoRepository = FakeProductoRepository()
        fakePedidoRepository = FakePedidoRepository()
        
        userRepository = io.mockk.mockk(relaxed = true)
        sessionManager = io.mockk.mockk(relaxed = true)
        
        val mockApplication = io.mockk.mockk<android.app.Application>(relaxed = true)
        
        productoViewModel = ProductoViewModel(fakeProductoRepository)
        carritoViewModel = CarritoViewModel()
        pedidosViewModel = PedidosViewModel(mockApplication, fakePedidoRepository)
        authViewModel = AuthViewModel(userRepository, sessionManager)
        println("DEBUG: setup finished")
    }

    @After
    fun tearDown() {
        println("DEBUG: tearDown running")
        Dispatchers.resetMain()
        println("DEBUG: tearDown finished")
    }

    @Test
    fun `testCartManagementFlow`() = runTest {
        println("DEBUG: Starting UseCase 9")
        
        backgroundScope.launch {
            carritoViewModel.total.collect { println("DEBUG: Total emitted: $it") }
        }
        backgroundScope.launch {
            carritoViewModel.subtotal.collect { println("DEBUG: Subtotal emitted: $it") }
        }
        backgroundScope.launch {
            carritoViewModel.iva.collect { println("DEBUG: IVA emitted: $it") }
        }
        backgroundScope.launch {
            carritoViewModel.costoEnvio.collect { println("DEBUG: CostoEnvio emitted: $it") }
        }
        
        val product = com.example.appajicolorgrupo4.data.ProductoCarrito(
            "1", "Polera", 1000, 1, 
            com.example.appajicolorgrupo4.data.Talla.M, 
            com.example.appajicolorgrupo4.data.ColorInfo("Red", androidx.compose.ui.graphics.Color.Red, "#FF0000"), 
            com.example.appajicolorgrupo4.data.CategoriaProducto.SERIGRAFIA, 0
        )

        println("DEBUG: Adding product: $product")
        // 9. Add to Cart
        carritoViewModel.agregarProducto(product)
        advanceUntilIdle()
        var cart = carritoViewModel.productos.value
        println("DEBUG: Cart size after add: ${cart.size}")
        assertEquals("Cart should have 1 item", 1, cart.size)

        // 10. View Cart (Implicit in checking 'cart')
        assertEquals("Polera", cart[0].nombre)

        // 11. Update Quantity
        println("DEBUG: Calling actualizarCantidad")
        carritoViewModel.actualizarCantidad(product, 2)
        advanceUntilIdle()
        println("DEBUG: Called actualizarCantidad")
        cart = carritoViewModel.productos.value
        println("DEBUG: Cart size after update: ${cart.size}")
        if (cart.isNotEmpty()) {
            println("DEBUG: Item quantity: ${cart[0].cantidad}")
        }
        assertEquals("Quantity should be 2", 2, cart[0].cantidad)
        
        val sub = carritoViewModel.subtotal.value
        val iva = carritoViewModel.iva.value
        val envio = carritoViewModel.costoEnvio.value
        val tot = carritoViewModel.total.value
        println("DEBUG: Sub=$sub, IVA=$iva, Envio=$envio, Total=$tot")
        
        // Subtotal: 2000, IVA: 380, Shipping: 5000 -> Total: 7380
        assertEquals("Total should update", 7380, tot)

        // 12. Remove from Cart
        val productToRemove = carritoViewModel.productos.value[0]
        carritoViewModel.eliminarProducto(productToRemove)
        cart = carritoViewModel.productos.value
        assertEquals("Cart should be empty", 0, cart.size)
    }

    @Test
    fun `testCheckoutAndOrderFlow`() = runTest(testDispatcher) {
        // Setup Cart with items
        val product = com.example.appajicolorgrupo4.data.ProductoCarrito(
            "1", "Polera", 1000, 1, 
            com.example.appajicolorgrupo4.data.Talla.M, 
            com.example.appajicolorgrupo4.data.ColorInfo("Red", androidx.compose.ui.graphics.Color.Red, "#FF0000"), 
            com.example.appajicolorgrupo4.data.CategoriaProducto.SERIGRAFIA, 0
        )
        
        // Ensure collectors are active for StateFlows
        backgroundScope.launch { carritoViewModel.total.collect {} }
        backgroundScope.launch { carritoViewModel.subtotal.collect {} }
        backgroundScope.launch { carritoViewModel.iva.collect {} }
        backgroundScope.launch { carritoViewModel.costoEnvio.collect {} }

        carritoViewModel.agregarProducto(product)
        advanceUntilIdle()

        // 13. Checkout
        val cartItems = carritoViewModel.productos.value
        val total = carritoViewModel.total.value
        assertEquals(1, cartItems.size)
        assertEquals(6190, total) // 1000 + 190 + 5000 = 6190
        
        // 14. Confirm Order (Create Pedido)
        val pedido = com.example.appajicolorgrupo4.data.PedidoCompleto(
            numeroPedido = "ALE00001",
            nombreUsuario = "Test User",
            productos = cartItems,
            subtotal = 1000.0,
            impuestos = 190.0,
            costoEnvio = 5000.0,
            total = total.toDouble(),
            direccionEnvio = "Test Address",
            telefono = "123456789",
            metodoPago = com.example.appajicolorgrupo4.data.MetodoPago.TARJETA_CREDITO,
            estado = com.example.appajicolorgrupo4.data.EstadoPedido.CONFIRMADO,
            fechaCreacion = System.currentTimeMillis()
        )
        
        // Mocking userId as Long
        pedidosViewModel.agregarPedido(pedido, 1L)
        advanceUntilIdle()
        
        // Verify order created
        val orders = pedidosViewModel.pedidos.value
        assertEquals(1, orders.size)
        assertEquals("ALE00001", orders[0].numeroPedido)
    }

    @Test
    fun `testAuthenticationFlow`() = runTest(testDispatcher) {
        println("DEBUG: Starting UseCase 1-8 (Authentication)")
        
        // 1. Register
        val registerState = authViewModel.register.value
        // Simulate user input
        authViewModel.onNameChange("Test User")
        authViewModel.onRegisterEmailChange("test@example.com")
        authViewModel.onTelefonoChange("123456789")
        authViewModel.onDireccionChange("Test Address")
        authViewModel.onRegisterPassChange("Password123!")
        authViewModel.onConfirmChange("Password123!")
        
        advanceUntilIdle()
        
        val updatedRegisterState = authViewModel.register.value
        assertEquals(true, updatedRegisterState.canSubmit)
        
        // Mock repository response
        io.mockk.coEvery { userRepository.register(any(), any(), any(), any(), any()) } returns Result.success(1L)
        
        authViewModel.submitRegister()
        advanceUntilIdle()
        
        assertEquals(true, authViewModel.register.value.success)
        
        // 2. Login
        authViewModel.onLoginEmailChange("test@example.com")
        authViewModel.onLoginPassChange("Password123!")
        advanceUntilIdle()
        
        assertEquals(true, authViewModel.login.value.canSubmit)
        
        // Mock login response
        val user = com.example.appajicolorgrupo4.data.local.user.UserEntity(
            1L, "Test User", "test@example.com", "123456789", "Password123!", "Test Address"
        )
        io.mockk.coEvery { userRepository.login(any(), any()) } returns Result.success(user)
        io.mockk.every { sessionManager.saveSession(any()) } returns Unit
        
        authViewModel.submitLogin()
        advanceUntilIdle()
        
        assertEquals(true, authViewModel.login.value.success)
    }

    @Test
    fun `testOrderHistoryFlow`() = runTest {
        println("DEBUG: Starting UseCase 19-20 (Order History)")
        
        // 19. View Order History
        val pedido1 = com.example.appajicolorgrupo4.data.PedidoCompleto(
            numeroPedido = "ALE00001",
            nombreUsuario = "Test User",
            productos = emptyList(),
            subtotal = 1000.0,
            impuestos = 190.0,
            costoEnvio = 5000.0,
            total = 6190.0,
            direccionEnvio = "Test Address",
            telefono = "123456789",
            metodoPago = com.example.appajicolorgrupo4.data.MetodoPago.TARJETA_CREDITO,
            estado = com.example.appajicolorgrupo4.data.EstadoPedido.CONFIRMADO,
            fechaCreacion = System.currentTimeMillis()
        )
        
        // Configure fake repository
        fakePedidoRepository.pedidosUsuarioFlow = kotlinx.coroutines.flow.flowOf(listOf(pedido1))
        
        // Trigger loading orders
        pedidosViewModel.cargarPedidosUsuario(1L)
        advanceUntilIdle()
        
        val orders = pedidosViewModel.pedidos.value
        assertEquals(1, orders.size)
        assertEquals("ALE00001", orders[0].numeroPedido)
        
        // 20. Track Order (View details)
        // Configure fake repository for single order
        fakePedidoRepository.pedidoPorNumero = pedido1
        
        val loadedOrder = pedidosViewModel.obtenerPedidoPorNumero("ALE00001")
        assertEquals("ALE00001", loadedOrder?.numeroPedido)
        assertEquals(com.example.appajicolorgrupo4.data.EstadoPedido.CONFIRMADO, loadedOrder?.estado)
    }
}
