package com.studitradev2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.ui.theme.StudiTradeV2Theme

class LoginActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setContent {
            StudiTradeV2Theme {
                LoginScreen(
                    onLoginSuccess = {
                        // Navigue vers MainActivity si login réussi
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Ferme LoginActivity
                    },
                    sharedPreferences = sharedPreferences // Passer SharedPreferences
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, sharedPreferences: SharedPreferences) {
    // Charger les identifiants sauvegardés
    val savedEmail = sharedPreferences.getString("email", "lucas@emse.fr") ?: ""
    val savedPassword = sharedPreferences.getString("password", "1234") ?: ""

    // États pour les champs de connexion
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(top = 64.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre avec deux niveaux
        Text(
            text = "Welcome to",
            style = TextStyle(
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "StudiTrade",
            style = TextStyle(
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Champ Email
        CustomTextField(
            label = "Write your email",
            value = email,
            onValueChange = { email = it },
            visualTransformation = VisualTransformation.None
        )

        // Champ Password
        CustomTextField(
            label = "Write your password",
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        // Bouton Login
        Button(
            onClick = {
                // Validation des identifiants
                if (email == savedEmail && password == savedPassword) {
                    errorMessage = ""
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid email or password"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Login",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // Message d’erreur
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, visualTransformation: VisualTransformation) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(50.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default
        )
    }
}
