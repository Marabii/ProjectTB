package com.studitradev2.models

data class NoteDTO(
    val id: Long,
    val owner: UserDTO,
    val title: String,
    val description: String,
    val price: Double,
    val files: List<NoteFileDTO>,
    val demoFile: NoteFileDTO?,
    val authorizedUsers: List<UserDTO>,
    val isDigital: Boolean,
    val isAvailable: Boolean
)
