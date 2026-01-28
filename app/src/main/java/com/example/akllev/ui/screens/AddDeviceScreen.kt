package com.example.akllev.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.akllev.model.Device
import com.example.akllev.model.DeviceType
import com.example.akllev.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    types: List<DeviceType>,
    onAddDevice: (Device) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Ekran genişliğine göre grid kolon sayısı
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val columns = when {
        screenWidthDp < 600  -> 2
        screenWidthDp < 840  -> 3
        else                 -> 4
    }

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<DeviceType?>(null) }
    val isFormValid = name.isNotBlank() && selectedType != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cihaz Ekle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isFormValid,
                enter = slideInVertically { it },
                exit  = slideOutVertically { it }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.l)
                ) {
                    Button(
                        onClick = {
                            onAddDevice(
                                Device(
                                    name = name.trim(),
                                    isOn = false,
                                    type = requireNotNull(selectedType),
                                    room = ""
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Ekle") }
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Cihaz Adı") },
                supportingText = {
                    if (selectedType == null) Text("Devam etmek için tür seçin")
                },
                trailingIcon = {
                    if (name.isNotBlank()) {
                        IconButton(onClick = { name = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )

            Text("Cihaz Türü", style = MaterialTheme.typography.titleMedium)

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(MaterialTheme.spacing.s),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                verticalArrangement   = Arrangement.spacedBy(MaterialTheme.spacing.m)
            ) {
                items(types) { type ->
                    val isSelected = type == selectedType
                    Card(
                        onClick = { selectedType = type },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            contentColor = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(MaterialTheme.spacing.m),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(MaterialTheme.spacing.s))
                            Text(
                                type.label,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
