package com.lab1.SpringBootLab1.SpringData.records;

import com.lab1.SpringBootLab1.SpringData.enums.WindowStatus;

public record Window(Long id, String name, WindowStatus windowStatus, Long roomId) {
}