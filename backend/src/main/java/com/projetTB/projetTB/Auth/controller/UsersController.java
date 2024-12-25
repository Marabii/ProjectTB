package com.projetTB.projetTB.Auth.controller;

import com.projetTB.projetTB.Auth.DTO.UserDTO;
import com.projetTB.projetTB.Auth.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class UsersController {
    private final UsersService userService;

    @GetMapping("/api/protected/getUserInfo")
    public ResponseEntity<UserDTO> getUserData(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        UserDTO userInfo = userService.getUserInfoByEmail(userEmail);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/api/protected/verifyUser")
    public ResponseEntity<HashMap<String, Boolean>> verifyUser() {
        HashMap<String, Boolean> responseMap = new HashMap<>();
        responseMap.put("success", true);
        return ResponseEntity.ok(responseMap);
    }
}
