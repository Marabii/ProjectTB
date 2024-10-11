package com.lab1.SpringBootLab1.hello;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AnotherConsoleGreetingService implements GreetingService {

    public void greet(String name) {
        System.out.println("Bonjour, " + name + "!");
    }
}
