package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import android.app.Application
import androidx.annotation.DrawableRes
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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.factory.RegisterEmailViewModelFactory
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.LocalAppDarkTheme
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.CustomAlertDialogSingleButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientOutlinedButton
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.RegisterEmailViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as Application
    val registerViewModel: RegisterEmailViewModel = viewModel(
        factory = RegisterEmailViewModelFactory(
            context,
            RetrofitClient.authRepository
        )
    )
    RegisterEmailView(navController = navController, viewModel = registerViewModel)
}

@Composable
fun RegisterEmailView(
    navController: NavController,
    viewModel: RegisterEmailViewModel
) {
    // Detectar modo claro/oscuro
    val isDarkTheme = LocalAppDarkTheme.current
    val backgroundColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.background
    @DrawableRes
    val profileIconRes = if (isDarkTheme) R.drawable.ic_profile else R.drawable.ic_profile_black
    @DrawableRes
    val emailIconRes = if (isDarkTheme) R.drawable.ic_email_dark else R.drawable.ic_email

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

    val isButtonEnabled = username.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            termsAccepted &&
            (password == confirmPassword) &&
            isMinLength && hasUppercase && hasDigit

    val errorMessage by viewModel.errorMessage.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        GradientHeader(
            title = stringResource(R.string.registerMail_title),
            onBack = { navController.popBackStack() },
            height = 140.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text(stringResource(R.string.username), style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = profileIconRes),
                        contentDescription = null,
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

            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(R.string.Registeremail), style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = emailIconRes),
                        contentDescription = null,
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

            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(R.string.Registerpassword), style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = if (isDarkTheme) R.drawable.password_dark else R.drawable.password),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    val iconRes = if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text(stringResource(R.string.confirm_password), style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = if (isDarkTheme) R.drawable.password_dark else R.drawable.password),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    val iconRes = if (confirmPasswordVisible) R.drawable.visibility_on else R.drawable.visibility_off
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = stringResource(R.string.password_rule_length),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isMinLength) Color.Green else Color.Gray
                )
                Text(
                    text = stringResource(R.string.password_rule_uppercase),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasUppercase) Color.Green else Color.Gray
                )
                Text(
                    text = stringResource(R.string.password_rule_digit),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasDigit) Color.Green else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = if (isDarkTheme) Color.White else Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.terms),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                Text(
                    text = stringResource(R.string.register_button),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    errorMessage?.let {
        CustomAlertDialogSingleButton(
            title = stringResource(R.string.register_error_title),
            message = stringResource(R.string.register_error_message),
            onAccept = { viewModel.clearError() }
        )
    }
}
