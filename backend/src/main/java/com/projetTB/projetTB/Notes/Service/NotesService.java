package com.projetTB.projetTB.Notes.Service;

import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import com.projetTB.projetTB.Notes.Repository.NoteRepository;
import com.projetTB.projetTB.Notes.models.Note;
import com.projetTB.projetTB.Notes.models.NoteFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesService {
    private final CDNService cdnService;
    private final NoteRepository noteRepository;
    private final UsersRepository userRepository;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    public Note uploadNoteFiles(MultipartFile[] files, String ownerEmail, String title, String description) throws IOException {
        Users owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + ownerEmail));

        Note note = Note.builder()
                .owner(owner)
                .title(title)
                .description(description)
                .build();

        List<NoteFile> noteFiles = new ArrayList<>();

        noteRepository.save(note); // Save to get the generated ID

        String sanitizedNoteId = sanitizeFolderName(note.getId().toString());

        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            String fileName = (originalFileName != null && !originalFileName.isEmpty())
                    ? sanitizeFileName(originalFileName)
                    : generateUniqueFileName();
            String contentType = file.getContentType();

            cdnService.uploadFile(file.getBytes(), sanitizedNoteId, fileName, ownerEmail, contentType);

            String fileUrl = "https://storage.cloud.google.com/" + bucketName + "/" + sanitizedNoteId + "/" + fileName;

            NoteFile noteFile = NoteFile.builder()
                    .note(note)
                    .fileUrl(fileUrl)
                    .fileName(fileName)
                    .build();

            noteFiles.add(noteFile);
        }

        note.setFiles(noteFiles);
        return noteRepository.save(note);
    }

    public void grantAccessToNoteFile(Long noteId, String fileName, String emailAddress) throws IOException {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        Users user = userRepository.findByEmail(emailAddress)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + emailAddress));

        String sanitizedNoteId = sanitizeFolderName(note.getId().toString());
        String sanitizedFileName = sanitizeFileName(fileName);
        cdnService.grantAccessToFile(sanitizedNoteId, sanitizedFileName, emailAddress);

        if (!note.getAuthorizedUsers().contains(user)) {
            note.getAuthorizedUsers().add(user);
            noteRepository.save(note);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[/\\\\]", "_");
    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[/\\\\]", "_");
    }

    private String generateUniqueFileName() {
        return UUID.randomUUID().toString();
    }
}
