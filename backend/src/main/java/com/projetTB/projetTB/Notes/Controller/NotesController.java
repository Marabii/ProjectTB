package com.projetTB.projetTB.Notes.Controller;

import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
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
    public ResponseEntity<NoteDTO> uploadNote(
            @RequestParam(value = "documents", required = false) MultipartFile[] files,
            @RequestParam("demoFile") MultipartFile demoFile, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Double price,
            @RequestParam("isDigital") boolean isDigital, HttpServletRequest request) {

        String ownerEmail = request.getUserPrincipal().getName();
        try {
            // Handle cases where files might be null or empty
            if (files == null || files.length == 0) {
                System.out.println("No document files provided.");
            } else {
                System.out.println("Document files provided: " + files.length);
            }

            NoteDTO note = notesService.uploadNoteFiles(files, demoFile, ownerEmail, title, description, price,
                    isDigital);
            return ResponseEntity.ok(note);
        } catch (IOException e) {
            System.out.println(e.getMessage());
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

    @GetMapping("/protected/notes")
    public ResponseEntity<List<NoteDTO>> getAllPersonalNotes(HttpServletRequest request) {
        String ownerEmail = request.getUserPrincipal().getName();
        try {
            List<NoteDTO> notes = notesService.getMyNotes(ownerEmail);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/protected/favourite-documents")
    public ResponseEntity<List<NoteDTO>> getFavouriteNotes(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        try {
            List<NoteDTO> favouriteNotes = notesService.getFavouriteDocuments(userEmail);
            return ResponseEntity.ok(favouriteNotes);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Log the exception (omitted for brevity)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/protected/favourite-documents/{noteId}")
    public ResponseEntity<String> addToFavouriteNotes(HttpServletRequest request, @PathVariable Long noteId) {
        String userEmail = request.getUserPrincipal().getName();
        System.out.println(noteId);
        System.out.println("api called");
        try {
            notesService.addToFavouriteDocuments(userEmail, noteId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Note added to favorites successfully.");
        } catch (UserNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Log the exception (omitted for brevity)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/protected/favourite-documents/{noteId}")
    public ResponseEntity<String> removeFromFavouriteNotes(HttpServletRequest request, @PathVariable Long noteId) {
        String userEmail = request.getUserPrincipal().getName();
        System.out.println(noteId);
        System.out.println("removeFromFavouriteNotes API called");
        try {
            notesService.removeFromFavouriteDocuments(userEmail, noteId);
            return ResponseEntity.ok("Note removed from favorites successfully.");
        } catch (UserNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Log the exception (omitted for brevity)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
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

    @PutMapping("/protected/notes/{noteId}/deposit-documents")
    public ResponseEntity<Void> depositDocuments(@PathVariable Long noteId) {
        try {
            notesService.depositDocuments(noteId);
            return ResponseEntity.ok().build(); // Success response with 200 OK
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/notes/takeDocuments/{secretCode}")
    public ResponseEntity<String> takeDocuments(@PathVariable String secretCode) {
        System.out.println("api called");
        try {
            notesService.takeDocuments(secretCode);
            return ResponseEntity.ok("Document taken successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}
