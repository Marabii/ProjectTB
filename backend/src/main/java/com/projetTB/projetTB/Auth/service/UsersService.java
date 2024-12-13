package com.projetTB.projetTB.Auth.service;

import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository repository;

    public Map<String, Object> getUserInfoByEmail(String userEmail) {
        Users user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found, please create account"));
        Map<String, Object> filteredUser = new HashMap<>();
        filteredUser.put("createdAt", user.getCreatedAt());
        filteredUser.put("phoneNumber", user.getPhoneNumber());
        filteredUser.put("name", user.getUsername());
        filteredUser.put("Email", user.getEmail());
        filteredUser.put("isAuthenticatedByGoogle", user.isAuthenticatedByGoogle());
        filteredUser.put("profilePicture", user.getProfilePicture());
        filteredUser.put("_id", user.getId());
        return filteredUser;
    }
}
