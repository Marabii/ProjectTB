package com.studitradev2

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.ui.theme.StudiTradeV2Theme

class SellActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudiTradeV2Theme {
                SellFilesScreen(
                    onBackClick = { finish() },
                    onFileSelected = { uri ->
                        Toast.makeText(this, "File selected: $uri", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellFilesScreen(
    onBackClick: () -> Unit,
    onFileSelected: (Uri?) -> Unit
) {
    var fileName by remember { mutableStateOf("No file selected") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Gestionnaire de sélection de fichiers
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fileUri = uri
            fileName = uri.lastPathSegment ?: "Unnamed file"
            onFileSelected(uri) // Passe l'URI sélectionné à l'activité
        } else {
            fileName = "No file selected"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sell Your Files",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Champ pour uploader un fichier
            Text(text = "Upload File", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = fileName,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        launcher.launch("*/*") // Ouvre le sélecteur pour tous types de fichiers
                    },
                readOnly = true,
                placeholder = { Text("Upload a file") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Upload Icon"
                    )
                }
            )

            // Champ pour le titre
            Text(text = "Title", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Enter a title for your file") },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ pour la description
            Text(text = "Description", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe your file") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            // Champ pour le prix
            Text(text = "Price (€)", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("Enter price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f)) // Espace pour pousser le bouton en bas

            // Bouton "Upload for Sale"
            Button(
                onClick = {
                    // TODO: Ajouter une logique pour envoyer les données sur un serveur
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Upload for Sale", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
