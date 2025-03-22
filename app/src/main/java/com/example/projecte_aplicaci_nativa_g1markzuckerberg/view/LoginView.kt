package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel

// Color primario (ajustar según la guía de estilos)
private val BluePrimary @Composable get() = MaterialTheme.colorScheme.primary

@Composable
fun LoginView(
    navController: NavController,
    viewModel: LoginViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior con fondo azul y botón de volver atrás
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() } // Función para volver atrás
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_back), // Icono personalizado
                        contentDescription = "Volver",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Iniciar sesión",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Línea divisoria gris bajo la barra
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campo de texto para Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.emailicon),
                        contentDescription = "Correo",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Campo de texto para Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    val iconRes = if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Mostrar/Ocultar contraseña",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            // Texto "Olvidé mi contraseña"
            Text(
                text = "Olvidé mi contraseña",
                fontSize = 14.sp,
                color = BluePrimary,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .clickable {
                        // TODO: Navegar a pantalla de recuperar contraseña
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Iniciar sesión"
            Button(
                onClick = {
                    navController.navigate(Routes.HomeLoged.route)

                    // TODO: Lógica para iniciar sesión
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Iniciar sesión con Google"
            OutlinedButton(
                onClick = {
                    // TODO: Lógica para iniciar sesión con Google
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciar sesión con Google",
                    fontSize = 16.sp
                )
            }
        }
    }
}
