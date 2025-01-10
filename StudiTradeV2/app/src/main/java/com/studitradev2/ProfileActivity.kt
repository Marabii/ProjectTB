package com.studitradev2

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.api.RetrofitInstance
import com.studitradev2.models.UserDTO
import com.studitradev2.ui.theme.StudiTradeV2Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudiTradeV2Theme {
                ProfileScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var user by remember { mutableStateOf<UserDTO?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Charger les informations utilisateur
    LaunchedEffect(Unit) {
        fetchUserInfo(
            context = context,
            onSuccess = { fetchedUser ->
                user = fetchedUser
                errorMessage = ""
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold, // Titre en gras
                        fontSize = 20.sp
                    )
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage.isNotEmpty() -> {
                    Text(text = errorMessage, color = Color.Red, fontSize = 16.sp)
                }
                user != null -> {
                    ProfileContent(user = user!!)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(user: UserDTO) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Username: ${user.username}", fontSize = 20.sp, color = Color.Black)
        Text(text = "Email: ${user.email}", fontSize = 18.sp, color = Color.Gray)
        Text(text = "Phone: ${user.phoneNumber}", fontSize = 18.sp, color = Color.Gray)

        // Vérification des détails de vérification
        user.verificationDetails?.let { details ->
            Text(
                text = if (details.isEmailVerified) "Email Verified" else "Email Not Verified",
                fontSize = 16.sp,
                color = if (details.isEmailVerified) Color.Green else Color.Red
            )
            Text(
                text = if (details.isPhoneNumberVerified) "Phone Verified" else "Phone Not Verified",
                fontSize = 16.sp,
                color = if (details.isPhoneNumberVerified) Color.Green else Color.Red
            )
        }
    }
}

fun fetchUserInfo(context: Context, onSuccess: (UserDTO) -> Unit, onError: (String) -> Unit) {
    val userApi = RetrofitInstance.getUserApi(context)
    val call = userApi.getUserInfo()

    call.enqueue(object : Callback<UserDTO> {
        override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
            if (response.isSuccessful) {
                response.body()?.let(onSuccess) ?: onError("No user data found")
            } else if (response.code() == 401) {
                onError("Session expired. Please log in again.")
                // Redirection si nécessaire
                context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                onError("Failed to load user info: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<UserDTO>, t: Throwable) {
            onError("Error: ${t.localizedMessage}")
        }
    })
}
