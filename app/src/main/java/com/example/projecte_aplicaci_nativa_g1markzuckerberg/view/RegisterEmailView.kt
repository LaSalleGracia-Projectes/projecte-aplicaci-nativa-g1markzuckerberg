package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.RegisterEmailViewModelFactory

// Color principal (ajusta según tu guía de estilos)
private val BluePrimary @Composable get() = MaterialTheme.colorScheme.primary

@Composable
fun RegisterScreen(navController: NavController) {
    val registerViewModel : RegisterEmailViewModel = viewModel(
        factory = RegisterEmailViewModelFactory(RetrofitClient.authRepository)
    )
    RegisterEmailView(navController = navController, viewModel = registerViewModel)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmailView(
    navController: NavController,
    viewModel: RegisterEmailViewModel
) {
    val username by viewModel.username.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val confirmPassword by viewModel.confirmPassword.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior con botón de volver atrás
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() } // Navega hacia atrás
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_back), // Icono personalizado
                        contentDescription = "Volver",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Crear una cuenta",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rellena los datos para crear tu cuenta", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            // Campo para username
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text("Nombre de usuario") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Icono de usuario",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo para correo
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.emailicon),
                        contentDescription = "Icono de correo",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo para contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Icono de contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            Spacer(modifier = Modifier.height(16.dp))

            // Campo para repetir contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Repetir Contraseña") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Icono de repetir contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes = if (confirmPasswordVisible) R.drawable.visibility_on else R.drawable.visibility_off
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Mostrar/Ocultar contraseña",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Requisitos de la contraseña
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "• Debe incluir una mayúscula", fontSize = 12.sp, color = Color.Gray)
                Text(text = "• Debe incluir números", fontSize = 12.sp, color = Color.Gray)
                Text(text = "• La contraseña debe ser de al menos 6 caracteres", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Checkbox para términos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "He leído y acepto las condiciones legales y la política de privacidad de Fantasy Draft",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón para crear cuenta
            Button(
                onClick = {
                    viewModel.register {
                        navController.navigate(Routes.HomeLoged.route)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Crear cuenta", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
