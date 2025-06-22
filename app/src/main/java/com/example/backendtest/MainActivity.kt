package com.example.backendtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.backendtest.ui.theme.BackendTestTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.StepSensorManager
import com.example.backendtest.data.network.UserSession


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var stepSensorManager: StepSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja managerów
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        stepSensorManager = StepSensorManager(applicationContext)

        // Inicjalizacja UserSession
        UserSession.init(applicationContext)

        setContent {
            BackendTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent(
                        sharedPreferencesManager = sharedPreferencesManager,
                        stepSensorManager = stepSensorManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.stopCounting()
    }
}

@Composable
fun MainAppContent(
    sharedPreferencesManager: SharedPreferencesManager,
    stepSensorManager: StepSensorManager
) {
    val navController = rememberNavController()

    // Sprawdzanie, czy użytkownik jest zalogowany
    val isUserLoggedIn = remember {
        mutableStateOf(UserSession.token != null)
    }

    // Ustawienie początkowego ekranu na podstawie stanu logowania
    LaunchedEffect(isUserLoggedIn.value) {
        if (!isUserLoggedIn.value) {
            navController.navigate(Route.LoginScreen().name) {
                popUpTo(0)
            }
        }
    }

    MyNavigation(
        navHostController = navController,
        sharedPreferencesManager = sharedPreferencesManager,
        stepSensorManager = stepSensorManager
    )
}

@Preview(showBackground = true)
@Composable
fun MainPerview(){
    BackendTestTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

        }
    }
}
