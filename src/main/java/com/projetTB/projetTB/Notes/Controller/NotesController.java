package com.projetTB.projetTB.Notes.Controller;

import com.projetTB.projetTB.Notes.Service.NotesService;
import com.projetTB.projetTB.Notes.models.Note;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/protected/notes")
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;

    @PostMapping("/upload")
    public ResponseEntity<Note> uploadNote(@RequestParam("files") MultipartFile[] files,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           HttpServletRequest request
                                           ) {
        try {
            String ownerEmail = request.getUserPrincipal().getName();
            Note note = notesService.uploadNoteFiles(files, ownerEmail, title, description);
            return ResponseEntity.ok(note);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{noteId}/grant-access")
    public ResponseEntity<Void> grantAccessToNoteFile(@PathVariable Long noteId,
                                                      @RequestParam("fileName") String fileName,
                                                      @RequestParam("emailAddress") String emailAddress) {
        try {
            notesService.grantAccessToNoteFile(noteId, fileName, emailAddress);
            return ResponseEntity.ok().build();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
