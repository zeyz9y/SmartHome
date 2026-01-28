package com.example.akllev.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.akllev.model.HeatingSchedule
import org.json.JSONArray

class ScheduleStore(context: Context) {
    private val prefs = context.getSharedPreferences("schedules", MODE_PRIVATE)
    private val KEY = "heating_schedules"

    fun getAll(): List<HeatingSchedule> {
        val raw = prefs.getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(raw)
        return buildList {
            for (i in 0 until arr.length()) {
                add(HeatingSchedule.fromJson(arr.getJSONObject(i)))
            }
        }
    }

    fun saveAll(list: List<HeatingSchedule>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    fun upsert(item: HeatingSchedule) {
        val list = getAll().toMutableList()
        val idx = list.indexOfFirst { it.id == item.id }
        if (idx >= 0) list[idx] = item else list.add(item)
        saveAll(list)
    }

    fun delete(id: String) = saveAll(getAll().filterNot { it.id == id })
}
