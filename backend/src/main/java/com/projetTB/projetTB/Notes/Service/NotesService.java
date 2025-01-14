package com.projetTB.projetTB.Notes.Service;

import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import com.projetTB.projetTB.Notes.DTOs.NoteDTO;
import com.projetTB.projetTB.Notes.DTOs.NoteFileDTO;
import com.projetTB.projetTB.Notes.Repository.NoteRepository;
import com.projetTB.projetTB.Notes.models.Note;
import com.projetTB.projetTB.Notes.models.NoteFile;
import com.projetTB.projetTB.Payments.CreatePaymentLink.Service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional; // <-- important for rollback behavior

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final CDNService cdnService;
    private final NoteRepository noteRepository;
    private final UsersRepository userRepository;
    private final EmailService emailService;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    /**
     * Upload multiple note files plus an optional demo file for a given user. If
     * any file fails to upload to GCS, the entire transaction is rolled back and
     * nothing is saved in the database.
     */
    @Transactional(rollbackOn = Exception.class)
    public NoteDTO uploadNoteFiles(MultipartFile[] files, MultipartFile demoFile, String ownerEmail, String title,
            String description, Double price, boolean isDigital) throws IOException {

        // 1) Check if the user exists
        Users owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + ownerEmail));

        // 2) Create a new note (unsaved) and persist it to get the note ID
        Note note = Note.builder().owner(owner).title(title).description(description).price(price).isDigital(isDigital)
                .isAvailable(isDigital).build();

        // Save the note to generate an ID so we can use it for naming folders in GCS
        noteRepository.save(note);

        // We'll keep track of all uploaded NoteFile objects here
        List<NoteFile> noteFiles = new ArrayList<>();

        // 4) Process "regular" files (private by default)
        if (files != null && isDigital) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    // Upload to GCS, build a NoteFile instance (not saved individually)
                    NoteFile noteFile = uploadFileToGCSAndCreateNoteFile(file, note, ownerEmail, false, // <-- private
                            note.getId());
                    noteFiles.add(noteFile);
                }
            }
            // Associate all noteFiles with the note
            note.setFiles(noteFiles);
        }

        // 5) Process demoFile if provided (public by design)
        if (demoFile != null && !demoFile.isEmpty()) {
            NoteFile demoNoteFile = uploadFileToGCSAndCreateNoteFile(demoFile, note, ownerEmail, true, // <-- public
                    note.getId());
            note.setDemoFile(demoNoteFile);
        }

        // 6) Attempt final save of the note
        Note result = noteRepository.save(note);
        return NoteDTO.parseNoteToNoteDTO(result);
    }

    /**
     * Helper: Upload a file to GCS for a particular Note and return the
     * corresponding NoteFile object (not yet saved to DB).
     */
    private NoteFile uploadFileToGCSAndCreateNoteFile(MultipartFile file, Note note, String ownerEmail,
            boolean isPublic, Long noteId) throws IOException {

        // Prepare unique or sanitized file name
        String originalFileName = file.getOriginalFilename();
        String fileName = (originalFileName != null && !originalFileName.isEmpty()) ? sanitizeFileName(originalFileName)
                : generateUniqueFileName();

        String contentType = file.getContentType();

        // Upload the file to GCS via CDNService; if this fails, an IOException is
        // thrown
        cdnService.uploadFile(file.getBytes(), noteId, fileName, ownerEmail, contentType);

        // Generate a file URL, depending on "public" or not
        String fileUrl = isPublic ? "https://storage.googleapis.com/" + bucketName + "/" + noteId + "/" + fileName
                : "https://storage.cloud.google.com/" + bucketName + "/" + noteId + "/" + fileName;

        // Create the NoteFile entity (still transient in memory).
        NoteFile noteFile = NoteFile.builder().note(note).fileUrl(fileUrl).fileName(fileName).build();

        // If it's public, we can call a method to set a public ACL (everyone has read
        // access)
        if (isPublic) {
            // We add a second call to set "allUsers" to READER so that the file is publicly
            // accessible.
            cdnService.grantAccessToFile(noteId, fileName, "allUsers");
        }

        return noteFile;
    }

    /**
     * Grant access to a note file in GCS. Also link the user to the note's
     * authorized users if not already present. This is used for private files that
     * need to be shared with specific individuals.
     */
    @Transactional(rollbackOn = Exception.class)
    public void grantAccessToNoteFile(Long noteId, String emailAddress) throws IOException {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        Users user = userRepository.findByEmail(emailAddress)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + emailAddress));

        note.getFiles().stream().forEach(file -> {
            String fileName = file.getFileName();

            // Use CDN service to grant access at the cloud storage level
            try {
                cdnService.grantAccessToFile(noteId, fileName, emailAddress);
            } catch (IOException e) {
                try {
                    emailService.sendEmailFailureNotification(note, emailAddress);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        });

        // Also add the user to the authorized users if not already present
        if (!note.getAuthorizedUsers().contains(user)) {
            note.getAuthorizedUsers().add(user);
            noteRepository.save(note);
        }
    }

    public void depositDocuments(Long noteId) {
        if (noteRepository.existsByIsDigitalFalseAndIsAvailableTrue())
            throw new IllegalStateException(
                    "there are already documents deposited in the box, wait until it's available");

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        note.setAvailable(true);
        noteRepository.save(note);
    }

    public void takeDocuments(String secretCode) {
        System.out.println("the input password is: " + secretCode);
        // Fetch notes from the repository
        List<Note> notes = noteRepository.findByIsDigitalFalseAndIsAvailableTrue();

        // Check if there are multiple or no notes
        if (notes.size() > 1)
            throw new RuntimeException("Something went wrong");
        if (notes.size() == 0)
            throw new RuntimeException("There are no available notes in the box at the moment");

        // Validate the secret code
        Note note = notes.get(0);
        if (!note.getSecretPassword().trim().equalsIgnoreCase(secretCode.trim()))
            throw new IllegalArgumentException("wrong password");

        // Mark the note as unavailable and save it
        note.setAvailable(false);
        note.setSecretPassword("");
        noteRepository.save(note);
    }

    /**
     * Retrieve all notes
     */
    public List<NoteDTO> getAllNotes() {
        List<Note> notes = noteRepository.findAll();
        if (notes.isEmpty()) {
            return Collections.emptyList();
        } else {
            return notes.stream().map(note -> NoteDTO.parseNoteToNoteDTO(note)).toList();
        }
    }

    /**
     * Retrieve all user's notes
     */
    public List<NoteDTO> getMyNotes(String userEmail) {
        List<Note> notes = noteRepository.findByOwner_Email(userEmail);
        if (notes.isEmpty()) {
            return Collections.emptyList();
        } else {
            return notes.stream().map(note -> NoteDTO.parseNoteToNoteDTO(note)).toList();
        }
    }

    /**
     * Retrieve the demo file for a given Note.
     */
    public NoteFileDTO getDemoFile(Long noteId) {
        NoteFile result = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + noteId)).getDemoFile();

        return NoteFileDTO.parseNoteFileToNoteFileDTO(result);
    }

    // --- Helper Methods ---

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[/\\\\]", "_");
    }

    private String generateUniqueFileName() {
        return UUID.randomUUID().toString();
    }

    public List<NoteDTO> getFavouriteDocuments(String userEmail) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        return user.getFavouriteNotes().stream().map(NoteDTO::parseNoteToNoteDTO).collect(Collectors.toList());
    }

    public void addToFavouriteDocuments(String userEmail, Long noteId) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        List<Note> favouriteNotes = user.getFavouriteNotes();

        if (favouriteNotes.contains(note)) {
            throw new RuntimeException("Note is already in favorites.");
        }

        favouriteNotes.add(note);
        user.setFavouriteNotes(favouriteNotes);

        userRepository.save(user);
    }

    public void removeFromFavouriteDocuments(String userEmail, Long noteId) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        List<Note> favouriteNotes = user.getFavouriteNotes();

        if (!favouriteNotes.contains(note)) {
            throw new RuntimeException("Note is not in favorites.");
        }

        favouriteNotes.remove(note);
        user.setFavouriteNotes(favouriteNotes);

        userRepository.save(user);
    }
}
