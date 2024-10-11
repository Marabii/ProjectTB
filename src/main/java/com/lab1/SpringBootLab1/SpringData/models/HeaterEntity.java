package com.lab1.SpringBootLab1.SpringData.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SP_HEATER")
@NoArgsConstructor
public class HeaterEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String  name;

    @ManyToOne
    private RoomEntity room;

    @Column(nullable = false)
    private SensorEntity status;

    public HeaterEntity(String name, SensorEntity status, RoomEntity room) {
        this.name = name;
        this.status = status;
        this.room = room;
    }
}
