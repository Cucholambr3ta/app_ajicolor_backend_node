package com.example.appajicolorgrupo4.viewmodel

import android.app.Application
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.data.PedidoCompleto
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosViewModelTest {

    private lateinit var viewModel: PedidosViewModel
    private lateinit var repository: FakePedidoRepository
    private val application = mockk<Application>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakePedidoRepository()
        viewModel = PedidosViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `agregarPedido updates state on success`() = runTest {
        // Given
        val pedido = PedidoCompleto(
            numeroPedido = "123",
            nombreUsuario = "User",
            productos = emptyList(),
            subtotal = 1000.0,
            impuestos = 190.0,
            costoEnvio = 0.0,
            total = 1190.0,
            direccionEnvio = "Address",
            telefono = "123456",
            metodoPago = MetodoPago.EFECTIVO,
            estado = EstadoPedido.CONFIRMADO,
            fechaCreacion = System.currentTimeMillis()
        )
        repository.guardarResult = Result.success("123")

        // When
        val result = viewModel.agregarPedido(pedido, 1L)

        // Then
        assertEquals(Result.success("123"), result)
        assertEquals(listOf(pedido), viewModel.pedidos.value)
        assertEquals("123", viewModel.ultimoPedidoGuardado.value)
    }

    @Test
    fun `cargarPedidosUsuario updates state`() = runTest {
        // Given
        val pedido = PedidoCompleto(
            numeroPedido = "123",
            nombreUsuario = "User",
            productos = emptyList(),
            subtotal = 1000.0,
            impuestos = 190.0,
            costoEnvio = 0.0,
            total = 1190.0,
            direccionEnvio = "Address",
            telefono = "123456",
            metodoPago = MetodoPago.EFECTIVO,
            estado = EstadoPedido.CONFIRMADO,
            fechaCreacion = System.currentTimeMillis()
        )
        repository.pedidosUsuarioFlow = flowOf(listOf(pedido))

        // When
        viewModel.cargarPedidosUsuario(1L)

        // Then
        assertEquals(listOf(pedido), viewModel.pedidos.value)
    }
}
