package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.PedidoCompleto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun DetallePedidoDialogContent(
    pedido: PedidoCompleto,
    formatoMoneda: NumberFormat,
    formatoFecha: SimpleDateFormat
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Pedido: ${pedido.numeroPedido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Fecha: ${formatoFecha.format(Date(pedido.fechaCreacion))}", style = MaterialTheme.typography.bodyMedium)
                Text("Estado: ${pedido.estado.displayName}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            Text("Productos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(pedido.productos) { producto ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(producto.nombre, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${producto.cantidad} x ${formatoMoneda.format(producto.precio)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(formatoMoneda.format(producto.subtotal()), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
             Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(formatoMoneda.format(pedido.subtotal), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Impuestos (IVA 19%)", style = MaterialTheme.typography.bodyMedium)
                    Text(formatoMoneda.format(pedido.impuestos), style = MaterialTheme.typography.bodyMedium)
                }
                 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Costo de Envío", style = MaterialTheme.typography.bodyMedium)
                    Text(formatoMoneda.format(pedido.costoEnvio), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Pagado", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = formatoMoneda.format(pedido.total),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
        
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Información de Envío", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Dirección: ${pedido.direccionEnvio}", style = MaterialTheme.typography.bodyMedium)
                Text("Teléfono: ${pedido.telefono}", style = MaterialTheme.typography.bodyMedium)
                if(pedido.notasAdicionales.isNotBlank()){
                     Text("Notas: ${pedido.notasAdicionales}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
