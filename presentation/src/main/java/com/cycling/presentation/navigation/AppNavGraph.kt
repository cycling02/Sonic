package com.cycling.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cycling.presentation.scan.ScanScreen
import com.cycling.presentation.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onShowToast: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings)
                }
            )
        }
        
        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScan = { navController.navigate(Screen.Scan) }
            )
        }
        
        composable<Screen.Scan> {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onShowToast = onShowToast
            )
        }
    }
}

@Composable
private fun HomeScreen(
    onNavigateToSettings: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.material3.TextButton(
            onClick = onNavigateToSettings,
            modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.Center)
        ) {
            androidx.compose.material3.Text("前往设置")
        }
    }
}
