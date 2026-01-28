package com.example.akllev.ui.components.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun PermissionGuard(
    permissions: List<String>,
    rationale: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var requestedOnce by remember { mutableStateOf(false) }

    fun isGranted() = permissions.all {
        ContextCompat.checkSelfPermission(context, it) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun isPermanentlyDenied(): Boolean {
        if (activity == null) return false
        return permissions.any { perm ->
            ContextCompat.checkSelfPermission(context, perm) !=
                    PackageManager.PERMISSION_GRANTED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity, perm) &&
                    requestedOnce
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { requestedOnce = true }

    when {
        isGranted() -> content()
        isPermanentlyDenied() -> {
            val appDetails = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            AlertDialog(
                onDismissRequest = {},
                title = { Text("İzin gerekli") },
                text = rationale ?: { Text("Devam etmek için gerekli izni Ayarlar’dan verin.") },
                confirmButton = {
                    TextButton(onClick = { context.startActivity(appDetails) }) {
                        Text("Ayarları Aç")
                    }
                }
            )
        }
        else -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("İzin gerekli") },
                text = rationale ?: { Text("Bu özelliği kullanmak için izne ihtiyacımız var.") },
                confirmButton = {
                    TextButton(onClick = { launcher.launch(permissions.toTypedArray()) }) {
                        Text("İzin Ver")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { /* kapat */ }) { Text("Daha Sonra") }
                }
            )
        }
    }
}
