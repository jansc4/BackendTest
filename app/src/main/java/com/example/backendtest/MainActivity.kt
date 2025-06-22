package com.example.backendtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.backendtest.ui.theme.BackendTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.backendtest.data.network.ApiClient
import com.example.backendtest.data.network.RegisterRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackendTestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BackendUI()
                }
            }
        }
    }
}

@Composable
fun BackendUI() {
    val scope = rememberCoroutineScope()
    var consoleOutput by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nazwa użytkownika") },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Button(
            onClick = {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = ApiClient.api.register(
                        RegisterRequest(username, email, password)
                    )
                    consoleOutput = "Zarejestrowano: ${response.username}, ${response.email}"
                } catch (e: Exception) {
                    consoleOutput = "Błąd rejestracji: ${e.message}"
                }
            }
        }) {
            Text("Zarejestruj")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = ApiClient.api.login(
                        mapOf(
                            "username" to email,
                            "password" to password
                        )
                    )
                    TokenStore.token = response.access_token
                    consoleOutput = "Zalogowano. Token: ${response.access_token}"
                } catch (e: Exception) {
                    consoleOutput = "Błąd logowania: ${e.message}"
                }
            }
        }) {
            Text("Zaloguj")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = ApiClient.api.getMe("Bearer ${TokenStore.token}")
                    consoleOutput = "Użytkownik: ${response.username}, ${response.email}"
                } catch (e: Exception) {
                    consoleOutput = "Błąd pobierania /me: ${e.message}"
                }
            }
        }) {
            Text("Pobierz dane użytkownika")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Konsola:", style = MaterialTheme.typography.titleMedium)
        Text(consoleOutput)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPerview(){
    BackendTestTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BackendUI()
        }
    }
}
