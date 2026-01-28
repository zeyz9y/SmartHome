package com.example.akllev.ui.components.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoomSelector(
    selectedRoom: String,
    rooms: List<String>,
    onRoomSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Listeyi iki’şerli gruplara böl, her grup için bir Row
        rooms.chunked(2).forEach { rowRooms ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowRooms.forEach { room ->
                    RoomButton(
                        label = room,
                        isSelected = (room == selectedRoom),  // 🎯 seçili kontrolü
                        onClick = { onRoomSelected(room) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Eğer tek oda kaldıysa boşluğu koru
                if (rowRooms.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
