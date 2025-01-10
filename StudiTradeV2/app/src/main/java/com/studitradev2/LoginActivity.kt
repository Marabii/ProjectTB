package com.studitradev2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Colors
import com.studitradev2.api.RetrofitInstance
import com.studitradev2.models.AuthenticationRequest
import com.studitradev2.models.AuthenticationResponse
import com.studitradev2.models.RegisterRequest
import com.studitradev2.ui.theme.StudiTradeV2Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setContent {
            StudiTradeV2Theme {
                LoginRegisterScreen(
                    onLoginSuccess = { token ->
                        // Sauvegarder le token JWT et naviguer vers MainActivity
                        sharedPreferences.edit().putString("jwtToken", token).apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginRegisterScreen(onLoginSuccess: (String) -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) } // Basculer entre Login et Register

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) "Login to StudiTrade" else "Register to StudiTrade",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoginMode) {
            LoginScreenContent(
                onLoginSuccess = onLoginSuccess,
                onToggleMode = { isLoginMode = false }
            )
        } else {
            RegisterScreenContent(
                onRegisterSuccess = { isLoginMode = true },
                onToggleMode = { isLoginMode = true }
            )
        }
    }
}

@Composable
fun LoginScreenContent(onLoginSuccess: (String) -> Unit, onToggleMode: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CustomTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            visualTransformation = VisualTransformation.None
        )
        CustomTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                isLoading = true
                val request = AuthenticationRequest(email = email, password = password)
                RetrofitInstance.authApi.login(request).enqueue(object : Callback<AuthenticationResponse> {
                    override fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        isLoading = false
                        if (response.isSuccessful) {
                            val token = response.body()?.token ?: ""
                            onLoginSuccess(token) // Naviguer vers MainActivity
                        } else {
                            errorMessage = "Invalid email or password"
                        }
                    }

                    override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error: ${t.localizedMessage}"
                    }
                })
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)


        ) {
            Text(text = if (isLoading) "Logging in..." else "Login")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextButton(onClick = onToggleMode) {
            Text(text = "Don't have an account? Register here.", color = Color.Black)
        }
    }
}

@Composable
fun RegisterScreenContent(onRegisterSuccess: () -> Unit, onToggleMode: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CustomTextField(label = "Name", value = name, onValueChange = { name = it }, visualTransformation = VisualTransformation.None)
        CustomTextField(label = "Email", value = email, onValueChange = { email = it }, visualTransformation = VisualTransformation.None)
        CustomTextField(label = "Password", value = password, onValueChange = { password = it }, visualTransformation = PasswordVisualTransformation())
        CustomTextField(label = "Phone Number", value = phoneNumber, onValueChange = { phoneNumber = it }, visualTransformation = VisualTransformation.None)

        Button(
            onClick = {
                isLoading = true
                val request = RegisterRequest(name, email, password, phoneNumber)
                RetrofitInstance.authApi.register(request).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            onRegisterSuccess() // Retourne au mode Login
                        } else {
                            errorMessage = "Registration failed: ${response.errorBody()?.string() ?: "Unknown error"}"
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Error: ${t.localizedMessage}"
                    }
                })
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = if (isLoading) "Registering..." else "Register")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextButton(onClick = onToggleMode) {
            Text(text = "Already have an account? Login here.", color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, visualTransformation: VisualTransformation) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray
        )
    )
}
