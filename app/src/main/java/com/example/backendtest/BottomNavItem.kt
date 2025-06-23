package com.example.backendtest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Start")
    object Steps : BottomNavItem("steps", Icons.Filled.Place, "Kroki")
    object Exercises : BottomNavItem("exercises", Icons.Filled.AccountBox, "Ćwiczenia")
    object Calendar : BottomNavItem("calendar", Icons.Filled.DateRange, "Kalendarz")
}
