package xyz.upperlevel.openverse.client.game;

import lombok.Getter;
import org.joml.Vector3i;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.util.ScreenshotUtil;
import xyz.upperlevel.openverse.client.window.WindowKeyChangeEvent;
import xyz.upperlevel.openverse.client.window.WindowMouseButtonChangeEvent;
import xyz.upperlevel.openverse.client.window.WindowScrollEvent;
import xyz.upperlevel.openverse.client.world.WorldViewer;
import xyz.upperlevel.openverse.client.world.updater.PlayerLocationWatcher;
import xyz.upperlevel.openverse.util.math.LineVisitor3d;
import xyz.upperlevel.openverse.world.Location;
import xyz.upperlevel.openverse.world.chunk.Block;
import xyz.upperlevel.openverse.world.entity.Entity;
import xyz.upperlevel.openverse.world.entity.EntityManager;
import xyz.upperlevel.openverse.world.entity.player.Player;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Scene instantiated when the player, the world and the initial chunks are received.
 */
public class PlayingWorldScene implements Scene, Listener {
    private static final File SCREENSHOTS_DIR = new File("screenshots");

    @Getter
    private final OpenverseClient client;

    @Getter
    private final WorldViewer worldViewer;

    @Getter
    private final PlayerLocationWatcher playerWatcher;

    @Getter
    private long ticksEach;

    @Getter
    private long lastTick;

    static {
        SCREENSHOTS_DIR.mkdir();
    }

    public PlayingWorldScene(OpenverseClient client, Player player) {
        this.client = client;

        OpenverseClient.get().setPlayer(player);
        this.worldViewer   = new WorldViewer(client, player);
        this.playerWatcher = new PlayerLocationWatcher(client, player);

        Launcher.get().getWindow().getEventManager().register(this);
    }

    @Override
    public void onEnable(Scene scene) {
        worldViewer.listen();
        ticksEach = Launcher.TICK_EACH;
        lastTick = System.currentTimeMillis();

        OpenverseClient.get().setCaptureInput(true);
    }

    @Override
    public void onDisable(Scene scene) {
    }

    @Override
    public void onTick() {
        lastTick = System.currentTimeMillis();
        playerWatcher.update();
    }

    public float getPartialTicks() {
        return (System.currentTimeMillis() - lastTick) / (float)ticksEach;
    }

    @Override
    public void onFps() {
        EntityManager.ENTITY_TICK_PROFILER.reset();//TODO: add ProfileSystem
    }

    @Override
    public void onRender() {
        glClearColor(0.0f, 0.0f, 1.0f, 0);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        OpenverseClient.get().getDebugGuiManager().render();

        float partialTicks = getPartialTicks();
        worldViewer.render(partialTicks);
        OpenverseClient.get().getGuiManager().render(partialTicks);
    }

    private Player getPlayer() {
        return OpenverseClient.get().getPlayer();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKey(WindowKeyChangeEvent event) {
        if (event.getAction() == GLFW_PRESS) {
            int key = event.getKey();
            switch (key) {
                case GLFW_KEY_L:
                    Location loc = worldViewer.getEntity().getLocation(getPartialTicks());
                    client.getLogger().info("loc: " + loc.toStringComplete());
                    break;
                case GLFW_KEY_F1:
                    File file = new File(SCREENSHOTS_DIR, System.currentTimeMillis() + ".png");
                    try {
                        ScreenshotUtil.saveToFile(Launcher.get().getWindow(), file, "png");
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    client.getLogger().fine("Screenshot saved in " + file.getAbsolutePath());
                    break;
                case GLFW_KEY_F3:
                    boolean ssao = !client.isSsaoEnabled();
                    client.setSsaoEnabled(ssao);
                    client.getLogger().fine(String.format("SSAO enabled? %b", ssao));
                    break;
                case GLFW_KEY_E:
                    getPlayer().openInventory();
                    break;
                case GLFW_KEY_Q: //TODO: replace Q with ESCAPE
                    Player p = getPlayer();
                    if (p.hasOpenedInventory()) {
                        p.closeInventory();
                    }
                    break;
            }
            // Inventory switching with numbers
            if (key >= GLFW_KEY_1 && key <= GLFW_KEY_9) {
                getPlayer().getInventory().setHandSlot(key - GLFW_KEY_1);
            }
        }
    }

    @EventHandler
    public void onScroll(WindowScrollEvent e) {
        // If game interaction is disabled quit event handling
        if (!OpenverseClient.get().isCaptureInput()) return;
        Player p = OpenverseClient.get().getPlayer();
        p.getInventory().scrollHand(e.getYOffset() > 0 ? -1 : 1);
    }

    @EventHandler
    public void onClick(WindowMouseButtonChangeEvent e) {
        // If game interaction is disabled quit event handling
        if (!OpenverseClient.get().isCaptureInput()) return;
        if (e.getAction() == GLFW_PRESS) {
            Entity clicker = worldViewer.getEntity();
            LineVisitor3d.RayCastResult rayCast = clicker.rayCast(getPartialTicks());
            if (rayCast == null) {
                client.getLogger().info("Clicked nothing");
            } else {

                Vector3i loc = rayCast.getBlock();
                Block block = clicker.getWorld().getBlock(loc);
                client.getLogger().info("Clicked " + block);
                Block facing = block.getRelative(rayCast.getFace());
                client.getLogger().info("Sky: " + facing.getSkyLight() + ", Block: " + facing.getBlockLight());

                if (e.getButton() == GLFW_MOUSE_BUTTON_LEFT) {
                    // TODO: only player can break blocks, right? 0_o
                    ((Player) clicker).breakBlock(loc.x, loc.y, loc.z);
                } else {
                    ((Player) clicker).useItemInHand(loc.x, loc.y, loc.z, rayCast.getFace());
                }
            }
        }
    }
}
