package xyz.upperlevel.openverse.client.render.inventory;

import org.joml.Matrix4f;
import xyz.upperlevel.openverse.client.gui.GuiBounds;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.item.ItemStack;

public interface ItemRenderer {
    void renderInSlot(ItemStack item, Window window, GuiBounds trans, SlotGui slot);

    void renderInHand(ItemStack item, Matrix4f trans);

    //void renderDrop(Drop drop); TODO: drops
}
