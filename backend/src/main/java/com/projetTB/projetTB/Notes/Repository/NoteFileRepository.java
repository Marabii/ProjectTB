package com.projetTB.projetTB.Notes.Repository;

import com.projetTB.projetTB.Notes.models.NoteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteFileRepository extends JpaRepository<NoteFile, Long> {
}
