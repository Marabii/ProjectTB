package com.projetTB.projetTB.Notes.DTOs;

import lombok.Builder;

import com.projetTB.projetTB.Notes.models.NoteFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteFileDTO {
    private Long id;
    private String fileUrl;
    private String fileName;

    public static NoteFileDTO parseNoteFileToNoteFileDTO(NoteFile noteFileInput) {
        NoteFileDTO result = NoteFileDTO.builder().id(noteFileInput.getId()).fileUrl(noteFileInput.getFileUrl())
                .fileName(noteFileInput.getFileName()).build();

        return result;
    }
}