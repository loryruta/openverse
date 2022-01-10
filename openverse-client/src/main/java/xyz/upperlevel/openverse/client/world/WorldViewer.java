package xyz.upperlevel.openverse.client.world;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.reflect.PacketHandler;
import xyz.upperlevel.hermes.reflect.PacketListener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.util.CameraUtil;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.client.window.WindowResizeEvent;
import xyz.upperlevel.openverse.network.inventory.InventoryContentPacket;
import xyz.upperlevel.openverse.network.inventory.PlayerCloseInventoryPacket;
import xyz.upperlevel.openverse.network.inventory.PlayerOpenInventoryPacket;
import xyz.upperlevel.openverse.network.inventory.SlotChangePacket;
import xyz.upperlevel.openverse.network.world.entity.PlayerChangeLookPacket;
import xyz.upperlevel.openverse.network.world.entity.PlayerChangePositionPacket;
import xyz.upperlevel.openverse.network.world.entity.PlayerChangeWorldPacket;
import xyz.upperlevel.openverse.util.exceptions.NotImplementedException;
import xyz.upperlevel.openverse.world.Location;
import xyz.upperlevel.openverse.world.entity.LivingEntity;
import xyz.upperlevel.openverse.world.entity.player.Player;
import xyz.upperlevel.openverse.world.entity.player.PlayerInventory;

/**
 * This class represents the player.
 * <p>Better, it represents the camera moving around the world and manages rendering stuff.
 */
public class WorldViewer implements PacketListener, Listener {
    @Getter
    private final OpenverseClient client;

    @Getter
    private final WorldSession worldSession;

    @Getter
    @Setter
    private LivingEntity entity;

    private final Matrix4f viewMtx = new Matrix4f(), projMtx = new Matrix4f();
    private float aspectRatio = 1f;

    public WorldViewer(OpenverseClient client, LivingEntity entity) {
        this.client = client;

        this.entity = entity;
        this.worldSession = new WorldSession();

        Window w = Launcher.get().getWindow();
        w.getEventManager().register(this);

        reloadAspectRatio();
    }

    /**
     * Starts listening for server packets.
     */
    public void listen() {
        client.getChannel().register(this);
    }

    public void render(float partialTicks) {
        Location loc = entity.getEyePosition(partialTicks);

        CameraUtil.createViewMatrix(
                viewMtx,
                (float) Math.toRadians(loc.getYaw()), (float) Math.toRadians(loc.getPitch()),
                (float) loc.getX(), (float) loc.getY(), (float) loc.getZ()
        );

        CameraUtil.createPerspectiveMatrix(
                projMtx,
                45f,
                aspectRatio,
                0.01f, 1000f
        );

        worldSession.getChunkView().render(viewMtx, projMtx);
    }

    public void reloadAspectRatio() {
        Window w = Launcher.get().getWindow();
        aspectRatio = w.getWidth() / (float) w.getHeight();
    }

    @EventHandler
    public void onWindowResize(WindowResizeEvent e) {
        reloadAspectRatio();
    }

    @PacketHandler
    public void onPlayerChangeWorld(Connection conn, PlayerChangeWorldPacket pkt) {
        worldSession.setWorld(new ClientWorld(client, pkt.getWorldName()));
        client.getLogger().info("Viewer changed world to: " + pkt.getWorldName());
    }

    @PacketHandler
    public void onPlayerChangePosition(Connection conn, PlayerChangePositionPacket pkt) {
        //TODO update player pos
        client.getLogger().info("Viewer changed position to: " + pkt.getX() + " " + pkt.getY() + " " + pkt.getZ());
    }

    @PacketHandler
    public void onPlayerChangeLook(Connection conn, PlayerChangeLookPacket pkt) {
        //TODO update player look
        client.getLogger().info("Viewer changed position to: " + pkt.getYaw() + " " + pkt.getPitch());
    }

    @PacketHandler
    public void onSlotChange(Connection conn, SlotChangePacket packet) {
        Player player = OpenverseClient.get().getPlayer();
        if (packet.getInventoryId() == 0) {//Player inventory is always 0
            PlayerInventory inventory = player.getInventory();
            inventory.get(packet.getSlotId()).swap(packet.getNewItem());
        } else throw new NotImplementedException();
        client.getLogger().info("slot change received");
        //TODO update multi-inventory view and graphic things
    }

    @PacketHandler
    public void onInventoryContent(Connection conn, InventoryContentPacket packet) {
        Player player = OpenverseClient.get().getPlayer();
        if (packet.getInventoryId() == 0) {//Player inventory is always 0
            PlayerInventory inventory = player.getInventory();
            packet.apply(inventory);
        } else throw new NotImplementedException();
        client.getLogger().info("Inventory content received");
        //TODO update multi-inventory view and graphic things
    }


    @PacketHandler
    public void onPlayerOpenInventory(Connection conn, PlayerOpenInventoryPacket packet) {
        Player player = OpenverseClient.get().getPlayer();

        // Player#openInventory() is seen as an input so it sends the packet while
        // Player#openInventory(Inventory) does not
        player.openInventory(player.getInventory());
    }

    @PacketHandler
    public void onPlayerCloseInventory(Connection conn, PlayerCloseInventoryPacket packet) {
        Player player = OpenverseClient.get().getPlayer();
        player.onCloseInventory();
    }
}
