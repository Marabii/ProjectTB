package com.studitradev2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.api.RetrofitInstance
import com.studitradev2.models.UserDTO
import com.studitradev2.ui.theme.StudiTradeV2Theme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser les SharedPreferences
        sharedPreferences = getSharedPreferences("UserProfilePrefs", Context.MODE_PRIVATE)

        setContent {
            StudiTradeV2Theme {
                ProfileScreen(
                    onBackClick = { finish() },
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit, sharedPreferences: SharedPreferences) {
    // Variables d'état pour les données utilisateur
    var user by remember { mutableStateOf<UserDTO?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Charger les informations utilisateur au démarrage
    LaunchedEffect(Unit) {
        fetchUserInfo { fetchedUser, error ->
            if (fetchedUser != null) {
                user = fetchedUser
                errorMessage = ""
            } else {
                errorMessage = error ?: "Failed to load user information"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile", color = Color.White, fontSize = 20.sp) },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (user != null) {
                // Afficher les informations utilisateur
                Text(text = "Name: ${user!!.username}", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Email: ${user!!.email}", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Phone: ${user!!.phoneNumber}", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                // Vérifier l'utilisateur
                Button(
                    onClick = {
                        verifyUser { isVerified, error ->
                            if (isVerified) {
                                scope.launch {
                                    SnackbarHostState().showSnackbar("User verified successfully")
                                }
                            } else {
                                errorMessage = error ?: "Failed to verify user"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Verify User", color = Color.White)
                }
            } else if (errorMessage.isNotEmpty()) {
                // Afficher le message d'erreur si les données échouent à se charger
                Text(text = errorMessage, color = Color.Red, fontSize = 16.sp)
            } else {
                // Afficher un indicateur de chargement
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

fun fetchUserInfo(callback: (UserDTO?, String?) -> Unit) {
    val call = RetrofitInstance.userApi.getUserInfo()
    call.enqueue(object : Callback<UserDTO> {
        override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
            if (response.isSuccessful) {
                callback(response.body(), null)
            } else {
                callback(null, "Failed to load user info: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<UserDTO>, t: Throwable) {
            callback(null, "Error: ${t.localizedMessage}")
        }
    })
}

fun verifyUser(callback: (Boolean, String?) -> Unit) {
    val call = RetrofitInstance.userApi.verifyUser()
    call.enqueue(object : Callback<Map<String, Boolean>> {
        override fun onResponse(
            call: Call<Map<String, Boolean>>,
            response: Response<Map<String, Boolean>>
        ) {
            if (response.isSuccessful && response.body()?.get("success") == true) {
                callback(true, null)
            } else {
                callback(false, "Verification failed: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
            callback(false, "Error: ${t.localizedMessage}")
        }
    })
}
