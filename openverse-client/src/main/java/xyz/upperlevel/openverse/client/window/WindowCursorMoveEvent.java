package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowCursorMoveEvent implements Event {
    private final Window window;
    private final double xPos;
    private final double yPos;
}
