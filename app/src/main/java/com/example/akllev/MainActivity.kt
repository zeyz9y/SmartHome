package com.example.akllev

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.akllev.bt.SpRepository
import com.example.akllev.ui.navigation.Navigation
import com.example.akllev.ui.theme.AkıllıEvTheme
import com.example.akllev.viewmodel.BluetoothSensorViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // ---- Bildirim izni (app-geneli, API 33+) ----
            val notifLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* sonucu loglamak istersen burada */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // ---- Repo + ViewModel (Bluetooth VM) ----
            val spRepo = remember { SpRepository(applicationContext) }
            val btVm   = remember { BluetoothSensorViewModel(spRepo) }


            val themeRepo = remember { com.example.akllev.repository.ThemeRepository(applicationContext) }
            val isDark by themeRepo.darkFlow.collectAsStateWithLifecycle(initialValue = false)
            val scope = rememberCoroutineScope()

            AkıllıEvTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalBluetoothVm provides btVm) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigation(
                            isDarkTheme = isDark,
                            onToggleTheme = { scope.launch { themeRepo.setDark(!isDark) } }
                        )
                    }
                }
            }
        }
    }
}
