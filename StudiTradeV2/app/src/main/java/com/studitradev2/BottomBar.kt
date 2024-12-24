package com.studitradev2

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(
    context: Context,

) {
    NavigationBar(
        containerColor = Color.Black,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_home),
                    contentDescription = stringResource(R.string.app_go_home),
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = {
                // Naviguer vers MainActivity
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_sell),
                    contentDescription = stringResource(R.string.app_go_sell),
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = {
                // Naviguer vers SellActivity
                val intent = Intent(context, SellActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_purshased),
                    contentDescription = stringResource(R.string.app_go_purshased),
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = {
                // Naviguer vers PurchasedActivity
                val intent = Intent(context, PurchasedActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_favorites),
                    contentDescription = stringResource(R.string.app_go_favorites),
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = {
                // Naviguer vers FavoritesActivity
                val intent = Intent(context, FavoritesActivity::class.java)
                context.startActivity(intent)
            }
        )
    }
}
