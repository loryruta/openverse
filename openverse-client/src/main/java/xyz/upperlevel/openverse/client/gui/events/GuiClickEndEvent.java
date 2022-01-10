package xyz.upperlevel.openverse.client.gui.events;

import lombok.Getter;
import lombok.NonNull;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiClickEndEvent extends GuiEvent {
    @Getter
    private final double x, y;

    @Getter
    private final int button;

    public GuiClickEndEvent(@NonNull Gui gui, double x, double y, int button) {
        super(gui);
        this.x = x;
        this.y = y;
        this.button = button;
    }
}
