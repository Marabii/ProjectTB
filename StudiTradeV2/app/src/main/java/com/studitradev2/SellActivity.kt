package com.studitradev2

import android.content.ContentResolver
import android.content.Context
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studitradev2.ui.theme.StudiTradeV2Theme
import java.io.File

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

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            fileUri = uri
            fileName = uri.path?.split("/")?.last() ?: "Unnamed file"
            onFileSelected(uri)
        } else {
            fileName = "No file selected"
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sell Your Files",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            // File selector
            Text(text = "Upload File", style = MaterialTheme.typography.labelLarge)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { launcher.launch(arrayOf("*/*")) }
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    placeholder = { Text("Click to upload a file") },
                )
            }

            // Title field
            Text(text = "Title", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Enter a title for your file") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description field
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

            // Price field
            Text(text = "Price (€)", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("Enter price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Upload button
            Button(
                onClick = {
                    if (fileUri != null) {
                        Toast.makeText(
                            context,
                            "File uploaded successfully: $fileName",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Please select a file first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

fun getUri(uri: Uri, context: Context): Uri {
    return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr = context.contentResolver
        val file = File.createTempFile("tempFile", null, context.cacheDir)
        cr.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(file)
    } else {
        uri
    }
}
