package com.example.akllev.ui.components.rooms

// ← foundation paketinden
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoomButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)    // seçili mor
    else
        MaterialTheme.colorScheme.surface      // seçili değil beyaz/koyu gri

    val contentColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface

    Card(
        onClick = onClick,                    // ← yerleşik ripple burada
        modifier = modifier,
        colors   = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor   = contentColor
        ),
        shape    = MaterialTheme.shapes.small
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical   = 8.dp
            )
        )
    }
}

