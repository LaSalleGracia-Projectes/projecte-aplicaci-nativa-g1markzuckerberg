package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.factory.LoginViewModelFactory
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.ForgotPasswordDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as Application
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            context,
            RetrofitClient.authRepository
        )
    )
    LoginView(navController = navController, viewModel = loginViewModel)
}


@Composable
fun LoginView(
    navController: NavController,
    viewModel: LoginViewModel
) {
    // Detectar tema claro/oscuro
    val isDarkTheme = LocalAppDarkTheme.current
    val backgroundColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.background

    // Recursos de iconos dinámicos
    @DrawableRes
    val emailIconRes = if (isDarkTheme) R.drawable.ic_email_dark else R.drawable.ic_email
    @DrawableRes
    val passwordIconRes = if (isDarkTheme) R.drawable.password_dark else R.drawable.password
    @DrawableRes
    val visibilityOnIconRes = if (isDarkTheme) R.drawable.visibility_on_dark else R.drawable.visibility_on
    @DrawableRes
    val visibilityOffIconRes = if (isDarkTheme) R.drawable.visibility_dark else R.drawable.visibility_off

    // Colores para el botón Google
    val outlinedButtonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isDarkTheme) Color.DarkGray else Color.White,
        contentColor   = if (isDarkTheme) Color.LightGray else Color.Black
    )

    // Estados observados
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val forgotPasswordMessage by viewModel.forgotPasswordMessage.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    // Diálogos
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let {
                viewModel.handleGoogleToken(it) { navController.navigate(Routes.HomeLoged.route) }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Error: ${e.message}")
        }
    }

    // Efectos secundarios
    LaunchedEffect(forgotPasswordMessage) {
        forgotPasswordMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            showForgotPasswordDialog = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initGoogle(context)
    }
    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Encabezado
        GradientHeader(
            title = stringResource(R.string.login),
            onBack = { navController.popBackStack() },
            height = 140.dp
        )
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )

        // Contenido de login
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChanged,
                label = { Text(stringResource(R.string.email), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = emailIconRes),
                        contentDescription = stringResource(R.string.email),
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text(stringResource(R.string.password), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = passwordIconRes),
                        contentDescription = stringResource(R.string.password),
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Image(
                            painter = painterResource(id = if (passwordVisible) visibilityOnIconRes else visibilityOffIconRes),
                            contentDescription = stringResource(R.string.toggle_password_visibility),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            // Olvidé contraseña
            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .clickable { showForgotPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading indicator
            if (viewModel.isLoading.value == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Botón login principal (azul con texto blanco)
            Button(
                onClick = { viewModel.login { navController.navigate(Routes.HomeLoged.route) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Google
            OutlinedButton(
                onClick = { launcher.launch(viewModel.getGoogleSignInIntent()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = outlinedButtonColors
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = stringResource(R.string.google_logo),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.login_with_google),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Diálogos fuera de la columna scrollable
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = { showForgotPasswordDialog = false },
                onSubmit = { email -> viewModel.forgotPassword(email) }
            )
        }
    }

    if (showErrorDialog && errorMessage != null) {
        CustomAlertDialogSingleButton(
            title = stringResource(R.string.auth_error),
            message = stringResource(R.string.invalid_credentials),
            onAccept = {
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }
}
