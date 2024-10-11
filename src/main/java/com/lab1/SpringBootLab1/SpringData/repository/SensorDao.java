package com.lab1.SpringBootLab1.SpringData.repository;

import com.lab1.SpringBootLab1.SpringData.models.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDao extends JpaRepository<SensorEntity, Long> {
}
