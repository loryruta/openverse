package xyz.upperlevel.openverse.client.gui.events;

import lombok.Getter;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiCursorExitEvent extends GuiEvent {
    @Getter
    private double x, y;

    public GuiCursorExitEvent(Gui gui, double x, double y) {
        super(gui);
        this.x = x;
        this.y = y;
    }
}
