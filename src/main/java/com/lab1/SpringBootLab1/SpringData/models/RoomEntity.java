package com.lab1.SpringBootLab1.SpringData.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Entity
@Table(name = "SP_ROOM")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"windows"})
public class RoomEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private String name;

    @OneToOne
    private SensorEntity currentTemperature;

    private Double targetTemperature;

    @OneToMany(mappedBy="room")
    private List<WindowEntity> windows;

    @OneToMany(mappedBy = "room")
    private List<HeaterEntity> heaters;

    public RoomEntity(int floor, String name) {
        this.floor = floor;
        this.name = name;
    }

}
