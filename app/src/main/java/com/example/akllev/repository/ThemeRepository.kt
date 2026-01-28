package com.example.akllev.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.akllev.util.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepository(private val ctx: Context) {
    private val KEY_DARK = booleanPreferencesKey("dark_theme")
    val darkFlow: Flow<Boolean> = ctx.appDataStore.data.map { it[KEY_DARK] ?: false }
    suspend fun setDark(v: Boolean) { ctx.appDataStore.edit { it[KEY_DARK] = v } }
}
