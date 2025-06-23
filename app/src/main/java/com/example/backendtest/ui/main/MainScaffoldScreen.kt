package com.example.backendtest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.backendtest.BottomNavItem
import com.example.backendtest.ui.calendar.CalendarScreen
import com.example.backendtest.ui.exercise.ExerciseScreen

@Composable
fun MainScaffoldScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    viewModel: MainViewModel
) {
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Steps,
        BottomNavItem.Exercises,
        BottomNavItem.Calendar
    )
    val navHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navHostController.navigate(item.route) {
                                popUpTo(navHostController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                MainScreen(viewModel = viewModel, onLogout = onLogout)
            }
            composable(BottomNavItem.Steps.route) {
               Text("StepsScreen") //StepsScreen()
            }
            composable(BottomNavItem.Exercises.route) {
                ExerciseScreen(onExerciseClick = {})
            }
            composable(BottomNavItem.Calendar.route) {
                CalendarScreen()
            }
        }
    }
}
