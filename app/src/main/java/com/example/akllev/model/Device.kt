package com.example.akllev.model

data class Device(
    val name: String,
    var isOn: Boolean,
    val type: DeviceType,
    val room: String
)
