package com.example.akllev.ui.components.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akllev.ui.theme.selectedContainer
import com.example.akllev.ui.theme.selectedContent
import com.example.akllev.ui.theme.unselectedContainer
import com.example.akllev.ui.theme.unselectedContent


@Composable
fun DeviceItem(
    deviceName: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier= Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),   // seçince çalışacak
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.selectedContainer.copy(alpha =0.5f)
            else
                MaterialTheme.colorScheme.unselectedContainer,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.selectedContent
            else
                MaterialTheme.colorScheme.unselectedContent
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = deviceName,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isOn,
                onCheckedChange = onToggle
            )


        }
    }
}




