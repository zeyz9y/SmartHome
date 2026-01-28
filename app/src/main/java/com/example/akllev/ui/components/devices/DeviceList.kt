package com.example.akllev.ui.components.devices

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akllev.model.Device

@Composable
fun DeviceList(
    devices: List<Device>,
    onToggleDevice: (Device, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        devices.forEach { device ->
            DeviceItem(
                deviceName = device.name,
                isOn = device.isOn,
                // Seçili durumu yok, hep false
                isSelected = false,
                // Tıklama yok, boş lambda
                onClick = { },
                onToggle = { newState -> onToggleDevice(device, newState) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

