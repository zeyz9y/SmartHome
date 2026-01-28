package com.example.akllev.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.akllev.model.Device
import com.example.akllev.ui.components.devices.DeviceList
import com.example.akllev.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    devices: List<Device>,
    onToggle: (Device) -> Unit,
    onAddClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tüm Cihazlar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Filled.Add, contentDescription = "Cihaz Ekle") }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            // Başlık satırı + cihaz sayısı
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cihaz Listesi", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${devices.size} cihaz",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (devices.isEmpty()) {
                // Boş durum
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz cihaz eklenmemiş",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Tek seferde tüm liste
                Box(modifier = Modifier.fillMaxSize()) {
                    DeviceList(
                        devices = devices,
                        onToggleDevice = { d, _ -> onToggle(d) }
                    )
                }
            }
        }
    }
}
