package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
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
