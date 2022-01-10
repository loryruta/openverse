package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowScrollEvent implements Event {
    private final Window window;
    private final double xOffset;
    private final double yOffset;
}
