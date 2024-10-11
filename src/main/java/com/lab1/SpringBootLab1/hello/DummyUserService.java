package com.lab1.SpringBootLab1.hello;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DummyUserService implements UserService {
    private final ConsoleGreetingService consoleGreetingService;

    @Override
    public void greetAll(List<String> names) {
        for (String name : names) {
            consoleGreetingService.greet(name);
        }
    }
}
