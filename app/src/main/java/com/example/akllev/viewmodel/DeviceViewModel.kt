// DeviceViewModel.kt
package com.example.akllev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.akllev.model.Device
import com.example.akllev.model.DeviceType

class DeviceViewModel : ViewModel() {
    // İç listeyi mutableStateListOf ile oluşturup içine default cihazları ekliyoruz.
    private val _devices = mutableStateListOf<Device>().apply {
        add(Device(name = "Salon Lambası",   isOn = false, type = DeviceType.LAMP,room="Salon"))
        add(Device(name = "Salon Klima", isOn = true,  type = DeviceType.AC,room="Salon"))
        add(Device(name = "Güvenlik Kamerası",isOn = false, type = DeviceType.CAMERA,room= "Çocuk Odası"))
        add(Device(name = "Buzdolabı",       isOn = true,  type = DeviceType.FRIDGE,room="Mutfak"))
    }
    // Dışarıya salt-okunur liste olarak sunuyoruz
    val devices: List<Device> get() = _devices

    // Cihaz ekleme fonksiyonu (AddDeviceScreen’den çağrılıyor)
    fun addDevice(device: Device) {
        _devices.add(device)
    }

    // Cihazın açık/kapalı durumunu toggle’layan fonksiyon
    fun toggleDevice(device: Device) {
        val idx = _devices.indexOf(device)
        if (idx >= 0) {
            _devices[idx] = device.copy(isOn = !device.isOn)
        }
    }
}
