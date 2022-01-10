package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowKeyChangeEvent implements Event {
    private final Window window;
    private final int key;
    private final int scancode;
    private final int action;
    private final int mods;
}
