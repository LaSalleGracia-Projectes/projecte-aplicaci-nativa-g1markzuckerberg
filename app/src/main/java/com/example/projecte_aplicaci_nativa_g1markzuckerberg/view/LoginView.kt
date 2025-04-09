package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.ForgotPasswordDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientOutlinedButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

// Color primario (ajustar según la guía de estilos)
private val BluePrimary @Composable get() = MaterialTheme.colorScheme.primary
@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(RetrofitClient.authRepository)
    )
    LoginView(navController = navController, viewModel = loginViewModel)
}

@Composable
fun LoginView(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val forgotPasswordMessage by viewModel.forgotPasswordMessage.observeAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.handleGoogleToken(idToken) {
                    navController.navigate(Routes.HomeLoged.route)
                }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Error: ${e.message}")
        }
    }

    LaunchedEffect(forgotPasswordMessage) {
        forgotPasswordMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            showForgotPasswordDialog = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initGoogle(context)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior: Usando GradientHeader
        GradientHeader(
            title = "Iniciar sesión",
            onBack = { navController.popBackStack() },
            height = 140.dp
        )

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
                onValueChange = viewModel::onEmailChanged,
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
                onValueChange = viewModel::onPasswordChanged,
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
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
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
                        showForgotPasswordDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))
            // Indicador de carga
            if (viewModel.isLoading.value == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Mostrar error si lo hay
            viewModel.errorMessage.value?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            // Botón "Iniciar sesión"
            GradientOutlinedButton(
                onClick = {
                    viewModel.login {
                        navController.navigate(Routes.HomeLoged.route)
                    }                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Iniciar sesión con Google"
            OutlinedButton(
                onClick = {
                    launcher.launch(viewModel.getGoogleSignInIntent())
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
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = { showForgotPasswordDialog = false },
                onSubmit = { email ->
                    viewModel.forgotPassword(email)
                }
            )
        }
    }
}
