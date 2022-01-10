package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowCursorEnterEvent implements Event {
    private final Window window;
}
