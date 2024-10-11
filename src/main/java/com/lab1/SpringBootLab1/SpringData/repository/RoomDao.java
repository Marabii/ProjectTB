package com.lab1.SpringBootLab1.SpringData.repository;

import com.lab1.SpringBootLab1.SpringData.models.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDao extends JpaRepository<RoomEntity, Long> {
}
