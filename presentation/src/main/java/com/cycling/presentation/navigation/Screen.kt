package com.cycling.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Settings : Screen
    
    @Serializable
    data object Scan : Screen
}
