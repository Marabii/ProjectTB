package com.projetTB.projetTB.Notes.DTOs;

import com.projetTB.projetTB.Auth.DTO.UserDTO;
import com.projetTB.projetTB.Notes.models.Note;

import java.util.List;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDTO {
    private Long id;
    private UserDTO owner;
    private String title;
    private String description;
    private Double price;
    private List<NoteFileDTO> files;
    private NoteFileDTO demoFile;
    private List<UserDTO> authorizedUsers;
    private boolean isDigital;
    private boolean isAvailable;

    public static NoteDTO parseNoteToNoteDTO(Note noteInput) {
        NoteDTO result = NoteDTO.builder().id(noteInput.getId()).owner(UserDTO.parseUserToUserDTO(noteInput.getOwner()))
                .title(noteInput.getTitle()).description(noteInput.getDescription()).price(noteInput.getPrice())
                .files(noteInput.getFiles().stream().map(file -> NoteFileDTO.parseNoteFileToNoteFileDTO(file)).toList())
                .demoFile(NoteFileDTO.parseNoteFileToNoteFileDTO(noteInput.getDemoFile()))
                .isAvailable(noteInput.isAvailable()).isDigital(noteInput.isDigital())
                .authorizedUsers(
                        noteInput.getAuthorizedUsers().stream().map(user -> UserDTO.parseUserToUserDTO(user)).toList())
                .build();

        return result;
    }
}
