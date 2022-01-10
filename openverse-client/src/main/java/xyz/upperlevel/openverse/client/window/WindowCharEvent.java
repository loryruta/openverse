package xyz.upperlevel.openverse.client.window;

import lombok.Data;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.openverse.client.window.Window;

@Data
public class WindowCharEvent implements Event {
    private final Window window;
    private final int codepoint;
}
