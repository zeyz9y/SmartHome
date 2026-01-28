package com.example.akllev.ui.components.controls

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
    forceCompact: Boolean = false // uzun label'lar için
) {
    // Heuristik: Uzun label'larda otomatik kompakt moda geç
    val compact = forceCompact || label.length >= 10

    val padH = if (compact) 8.dp else 12.dp
    val iconSize = if (compact) 18.dp else 20.dp
    val gap = if (compact) 6.dp else 8.dp
    val textStyle = if (compact)
        MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
    else
        MaterialTheme.typography.labelLarge

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = padH),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.height(iconSize))
        Spacer(Modifier.width(gap))
        Text(
            text = label,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip // istersen Ellipsis
        )
    }
}
