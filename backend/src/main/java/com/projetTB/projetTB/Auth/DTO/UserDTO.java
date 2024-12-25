package com.projetTB.projetTB.Auth.DTO;

import lombok.Builder;

import com.projetTB.projetTB.Auth.models.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private VerificationDetails verificationDetails;

    public static UserDTO parseUserToUserDTO(Users userInput) {
        UserDTO result = UserDTO.builder().id(userInput.getId()).username(userInput.getUsername())
                .phoneNumber(userInput.getPhoneNumber()).email(userInput.getEmail())
                .verificationDetails(userInput.getVerificationDetails()).build();

        return result;
    }
}
