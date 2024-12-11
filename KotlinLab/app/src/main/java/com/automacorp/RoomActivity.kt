package com.automacorp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val onRoomClick: () -> Unit = {
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }

        val sendEmail: () -> Unit = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto://minehamza97@gmail.com"))
            startActivity(intent)
        }

        val openGithub: () -> Unit = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Marabii"))
            startActivity(intent)
        }

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)

        val model: RoomViewModel by viewModels()
        model.findByNameOrId(param ?: "")

        val onRoomSave: () -> Unit = {
            if (model.room != null) {
                val roomDto: RoomDto = model.room as RoomDto
                model.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack, onRoomClick, openGithub, sendEmail) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (model.room != null) {
                        RoomDetail(model, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }

                }
            }
        }
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    var localRoom by remember { mutableStateOf(model.room) }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            model.room?.name ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { newName ->
                localRoom = localRoom?.copy(name = newName)
                localRoom?.let { model.updateRoom(localRoom!!.id, it) }
            },
            placeholder = { Text(stringResource(R.string.act_room_name)) },
        )

        Text(
            text = "Current Temperature: ${model.room?.currentTemperature}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { newTemp ->
                localRoom = localRoom?.copy(targetTemperature = newTemp.toDouble())
                localRoom?.let { model.updateRoom(localRoom!!.id, it) }
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )
        Text(text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString())

    }

}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.act_room_none),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(bottom = 4.dp)
    )
}

