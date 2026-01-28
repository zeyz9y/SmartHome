package com.example.akllev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akllev.bt.SpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BluetoothSensorViewModel(private val sp: SpRepository) : ViewModel() {

    // UI’nın dinleyeceği akışlar
    val data = sp.data
    val connected = sp.connected

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()   // <<< EKLENDİ

    fun connect(nameOrMac: String) {
        if (_busy.value) return
        viewModelScope.launch {
            _busy.value = true
            try {
                sp.connect(nameOrMac)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Bağlantı hatası"
            } finally {
                _busy.value = false
            }
        }
    }

    fun disconnect() {
        sp.disconnect()
    }

    fun setLed(on: Boolean) {
        viewModelScope.launch {
            runCatching { sp.setLed(on) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sp.disconnect()
    }
}
