package com.example.appajicolorgrupo4.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji

/**
 * Carousel de ejemplo con imágenes múltiples
 * Muestra un carousel horizontal con navegación multi-browse
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CarouselExample_MultiBrowse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.polera_red_hot_chili_peppers, "Polera Red Hot Chili Peppers"),
            CarouselItem(1, R.drawable.polera_tool, "Polera Tool"),
            CarouselItem(2, R.drawable.polera_deftones, "Polera Deftones"),
            CarouselItem(3, R.drawable.polera_incubus, "Polera Incubus"),
            CarouselItem(4, R.drawable.jockey, "Jockey"),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Carousel de productos
 * Muestra productos en un carousel horizontal con título y precio
 * @param productos Lista de productos a mostrar
 * @param onProductClick Acción al hacer clic en un producto
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CarouselProductos(
    productos: List<ProductoCarousel>,
    onProductClick: (ProductoCarousel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { productos.count() },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp),
        preferredItemWidth = 200.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val producto = productos[i]

        Column(
            modifier = Modifier
                .clickable { onProductClick(producto) }
                .clip(MaterialTheme.shapes.large)
        ) {
            // Imagen del producto
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                painter = painterResource(id = producto.imageResId),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = producto.precio,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Carousel compacto de productos
 * Versión más pequeña ideal para secciones de "Productos Relacionados" o "Recomendados"
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CarouselProductosCompacto(
    productos: List<ProductoCarousel>,
    titulo: String = "Productos Destacados",
    onProductClick: (ProductoCarousel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Título de la sección
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AmarilloAji,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { productos.count() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            preferredItemWidth = 150.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val producto = productos[i]

            Column(
                modifier = Modifier
                    .clickable { onProductClick(producto) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen del producto
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(MaterialTheme.shapes.medium),
                    painter = painterResource(id = producto.imageResId),
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Precio
                Text(
                    text = producto.precio,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Carousel de imágenes de producto (para detalles)
 * Muestra múltiples imágenes de un mismo producto
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CarouselImagenesProducto(
    imagenes: List<Int>,
    modifier: Modifier = Modifier
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { imagenes.count() },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        preferredItemWidth = 300.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large),
                painter = painterResource(id = imagenes[i]),
                contentDescription = "Imagen del producto ${i + 1}",
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Data class para productos en el carousel
 */
data class ProductoCarousel(
    val id: String,
    val nombre: String,
    val precio: String,
    @DrawableRes val imageResId: Int,
    val categoria: String = "",
    val descripcion: String = ""
)

