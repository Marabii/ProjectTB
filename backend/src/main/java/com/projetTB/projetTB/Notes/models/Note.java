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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    private String title;
    private String description;
    private Double price;

    @Builder.Default
    private boolean isDigital = false;

    @Builder.Default
    private boolean isAvailable = true;

    private String secretPassword;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<NoteFile> files = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "demo_file_id")
    private NoteFile demoFile;

    // Users authorized to access the note
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "note_authorized_users", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private List<Users> authorizedUsers = new ArrayList<>();
}
