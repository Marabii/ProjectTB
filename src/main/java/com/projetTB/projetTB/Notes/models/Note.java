package com.projetTB.projetTB.Notes.models;

import com.projetTB.projetTB.Auth.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner of the note
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    private String title;
    private String description;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteFile> files = new ArrayList<>();

    // Users authorized to access the note
    @ManyToMany
    @JoinTable(
            name = "note_authorized_users",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Users> authorizedUsers = new ArrayList<>();
}
