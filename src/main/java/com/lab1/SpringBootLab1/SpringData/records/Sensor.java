package com.lab1.SpringBootLab1.SpringData.records;

import com.lab1.SpringBootLab1.SpringData.enums.SensorType;

public record Sensor(Long Id, String name, Double sensorValue, SensorType sensorType) {
}
