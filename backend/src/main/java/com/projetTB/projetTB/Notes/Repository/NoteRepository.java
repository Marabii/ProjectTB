package com.projetTB.projetTB.Notes.Repository;

import com.projetTB.projetTB.Notes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
