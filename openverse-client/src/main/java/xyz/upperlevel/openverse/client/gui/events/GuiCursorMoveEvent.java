package xyz.upperlevel.openverse.client.gui.events;

import lombok.Getter;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiCursorMoveEvent extends GuiEvent {
    @Getter
    private double startX, startY, endX, endY;

    public GuiCursorMoveEvent(Gui gui, double startX, double startY, double endX, double endY) {
        super(gui);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}