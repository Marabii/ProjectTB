package com.projetTB.projetTB.Auth.dataValidators;


import com.projetTB.projetTB.Auth.exceptions.InvalidInputException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AccountValidator {

    public String emailValidator(String email) {
        // Define the regex pattern for validating an Email address
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(emailRegex);

        // Check if the Email matches the regex pattern
        if (email == null || !pattern.matcher(email).matches()) {
            throw new InvalidInputException("Invalid Email format");
        }

        // If Email passes the regex test, return it
        return email;
    }

    public String phoneNumberValidator(String phoneNumber) {
        // Define the regex pattern for validating a phone number
        String phoneRegex = "^\\+?[0-9. ()-]{7,25}$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(phoneRegex);

        // Check if the phone number matches the regex pattern
        if (phoneNumber == null || !pattern.matcher(phoneNumber).matches()) {
            throw new InvalidInputException("Invalid phone number format");
        }

        // If phone number passes the regex test, return it
        return phoneNumber;
    }

    public String nameValidator(String name) {
        // Define the regex pattern for validating a name
        String nameRegex = "^[\\p{L} .'-]+$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(nameRegex);

        // Check if the name matches the regex pattern
        if (name == null || !pattern.matcher(name).matches() || name.length() < 2 || name.length() > 50) {
            throw new InvalidInputException("Invalid name format");
        }

        // If name passes the regex test, return it
        return name;
    }

    public String passwordValidator(String password) {
        // Define the regex pattern for validating a strong password
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(passwordRegex);

        // Check if the password matches the regex pattern
        if (password == null || !pattern.matcher(password).matches()) {
            throw new InvalidInputException("Password must be at least 8 characters long, and include one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        // If password passes the regex test, return it
        return password;
    }
}
