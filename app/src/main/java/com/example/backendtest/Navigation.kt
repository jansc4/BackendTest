package com.example.backendtest

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.backendtest.data.network.RetrofitInstance
import com.example.backendtest.data.network.UserSession
import com.example.backendtest.ui.login.LoginViewModel
import com.example.backendtest.ui.login.LoginViewModelFactory
import com.example.backendtest.ui.login.LoginScreen
import com.example.backendtest.ui.main.MainScaffoldScreen
import com.example.backendtest.ui.main.MainViewModel
import com.example.backendtest.ui.main.MainViewModelFactory
import com.example.backendtest.ui.signup.SignUpScreen
import com.example.backendtest.ui.signup.SignUpViewModel
import com.example.backendtest.ui.signup.SignUpViewModelFactory


sealed class Route{
    data class LoginScreen(val name:String = "Login"):Route()
    data class SignUpScreen(val name:String = "SignUp"):Route()
    data class MainScreen(val name:String = "Main"):Route()
}


@Composable
fun MyNavigation(
    navHostController: NavHostController,
    sharedPreferencesManager: SharedPreferencesManager,
    stepSensorManager: StepSensorManager
) {
    NavHost(
        navController = navHostController,
        startDestination = "login_flow",
    ) {
        navigation(
            startDestination = Route.LoginScreen().name,
            route = "login_flow"
        ) {
            composable(route = Route.LoginScreen().name) {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(
                        RetrofitInstance.api,
                        sharedPreferencesManager
                    )
                )

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navHostController.navigate(Route.MainScreen().name) {
                            popUpTo("login_flow") { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navHostController.navigate(Route.SignUpScreen().name)
                    }
                )
            }

            composable(route = Route.SignUpScreen().name) {
                val signUpViewModel: SignUpViewModel = viewModel(
                    factory = SignUpViewModelFactory(
                        RetrofitInstance.api,
                        sharedPreferencesManager
                    )
                )

                SignUpScreen(
                    viewModel = signUpViewModel,
                    onSignUpSuccess = {
                        navHostController.navigate(Route.MainScreen().name) {
                            popUpTo("login_flow") { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navHostController.navigateUp()
                    }
                )
            }
        }

        composable(route = Route.MainScreen().name) {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    RetrofitInstance.api,
                    sharedPreferencesManager,
                    stepSensorManager
                )
            )

            MainScaffoldScreen(
                navController = navHostController,
                onLogout = {
                    UserSession.clearAuthToken()
                    UserSession.clearAuthRefreshToken()
                    navHostController.navigate(Route.LoginScreen().name) {
                        popUpTo(0)
                    }
                },
                viewModel = mainViewModel
            )
        }
    }
}