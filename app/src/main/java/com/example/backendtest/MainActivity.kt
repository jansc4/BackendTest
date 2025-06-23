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
import com.example.backendtest.data.network.RetrofitInstance
import com.example.backendtest.data.network.RetrofitInstance.api
import com.example.backendtest.data.network.UserSession


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var stepSensorManager: StepSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja managerów
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        stepSensorManager = StepSensorManager(applicationContext)

        stepSensorManager.startTracking()


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
        stepSensorManager.stopTracking()

    }
}

@Composable
fun MainAppContent(
    sharedPreferencesManager: SharedPreferencesManager,
    stepSensorManager: StepSensorManager
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val api = RetrofitInstance.api

    val isUserLoggedIn = remember {
        mutableStateOf(false)
    }

    // Funkcja do automatycznego logowania
    suspend fun tryAutoLogin(): Boolean {
        return try {
            if (sharedPreferencesManager.isRememberMeEnabled() && sharedPreferencesManager.hasStoredCredentials()) {
                val userPreferences = sharedPreferencesManager.getUserPreferences()
                val response = api.login(
                    username = userPreferences.email,
                    password = userPreferences.password
                )
                UserSession.saveAuthToken(response.access_token, response.refresh_token)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            sharedPreferencesManager.clearUserPreferences()
            false
        }
    }

    // Funkcja do weryfikacji i odświeżania tokena
    suspend fun verifyAndRefreshTokenIfNeeded(): Boolean {
        return try {
            // Próba wykonania przykładowego zapytania do API aby zweryfikować token
            api.getDailySteps()
            true
        } catch (e: Exception) {
            // Token wygasł lub jest nieprawidłowy
            try {
                // Próba użycia refresh tokena
                UserSession.refresh_token?.let { refreshToken ->
                    val response = api.refreshToken(refreshToken)
                    UserSession.saveAuthToken(response.access_token, response.refresh_token)
                    return true
                }
                // Jeśli nie ma refresh tokena lub refresh się nie powiódł, próbujemy automatycznego logowania
                if (tryAutoLogin()) {
                    return true
                }
                false
            } catch (e: Exception) {
                // Błąd odświeżania tokena lub automatycznego logowania
                UserSession.clearSession()
                sharedPreferencesManager.clearUserPreferences()
                false
            }
        }
    }

    // Sprawdzanie stanu logowania przy starcie
    LaunchedEffect(Unit) {
        isUserLoggedIn.value = if (UserSession.token != null) {
            verifyAndRefreshTokenIfNeeded()
        } else {
            tryAutoLogin()
        }
    }

    // Nawigacja na podstawie stanu logowania z zabezpieczeniem przed wielokrotną nawigacją
    LaunchedEffect(isUserLoggedIn.value) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val targetRoute = if (isUserLoggedIn.value) {
            Route.MainScreen().name
        } else {
            Route.LoginScreen().name
        }

        // Nawiguj tylko jeśli obecna trasa jest inna niż docelowa
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
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
