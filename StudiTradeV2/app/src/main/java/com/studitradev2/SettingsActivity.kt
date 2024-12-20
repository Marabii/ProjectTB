package com.studitradev2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.ui.theme.StudiTradeV2Theme
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser les SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setContent {
            StudiTradeV2Theme {
                SettingsScreen(
                    onBackClick = { finish() },
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit, sharedPreferences: SharedPreferences) {
    // Charger les données sauvegardées
    var email by remember { mutableStateOf(sharedPreferences.getString("email", "lucas@emse.fr") ?: "") }
    var password by remember { mutableStateOf(sharedPreferences.getString("password", "1234") ?: "") }
    var emailNotificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("emailNotifications", false)) }
    var pushNotificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("pushNotifications", false)) }
    var publicProfileEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("publicProfile", false)) }

    val snackbarHostState = remember { SnackbarHostState() } // Gérer le Snackbar
    val scope = rememberCoroutineScope() // Pour lancer les coroutines

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings", color = Color.White, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // SnackbarHost
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Account
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.align(Alignment.Start)
                )
                CustomTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )
                CustomTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it }
                )

                // Section Notifications
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.align(Alignment.Start)
                )
                SettingsSwitchItem(
                    label = "Email Notifications",
                    checked = emailNotificationsEnabled,
                    onCheckedChange = { emailNotificationsEnabled = it }
                )
                SettingsSwitchItem(
                    label = "Push Notifications",
                    checked = pushNotificationsEnabled,
                    onCheckedChange = { pushNotificationsEnabled = it }
                )

                // Section Privacy
                Text(
                    text = "Privacy",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.align(Alignment.Start)
                )
                SettingsSwitchItem(
                    label = "Public Profile",
                    checked = publicProfileEnabled,
                    onCheckedChange = { publicProfileEnabled = it }
                )
            }

            // Bouton "Save Modifications"
            Button(
                onClick = {
                    // Sauvegarder les données dans SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("emailNotifications", emailNotificationsEnabled)
                        putBoolean("pushNotifications", pushNotificationsEnabled)
                        putBoolean("publicProfile", publicProfileEnabled)
                        apply()
                    }

                    // Afficher un Snackbar pour confirmer la sauvegarde
                    scope.launch {
                        snackbarHostState.showSnackbar("Changes saved successfully")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Save Modifications", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default
    )
}

@Composable
fun SettingsSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Black)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                uncheckedThumbColor = Color.Gray
            )
        )
    }
}
