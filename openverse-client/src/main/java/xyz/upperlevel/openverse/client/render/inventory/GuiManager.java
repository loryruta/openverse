package xyz.upperlevel.openverse.client.render.inventory;

import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.gui.GuiBounds;
import xyz.upperlevel.openverse.client.gui.GuiViewer;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.client.window.WindowKeyChangeEvent;
import xyz.upperlevel.openverse.inventory.PlayerInventorySession;
import xyz.upperlevel.openverse.inventory.Slot;
import xyz.upperlevel.openverse.item.ItemStack;
import xyz.upperlevel.openverse.world.entity.player.events.PlayerInventoryCloseEvent;
import xyz.upperlevel.openverse.world.entity.player.events.PlayerInventoryOpenEvent;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;

public class GuiManager implements Listener {
    private final OpenverseClient client;

    private GuiViewer viewer = new GuiViewer(Launcher.get().getWindow());
    private HandSlotGui handSlotGui;

    public GuiManager(OpenverseClient client) {
        this.client = client;

        client.getEventManager().register(this);
        Launcher.get().getWindow().getEventManager().register(WindowKeyChangeEvent.class, this::onKey, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerOpenInventory(PlayerInventoryOpenEvent event) {
        //Should never happen but you never know
        if (event.getPlayer() != OpenverseClient.get().getPlayer()) return;
        OpenverseClient.get().setCaptureInput(false);
        InventoryGui<?> currentGui = OpenverseClient.get().getInventoryGuiRegistry().create(event.getInventory());
        viewer.open(currentGui);
        currentGui.reloadLayout();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCloseInventory(PlayerInventoryCloseEvent event) {
        //Should never happen but you never know
        if (event.getPlayer() != OpenverseClient.get().getPlayer()) return;
        handSlotGui = null;
        viewer.close();
        OpenverseClient.get().setCaptureInput(true);
    }

    public void onKey(WindowKeyChangeEvent e) {
        if (viewer.getHandle().isEmpty()) return;
        if (e.getAction() == GLFW_PRESS){
            switch (e.getKey()) {
                case GLFW_KEY_ESCAPE:
                    OpenverseClient.get().getPlayer().closeInventory();
                    break;
            }
        }
    }

    public void render(float partialTicks) {
        if (!viewer.getHandle().isEmpty()) {
            glClear(GL_DEPTH_BUFFER_BIT);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            viewer.render();

            PlayerInventorySession session = OpenverseClient.get()
                    .getPlayer()
                    .getInventorySession();
            if (session != null) {
                if (handSlotGui == null) {
                    handSlotGui = new  HandSlotGui(session.getHand().get());
                }
                ItemStack item = session
                        .getHand()
                        .get()
                        .getContent();
                if (!item.isEmpty()) {
                    Window window = viewer.getCurrent().getWindow();

                    double mousePosX = window.getCursorX();
                    double mousePosY = window.getCursorY();

                    ItemRenderer renderer = OpenverseClient.get().getItemRendererRegistry().get(item.getType(client.getResources().itemTypes()));
                    int size = Math.min(window.getWidth() / 10, window.getHeight() / 10);
                    renderer.renderInSlot(item, window, new GuiBounds(mousePosX - size / 2.0, mousePosY - size / 2.0, mousePosX + size, mousePosY + size), handSlotGui);
                }
            } else {
                client.getLogger().warning("Null session!");
            }

            glDisable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
        }
    }

    protected static class HandSlotGui extends SlotGui {
        public HandSlotGui(Slot handle) {
            super(handle);
        }
    }
}
