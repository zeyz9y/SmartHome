// com/example/akllev/model/DeviceType.kt
package com.example.akllev.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class DeviceType(val label: String, val icon: ImageVector) {
    LAMP("Lamba", Icons.Default.Lightbulb),
    TV("TV",Icons.Default.Tv),
    NIGHT_LAMP("Gece Lambası",Icons.Default.LightbulbCircle),
    TOY_BOX("Oyuncak Kutusu",Icons.Default.Toys),
    AC("Klima", Icons.Default.AcUnit),
    OVEN("Fırın", Icons.Default.Kitchen),
    FRIDGE("Buzdolabı", Icons.Default.Kitchen),
    CAMERA("Kamera", Icons.Default.Videocam),
    OTHER("Diğer", Icons.Default.DevicesOther)

    // vs. eklemek istediğin diğer tipler…
}
