package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowMouseButtonChangeEvent implements Event {
    private final Window window;
    private final int button;
    private final int action;
    private final int mode;
}
