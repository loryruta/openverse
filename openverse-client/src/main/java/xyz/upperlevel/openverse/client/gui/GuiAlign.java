package xyz.upperlevel.openverse.client.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GuiAlign {
    TOP_LEFT(0, 0),
    TOP_CENTER(0, 1),
    TOP_RIGHT(0, 2),
    CENTER_LEFT(1, 0),
    CENTER(1, 1),
    CENTER_RIGHT(1, 2),
    BOTTOM_LEFT(2, 0),
    BOTTOM_CENTER(2, 1),
    BOTTOM_RIGHT(2, 2);

    @Getter
    private final int vertical, horizontal;
}
