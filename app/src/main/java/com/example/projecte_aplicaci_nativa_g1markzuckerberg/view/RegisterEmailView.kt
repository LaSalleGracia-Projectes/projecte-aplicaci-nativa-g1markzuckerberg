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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientOutlinedButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.RegisterEmailViewModelFactory

@Composable
fun RegisterScreen(navController: NavController) {
    val registerViewModel: RegisterEmailViewModel = viewModel(
        factory = RegisterEmailViewModelFactory(RetrofitClient.authRepository)
    )
    RegisterEmailView(navController = navController, viewModel = registerViewModel)
}

@Composable
fun RegisterEmailView(
    navController: NavController,
    viewModel: RegisterEmailViewModel
) {
    val username by viewModel.username.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val confirmPassword by viewModel.confirmPassword.observeAsState("")

    val isMinLength by viewModel.isMinLength.observeAsState(false)
    val hasUppercase by viewModel.hasUppercase.observeAsState(false)
    val hasDigit by viewModel.hasDigit.observeAsState(false)

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // Se habilita el botón solo si se cumplen todas las condiciones
    val isButtonEnabled = username.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            termsAccepted &&
            (password == confirmPassword) &&
            isMinLength && hasUppercase && hasDigit

    // Observar el mensaje de error
    val errorMessage by viewModel.errorMessage.observeAsState()

    // El resto del contenido
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GradientHeader(
            title = "Crear una cuenta",
            onBack = { navController.popBackStack() },
            height = 140.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rellena los datos para crear tu cuenta",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Campo de username
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = {
                    Text(
                        text = "Nombre de usuario",
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_black),
                        contentDescription = "Icono de usuario",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = {
                    Text(
                        text = "Correo",
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Icono de correo",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Campo para contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = {
                    Text(
                        text = "Contraseña",
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Icono de contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes =
                        if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off
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
                label = {
                    Text(
                        text = "Repetir Contraseña",
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Icono de repetir contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes =
                        if (confirmPasswordVisible) R.drawable.visibility_on else R.drawable.visibility_off
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
                Text(
                    text = "• La contraseña debe ser de al menos 6 caracteres",
                    fontSize = 12.sp,
                    color = if (isMinLength) Color.Green else Color.Gray
                )
                Text(
                    text = "• Debe incluir una mayúscula",
                    fontSize = 12.sp,
                    color = if (hasUppercase) Color.Green else Color.Gray
                )
                Text(
                    text = "• Debe incluir números",
                    fontSize = 12.sp,
                    color = if (hasDigit) Color.Green else Color.Gray
                )
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
            // Botón "Crear cuenta"
            GradientOutlinedButton(
                onClick = {
                    if (isButtonEnabled) {
                        viewModel.register {
                            navController.navigate(Routes.HomeLoged.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Crear cuenta",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (errorMessage != null) {
        CustomAlertDialogSingleButton(
            title = "Error al crear cuenta",
            message = "Este username o correo ya existen, por favor intenta con otros.",
            onAccept = { viewModel.clearError() }
        )
    }
}