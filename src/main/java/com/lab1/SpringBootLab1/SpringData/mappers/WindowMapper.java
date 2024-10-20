package com.lab1.SpringBootLab1.SpringData.mappers;

import com.lab1.SpringBootLab1.SpringData.enums.WindowStatus;
import com.lab1.SpringBootLab1.SpringData.models.WindowEntity;
import com.lab1.SpringBootLab1.SpringData.records.Window;

public class WindowMapper {
    public static Window mapToWindow(WindowEntity windowEntity) {
        WindowStatus windowStatus = windowEntity.getWindowStatus().getSensorValue() == 0 ? WindowStatus.CLOSED : WindowStatus.OPENED;
        return new Window(windowEntity.getId(), windowEntity.getName(), windowStatus, windowEntity.getRoom().getId());
    }
}
