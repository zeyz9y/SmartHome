package com.example.akllev.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.akllev.LocalBluetoothVm
import com.example.akllev.bt.SpRepository
import com.example.akllev.model.SensorData
import com.example.akllev.ui.components.common.Section
import com.example.akllev.ui.components.controls.NavActionButton
import com.example.akllev.ui.components.dashboard.DashboardTopCards
import com.example.akllev.ui.components.devices.DeviceList
import com.example.akllev.ui.components.feedback.ConnectionStatusPillInline
import com.example.akllev.ui.components.permission.PermissionGuard
import com.example.akllev.ui.components.rooms.RoomSelector
import com.example.akllev.viewmodel.BluetoothSensorViewModel
import com.example.akllev.viewmodel.DeviceViewModel
import com.example.akllev.util.Prefs
import com.example.akllev.util.rememberNotifier
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.unit.dp
import com.example.akllev.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    onDevicesClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onSchedulesClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(Prefs.BT, Context.MODE_PRIVATE) }

    // Provider varsa onu kullan, yoksa yerel (fallback) VM yarat
    val providedVm = LocalBluetoothVm.current
    val btVm: BluetoothSensorViewModel = providedVm ?: viewModel<BluetoothSensorViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BluetoothSensorViewModel(SpRepository(context.applicationContext)) as T
            }
        }
    ).also { Log.w("Dashboard", "Using local fallback BT VM (provider missing)") }

    // --- UI state ---
    var deviceName by rememberSaveable {
        mutableStateOf(prefs.getString(Prefs.Keys.DEVICE_NAME, "H-C-2010-06-01") ?: "")
    }
    val data      by btVm.data.collectAsStateWithLifecycle(initialValue = SensorData())
    val connected by btVm.connected.collectAsStateWithLifecycle(initialValue = false)
    val busy      by btVm.busy.collectAsStateWithLifecycle(initialValue = false)
    val ledChecked = data.led ?: false

    val vm: DeviceViewModel = viewModel()
    var selectedRoom by remember { mutableStateOf("Salon") }
    val devicesForRoom by remember(selectedRoom, vm.devices) {
        mutableStateOf(vm.devices.filter { it.room == selectedRoom })
    }

    val (snackbarHost, notify) = rememberNotifier()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Akıllı Ev") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (isDarkTheme) "Açık tema" else "Koyu tema"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
        ) {
            // Bağlantı – PermissionGuard ile just-in-time izin
            item {
                Section("Bağlantı") {
                    val btPermissions = remember {
                        buildList {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                add(Manifest.permission.BLUETOOTH_CONNECT)
                                add(Manifest.permission.BLUETOOTH_SCAN)
                            }
                        }
                    }

                    PermissionGuard(
                        permissions = btPermissions,
                        rationale = { Text("Cihaza bağlanmak için Bluetooth izinlerine ihtiyacımız var.") }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = deviceName,
                                onValueChange = { deviceName = it },
                                label = { Text("Cihaz adı/MAC") },
                                placeholder = { Text("Örn: H-C-2010-06-01") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Filled.Bluetooth, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )

                            // Bağlan / Kes (tema primary)
                            Button(
                                onClick = {
                                    Log.d("Dashboard", "Connect pressed name=$deviceName connected=$connected")
                                    if (connected) {
                                        btVm.disconnect()
                                    } else {
                                        prefs.edit().putString(Prefs.Keys.DEVICE_NAME, deviceName).apply()
                                        btVm.connect(deviceName)
                                    }
                                },
                                enabled = !busy,
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                                )
                            ) {
                                Text(if (connected) "Kes" else "Bağlan")
                            }

                            if (busy || connected) {
                                ConnectionStatusPillInline(connected = connected, busy = busy)
                            }
                        }

                        if (busy) {
                            Spacer(Modifier.height(MaterialTheme.spacing.s))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }
                    }
                }
            }

            // Genel Durum
            item {
                Section("Genel Durum") {
                    DashboardTopCards(
                        temperature = data.temperature,
                        humidity    = data.humidity,
                        energy      = "1,2 kWh"
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.xs))
                    ListItem(
                        headlineContent = { Text("LED Durumu") },
                        trailingContent = {
                            Switch(
                                checked = ledChecked,
                                onCheckedChange = { on -> btVm.setLed(on) },
                                enabled = connected && !busy
                            )
                        }
                    )
                }
            }

            // Hızlı Kontroller
            item {
                Section("Hızlı Kontroller") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                    ) {
                        NavActionButton(
                            icon = Icons.AutoMirrored.Filled.List,
                            label = "Cihazlar",
                            onClick = onDevicesClick,
                            modifier = Modifier.weight(1f)
                        )
                        NavActionButton(
                            icon = Icons.Filled.Security,
                            label = "Güvenlik",
                            onClick = onAlertsClick,
                            modifier = Modifier.weight(1f)
                        )
                        NavActionButton(
                            icon = Icons.Filled.AccessTime,
                            label = "Zamanlayıcı",
                            onClick = onSchedulesClick,
                            modifier = Modifier.weight(1f),
                            forceCompact = true
                        )
                    }
                }
            }

            // Odalar
            item {
                Section("Odalar") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        RoomSelector(
                            selectedRoom   = selectedRoom,
                            rooms          = listOf("Salon", "Mutfak", "Yatak Odası", "Çocuk Odası"),
                            onRoomSelected = { selectedRoom = it }
                        )
                    }
                }
            }

            // Oda Cihazları
            item {
                Section("Oda Cihazları") {
                    Crossfade(targetState = devicesForRoom, label = "devices_cf") { list ->
                        if (list.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = MaterialTheme.spacing.m),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Bu odada cihaz yok", style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                DeviceList(
                                    devices = list,
                                    onToggleDevice = { device, _ -> vm.toggleDevice(device) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
