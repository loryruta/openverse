package xyz.upperlevel.openverse.client.gui.events;

import lombok.Getter;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.gui.Gui;

public class GuiEvent implements Event {
    @Getter
    private final Gui gui;

    public GuiEvent(Gui gui) {
        this.gui = gui;
    }
}
