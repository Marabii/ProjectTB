package com.automacorp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel: ViewModel() {
    var room by mutableStateOf <RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())
    // Function to set or update the room
    fun updateRoom(newRoom: RoomDto) {
        this.room = newRoom
    }

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) { // (1)
            runCatching { ApiServices.roomsApiService.readAllRooms().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms) // (2)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString() ) // (3)
                }
        }
    }

    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.readOneRoomById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature ?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature,
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }
}