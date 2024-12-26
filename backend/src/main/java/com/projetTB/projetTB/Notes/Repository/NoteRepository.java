package com.projetTB.projetTB.Notes.Repository;

import com.projetTB.projetTB.Notes.models.Note;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Method to check if there are notes with isDigital set to false and
    // isAvailable set to true
    boolean existsByIsDigitalFalseAndIsAvailableTrue();

    // Find notes by owner's email
    List<Note> findByOwner_Email(String email);

    // Find notes that are deposited in the box:
    List<Note> findByIsDigitalFalseAndIsAvailableTrue();
}
