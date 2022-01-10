package xyz.upperlevel.openverse.client.render.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.openverse.client.gui.Gui;
import xyz.upperlevel.openverse.inventory.Inventory;

@RequiredArgsConstructor
public abstract class InventoryGui<I extends Inventory> extends Gui {
    @Getter
    private final I handle;
}
