package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    // Sobrescribe más colores si lo deseas para modo oscuro
    background = Color(0xFF121212),
    surface = Color(0xFF1D1D1D),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    tertiary = Pink40,
    onTertiary = Color.White,

    // Sobrescribe con tu guía de estilos
    background = BackgroundLight,
    onBackground = Color.Black,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight

    /* Otros colores opcionales:
       surface = Color.White,
       onSurface = Color.Black,
       etc.
    */
)

@Composable
fun Projecteaplicacinativag1markzuckerbergTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color es para Android 12+; lo mantengo para que puedas decidir si lo usas
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // ver Typography.kt
        content = content
    )
}
