package com.projetTB.projetTB.Auth.service;

import com.projetTB.projetTB.Auth.DTO.UserDTO;
import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository repository;

    public UserDTO getUserInfoByEmail(String userEmail) {
        Users user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found, please create account"));
        UserDTO filteredUser = UserDTO.parseUserToUserDTO(user);
        return filteredUser;
    }
}
