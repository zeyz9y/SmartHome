package com.example.akllev.model

import org.json.JSONObject
import java.util.UUID

data class HeatingSchedule(
    val id: String = UUID.randomUUID().toString(),
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val daysOfWeek: Set<Int>, // Calendar.SUNDAY..Calendar.SATURDAY
    val targetTemp: Float? = null,
    val isEnabled: Boolean = true
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("startHour", startHour)
        put("startMinute", startMinute)
        put("endHour", endHour)
        put("endMinute", endMinute)
        put("daysOfWeek", daysOfWeek.joinToString(","))
        put("targetTemp", targetTemp)
        put("isEnabled", isEnabled)
    }

    companion object {
        fun fromJson(obj: JSONObject) = HeatingSchedule(
            id = obj.getString("id"),
            startHour = obj.getInt("startHour"),
            startMinute = obj.getInt("startMinute"),
            endHour = obj.getInt("endHour"),
            endMinute = obj.getInt("endMinute"),
            daysOfWeek = obj.getString("daysOfWeek")
                .split(",").filter { it.isNotBlank() }.map { it.toInt() }.toSet(),
            targetTemp = if (obj.isNull("targetTemp")) null else obj.getDouble("targetTemp").toFloat(),
            isEnabled = obj.getBoolean("isEnabled")
        )
    }
}