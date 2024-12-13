package com.projetTB.projetTB.Auth.service;

import com.projetTB.projetTB.Auth.DTO.AuthenticationRequest;
import com.projetTB.projetTB.Auth.DTO.AuthenticationResponse;
import com.projetTB.projetTB.Auth.DTO.RegisterRequest;
import com.projetTB.projetTB.Auth.dataValidators.AccountValidator;
import com.projetTB.projetTB.Auth.enums.ROLE;
import com.projetTB.projetTB.Auth.exceptions.UserAlreadyExistsException;
import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AccountValidator accountValidator;

    public AuthenticationResponse register(RegisterRequest request) {
        // Validate all fields
        String email = accountValidator.emailValidator(request.getEmail());
        String password = accountValidator.passwordValidator(request.getPassword());
        String name = accountValidator.nameValidator(request.getName());
        String phoneNumber = accountValidator.phoneNumberValidator(request.getPhoneNumber());

        // Check if the user already exists
        if (repository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("You already have an account");
        }

        // Encode the password using PasswordEncoder
        String encodedPassword = passwordEncoder.encode(password);

        // Create and save a new user
        Users newUser = Users.builder()
                .email(email)
                .password(encodedPassword)
                .username(name)
                .phoneNumber(phoneNumber)
                .createdAt(new Date())
                .updatedAt(new Date())
                .role(ROLE.USER)
                .build();

        repository.save(newUser);

        // Generate and return the JWT token
        String jwtToken = jwtService.generateToken(newUser);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = accountValidator.emailValidator(request.getEmail());
        String password = accountValidator.passwordValidator(request.getPassword());


        // Find the user by Email
        Users user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with Email: " + email));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password for Email: " + email);
        }

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


}
