package com.projetTB.projetTB.Notes.Controller;

import com.projetTB.projetTB.Notes.DTOs.NoteDTO;
import com.projetTB.projetTB.Notes.DTOs.NoteFileDTO;
import com.projetTB.projetTB.Notes.Service.NotesService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;

    @PostMapping("/protected/notes/upload")
    public ResponseEntity<NoteDTO> uploadNote(@RequestParam("documents") MultipartFile[] files,
            @RequestParam("demoFile") MultipartFile demoFile, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Double price,
            HttpServletRequest request) {
        try {
            String ownerEmail = request.getUserPrincipal().getName();
            NoteDTO note = notesService.uploadNoteFiles(files, demoFile, ownerEmail, title, description, price);
            return ResponseEntity.ok(note);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/notes")
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        try {
            List<NoteDTO> notes = notesService.getAllNotes();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/protected/notes/{noteId}/getDemoFile")
    public ResponseEntity<NoteFileDTO> getDemoFile(@PathVariable Long noteId) {
        try {
            NoteFileDTO demoFile = notesService.getDemoFile(noteId);
            return ResponseEntity.ok(demoFile);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
