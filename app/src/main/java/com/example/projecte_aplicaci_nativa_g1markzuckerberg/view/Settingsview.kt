package com.example.projecte_aplicaci_nativa_g1markzuckerberg.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.data.ThemePreferences
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.nav.Routes
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.ContactFormDialog
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.GradientHeader
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel,
    themePrefs: ThemePreferences
) {
    SettingsView(navController, viewModel, themePrefs)
}

@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel,
    themePrefs: ThemePreferences

) {
    // recogemos el estado actual del switch desde DataStore
    val isDark by themePrefs.isDarkModeFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    var showContactDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val dialogTitle by remember { mutableStateOf<String?>(null) }
    dialogTitle?.let { }

    val headerHeight = 110.dp
    BackHandler(enabled = true) {}

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeight)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item {
                ExpandableSettingsCard(
                    title = stringResource(R.string.creators_title),
                    body = stringResource(R.string.creators_body)
                )
            }
            item {
                SettingsCard(stringResource(R.string.contact)) {
                    showContactDialog = true
                }
            }
            item {
                DarkModeCard(isDark) {
                    // 2) Cuando el usuario toca el switch, persiste el cambio
                    scope.launch {
                        themePrefs.setDarkMode(!isDark)
                    }
                }
            }
            item {
                SettingsCard(stringResource(R.string.privacy_title)) {
                    showPrivacyDialog = true
                }
            }
            item {
                ExpandableSettingsCard(
                    title = stringResource(R.string.api_title),
                    body = stringResource(R.string.api_body)
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.logout))
                }
            }

            if (isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            errorMessage?.let { msg ->
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(msg, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (showPrivacyDialog) {
                item {
                    PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        Box(
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            GradientHeader(title = stringResource(R.string.settings_title))
        }
    }

    if (showContactDialog) {
        ContactFormDialog(
            onDismiss = { showContactDialog = false },
            onSubmit = { msg ->
                viewModel.sendContactForm(msg)
                showContactDialog = false
            }
        )
    }

    val contactResult by viewModel.contactResult.observeAsState()
    LaunchedEffect(contactResult) {
        contactResult?.let {
            viewModel.clearContactResult()
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick(title) },
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun DarkModeCard(
    isDark: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.dark_mode), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isDark, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
fun ExpandableSettingsCard(
    title: String,
    body: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.privacy_title),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_point1),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_body1),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.privacy_point2),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_body2),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.privacy_point3),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_body3),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.privacy_point4),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_body4),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.privacy_point5),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_body5),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = stringResource(R.string.Settingsclose),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
