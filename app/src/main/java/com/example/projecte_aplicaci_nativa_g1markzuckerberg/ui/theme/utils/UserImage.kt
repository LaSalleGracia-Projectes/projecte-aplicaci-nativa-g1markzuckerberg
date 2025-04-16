package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.network.AuthInterceptor
import okhttp3.OkHttpClient

@Composable
fun UserImage(url: String, modifier: Modifier = Modifier.size(48.dp)) {
    val context = LocalContext.current

    // Configura un OkHttpClient que incluya el interceptor de autenticación
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { RetrofitClient.authRepository.getToken() })
        .build()

    // Crea un ImageLoader personalizado
    val imageLoader = ImageLoader.Builder(context)
        .okHttpClient { okHttpClient }
        .build()

    // Construye la request para la imagen
    val request = ImageRequest.Builder(context)
        .data(url)
        .placeholder(R.drawable.fantasydraft)
        .error(R.drawable.fantasydraft)
        .build()

    Image(
        painter = rememberAsyncImagePainter(
            model = request,
            imageLoader = imageLoader
        ),
        contentDescription = "Imagen de usuario",
        modifier = modifier.clip(CircleShape)
    )
}
@Composable
fun GradientCircleImage(
    imageUrl: String,
    contentDescription: String,
    size: Dp,
    placeholderRes: Int,
    errorRes: Int,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    ),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.horizontalGradient(colors = gradientColors),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            placeholder = painterResource(id = placeholderRes),
            error = painterResource(id = errorRes),
            modifier = Modifier
                // Se reduce el tamaño para dejar ver el borde degradado
                .size(size * 0.85f)
                .clip(CircleShape)
        )
    }
}
