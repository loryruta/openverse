package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowResizeEvent implements Event {
    private final Window window;
    private final int width;
    private final int height;
}
