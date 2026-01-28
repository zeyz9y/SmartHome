package com.example.akllev.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.akllev.util.DeviceApi
import com.example.akllev.ui.components.controls.TransportChip
import com.example.akllev.util.Prefs
import com.example.akllev.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { ctx.getSharedPreferences(Prefs.ALERTS, Context.MODE_PRIVATE) }

    var theft by remember { mutableStateOf(prefs.getBoolean(Prefs.Keys.THEFT, true)) }
    var water by remember { mutableStateOf(prefs.getBoolean(Prefs.Keys.WATER, true)) }
    var transport by remember { mutableStateOf(DeviceApi.getTransport(ctx)) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Uyarılar & Bildirimler") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bağlantı Yöntemi – temaya uygun renk + ikonlar
            ElevatedCard(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bağlantı yöntemi", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TransportChip(
                            selected = transport == DeviceApi.Transport.BLUETOOTH,
                            label = "Bluetooth",
                            leadingIcon = { Icon(Icons.Filled.Bluetooth, contentDescription = null) },
                            onClick = {
                                transport = DeviceApi.Transport.BLUETOOTH
                                DeviceApi.setTransport(ctx, transport)
                            }
                        )
                        TransportChip(
                            selected = transport == DeviceApi.Transport.TCP,
                            label = "TCP",
                            leadingIcon = { Icon(Icons.Filled.SettingsEthernet, contentDescription = null) },
                            onClick = {
                                transport = DeviceApi.Transport.TCP
                                DeviceApi.setTransport(ctx, transport)
                            }
                        )
                    }
                }
            }

            // Alarm & Bildirim Ayarları
            ElevatedCard(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.s),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    ListItem(
                        headlineContent = { Text("Hırsızlık algılanırsa bildir") },
                        trailingContent = {
                            Switch(
                                checked = theft,
                                onCheckedChange = {
                                    theft = it
                                    prefs.edit().putBoolean(Prefs.Keys.THEFT, it).apply()
                                }
                            )
                        }
                    )
                    Divider()
                    ListItem(
                        headlineContent = { Text("Su kaçağı algılanırsa bildir") },
                        trailingContent = {
                            Switch(
                                checked = water,
                                onCheckedChange = {
                                    water = it
                                    prefs.edit().putBoolean(Prefs.Keys.WATER, it).apply()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
