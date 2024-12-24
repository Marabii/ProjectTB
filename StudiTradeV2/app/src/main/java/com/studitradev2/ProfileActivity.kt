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
    // Charger les données sauvegardées au démarrage
    var name by remember { mutableStateOf(sharedPreferences.getString("name", "Lucas Chateau") ?: "") }
    var universityIndex by remember {
        mutableIntStateOf(
            sharedPreferences.getInt("universityIndex", 0) // Charge l'index sauvegardé (par défaut 0)
        )
    }
    var studyProgramIndex by remember {
        mutableIntStateOf(
            sharedPreferences.getInt("studyProgramIndex", 0) // Charge l'index sauvegardé (par défaut 0)
        )
    }
    var yearOfStudyIndex by remember {
        mutableIntStateOf(
            sharedPreferences.getInt("yearOfStudyIndex", 0) // Charge l'index sauvegardé (par défaut 0)
        )
    }

    // Listes des options
    val universities = listOf("EMSE", "HEC Paris", "INSEAD")
    val studyPrograms = listOf("Business Administration", "Engineering", "Computer Science")
    val yearsOfStudy = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

    // Snackbar pour afficher un message de confirmation
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
                    options = universities,
                    selectedIndex = universityIndex,
                    onItemSelected = { universityIndex = it }
                )

                // Dropdown pour Domaine d'étude
                CustomDropdownMenu(
                    label = "Study Program",
                    options = studyPrograms,
                    selectedIndex = studyProgramIndex,
                    onItemSelected = { studyProgramIndex = it }
                )

                // Dropdown pour Année d'étude
                CustomDropdownMenu(
                    label = "Year of Study",
                    options = yearsOfStudy,
                    selectedIndex = yearOfStudyIndex,
                    onItemSelected = { yearOfStudyIndex = it }
                )
            }

            FloatingActionButton(
                onClick = {
                    // Sauvegarder les données dans les SharedPreferences
                    sharedPreferences.edit {
                        putString("name", name)
                        putInt("universityIndex", universityIndex)
                        putInt("studyProgramIndex", studyProgramIndex)
                        putInt("yearOfStudyIndex", yearOfStudyIndex)
                    }
                    // Afficher une confirmation
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
    options: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropDownExpanded = true }
            ) {
                Text(
                    text = options[selectedIndex],
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon"
                )
            }

            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            isDropDownExpanded = false
                            onItemSelected(index)
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