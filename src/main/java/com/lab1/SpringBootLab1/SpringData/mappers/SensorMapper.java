package com.lab1.SpringBootLab1.SpringData.mappers;

import com.lab1.SpringBootLab1.SpringData.models.SensorEntity;
import com.lab1.SpringBootLab1.SpringData.records.Sensor;

public class SensorMapper {
    public static Sensor mapToSensor(SensorEntity sensorEntity) {
        return new Sensor(sensorEntity.getId(), sensorEntity.getName(), sensorEntity.getSensorValue(), sensorEntity.getSensorType());
    }
}
