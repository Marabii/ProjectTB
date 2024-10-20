package com.lab1.SpringBootLab1.SpringData.mappers;

import com.lab1.SpringBootLab1.SpringData.models.RoomEntity;
import com.lab1.SpringBootLab1.SpringData.records.Room;
import com.lab1.SpringBootLab1.SpringData.records.Window;

import java.util.List;

public class RoomMapper {
    public static Room mapToRoom(RoomEntity roomEntity) {
        List<Window> windows = roomEntity.getWindows().stream().map(WindowMapper::mapToWindow).toList();
        return new Room(roomEntity.getId(), roomEntity.getName(), roomEntity.getFloor(), roomEntity.getCurrentTemperature().getSensorValue(), roomEntity.getTargetTemperature().getSensorValue(), windows);
    }
}
