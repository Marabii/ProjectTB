package com.studitradev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.studitradev2.ui.theme.StudiTradeV2Theme

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudiTradeV2Theme {
                FavoritesScreen()
            }
        }
    }
}

@Composable
fun FavoritesScreen() {
    Text(text = "My favorites")
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    StudiTradeV2Theme {
        FavoritesScreen()
    }
}
