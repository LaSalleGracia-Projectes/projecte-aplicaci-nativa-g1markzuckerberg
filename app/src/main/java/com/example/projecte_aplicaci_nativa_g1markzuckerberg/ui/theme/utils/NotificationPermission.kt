package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Pide el permiso POST_NOTIFICATIONS (Android 13+) y llama a onGranted() si se concede.
 */
object NotificationPermission {
    @Composable
    fun ensure(activity: ComponentActivity, onGranted: () -> Unit) {
        val requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted: Boolean ->
            if (granted) onGranted()
        }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
