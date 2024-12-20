package com.studitradev2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.studitradev2.ui.theme.StudiTradeV2Theme
import kotlinx.coroutines.launch

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
    var name by remember { mutableStateOf(sharedPreferences.getString("name", "Lucas Chateau") ?: "") }
    var university by remember { mutableStateOf(sharedPreferences.getString("university", "EMSE") ?: "") }
    var studyProgram by remember { mutableStateOf(sharedPreferences.getString("studyProgram", "Business Administration") ?: "") }
    var yearOfStudy by remember { mutableStateOf(sharedPreferences.getString("yearOfStudy", "2nd Year") ?: "") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Profile", color = Color.White, fontSize = 20.sp)
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Champ pour le nom
                ProfileCustomTextField(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it }
                )

                // Dropdown pour Université
                CustomDropdownMenu(
                    label = "University",
                    selectedOption = university,
                    options = listOf("EMSE", "HEC Paris", "INSEAD")
                ) { selected ->
                    university = selected
                }

                // Dropdown pour Domaine d'étude
                CustomDropdownMenu(
                    label = "Study Program",
                    selectedOption = studyProgram,
                    options = listOf("Business Administration", "Engineering", "Computer Science")
                ) { selected ->
                    studyProgram = selected
                }

                // Dropdown pour Année d'étude
                CustomDropdownMenu(
                    label = "Year of Study",
                    selectedOption = yearOfStudy,
                    options = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
                ) { selected ->
                    yearOfStudy = selected
                }
            }

            FloatingActionButton(
                onClick = {
                    sharedPreferences.edit {
                        putString("name", name)
                        putString("university", university)
                        putString("studyProgram", studyProgram)
                        putString("yearOfStudy", yearOfStudy)
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar("Modifications saved successfully")
                    }
                },
                modifier = Modifier.align(Alignment.End),
                containerColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Save", tint = Color.White)
            }
        }
    }
}


@Composable
fun CustomDropdownMenu(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Gérer l'état du menu déroulant

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // Affiche le label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Conteneur pour le champ sélectionné et le menu
        Box {
            // Champ affichant l'option sélectionnée
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {}, // Pas de saisie manuelle
                readOnly = true, // Empêche la saisie
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }, // Ouvre le menu déroulant
                trailingIcon = { // Icône pour le menu déroulant
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )

            // Menu déroulant
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // Ferme le menu si on clique ailleurs
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option) // Met à jour l'option sélectionnée
                            expanded = false // Ferme le menu après sélection
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default
    )
}
