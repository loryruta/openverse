package xyz.upperlevel.openverse.client.gui.events;

import lombok.NonNull;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiCloseEvent extends GuiEvent {
    public GuiCloseEvent(@NonNull Gui gui) {
        super(gui);
    }
}
