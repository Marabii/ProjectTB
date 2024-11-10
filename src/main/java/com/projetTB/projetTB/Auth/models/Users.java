package com.projetTB.projetTB.Auth.models;

import com.projetTB.projetTB.Auth.DTO.VerificationDetails;
import com.projetTB.projetTB.Auth.enums.ROLE;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_details_id", referencedColumnName = "id")
    private VerificationDetails verificationDetails;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @Column(nullable = false)
    private String password;

    @Column
    private String phoneNumber;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String username;

    @Column
    @Builder.Default
    private boolean isAuthenticatedByGoogle = false;

    @NotBlank
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ROLE role = ROLE.USER;

    @Column
    @Builder.Default
    private String profilePicture = "https://t4.ftcdn.net/jpg/05/49/98/39/240_F_549983970_bRCkYfk0P6PP5fKbMhZMIb07mCJ6esXL.jpg";

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
