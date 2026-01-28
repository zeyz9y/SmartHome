package com.example.akllev.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.akllev.model.DeviceType
import com.example.akllev.ui.screens.AddDeviceScreen
import com.example.akllev.ui.screens.AlertsScreen
import com.example.akllev.ui.screens.DashboardScreen
import com.example.akllev.ui.screens.DevicesScreen
import com.example.akllev.ui.screens.SchedulesScreen
import com.example.akllev.viewmodel.DeviceViewModel

@Composable
fun Navigation(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val deviceViewModel: DeviceViewModel = viewModel()

    NavHost(navController, startDestination = Route.Dashboard.route) {

        composable(Route.Dashboard.route) {
            DashboardScreen(
                onDevicesClick = { navController.navigate(Route.Devices.route) { launchSingleTop = true } },
                onAlertsClick = { navController.navigate(Route.Alerts.route) { launchSingleTop = true } },
                onSchedulesClick = { navController.navigate(Route.Schedules.route) { launchSingleTop = true } },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable(Route.Devices.route) {
            DevicesScreen(
                devices        = deviceViewModel.devices,
                onToggle       = { deviceViewModel.toggleDevice(it) },
                onAddClick     = { navController.navigate(Route.AddDevice.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.Alerts.route) {
            AlertsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.Schedules.route) {
            SchedulesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.AddDevice.route) {
            AddDeviceScreen(
                types          = DeviceType.entries,
                onAddDevice    = {
                    deviceViewModel.addDevice(it)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
