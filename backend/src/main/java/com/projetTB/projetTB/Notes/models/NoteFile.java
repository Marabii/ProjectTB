package com.projetTB.projetTB.Notes.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "note_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The note to which this file belongs
    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    // URL of the file in GCS
    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;
}
