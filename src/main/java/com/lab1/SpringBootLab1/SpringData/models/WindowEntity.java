package com.lab1.SpringBootLab1.SpringData.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SP_WINDOW")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"windowStatus", "room"})
public class WindowEntity {
    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    private SensorEntity windowStatus;

    @ManyToOne
    private RoomEntity room;

    public WindowEntity(String name, SensorEntity sensor) {
        this.windowStatus = sensor;
        this.name = name;
    }

}