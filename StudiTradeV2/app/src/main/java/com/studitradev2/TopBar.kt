package com.studitradev2

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(context: Context) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    // Navigue vers ProfileActivity
                    val intent = Intent(context, ProfileActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_action_user),
                        contentDescription = stringResource(R.string.app_go_user_profile),
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
