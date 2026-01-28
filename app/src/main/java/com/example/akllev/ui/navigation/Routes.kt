package com.example.akllev.ui.navigation

sealed class Route(val route: String) {
    data object Dashboard : Route("dashboard")
    data object Devices   : Route("devices")
    data object Alerts    : Route("alerts")
    data object Schedules : Route("schedules")
    data object AddDevice : Route("addDevice")
}
