package xyz.upperlevel.openverse.client.gui.events;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiDragEvent extends GuiEvent {
    @Getter
    @Setter
    private double startX, startY, endX, endY;

    public GuiDragEvent(Gui gui, double startX, double startY, double endX, double endY) {
        super(gui);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
