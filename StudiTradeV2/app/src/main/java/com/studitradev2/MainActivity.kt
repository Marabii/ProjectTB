package com.studitradev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.api.RetrofitInstance
import com.studitradev2.models.NoteDTO
import com.studitradev2.ui.theme.StudiTradeV2Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudiTradeV2Theme {
                NotesScreen()
            }
        }
    }
}

@Composable
fun NotesScreen() {
    val context = LocalContext.current // Récupère le contexte pour les composants TopBar et BottomBar
    var notes by remember { mutableStateOf<List<NoteDTO>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    // Charger les notes au démarrage
    LaunchedEffect(Unit) {
        RetrofitInstance.notesApi.getAllNotes().enqueue(object : Callback<List<NoteDTO>> {
            override fun onResponse(
                call: Call<List<NoteDTO>>,
                response: Response<List<NoteDTO>>
            ) {
                if (response.isSuccessful) {
                    notes = response.body() ?: emptyList()
                } else {
                    errorMessage = "Failed to load notes: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<NoteDTO>>, t: Throwable) {
                errorMessage = "Error: ${t.localizedMessage}"
            }
        })
    }

    Scaffold(
        topBar = { TopBar(context = context) }, // Utilisation de votre TopBar personnalisée
        bottomBar = { BottomBar(context = context) } // Utilisation de votre BottomBar personnalisée
    ) { innerPadding ->
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(notes) { note ->
                    NoteCard(note)
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteDTO) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Action sur le clic du document */ },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Author: ${note.owner.email}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: ${note.price}€",
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (note.isDigital) "Digital document" else "Non digital document",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Gérer le bouton Favoris */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = "Favorite", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Favorite", color = Color.White)
            }
        }
    }
}
