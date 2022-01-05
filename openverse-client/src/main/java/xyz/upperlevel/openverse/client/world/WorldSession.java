package xyz.upperlevel.openverse.client.world;

import lombok.Getter;
import xyz.upperlevel.hermes.reflect.PacketListener;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.render.world.ChunkViewRenderer;

/**
 * Handles world renderers and changes.
 */
public class WorldSession implements PacketListener {
    @Getter
    private ClientWorld world;

    @Getter
    private final ChunkViewRenderer chunkView;

    public WorldSession() {
        this.chunkView = new ChunkViewRenderer();
        this.world = (ClientWorld) OpenverseClient.get().getPlayer().getWorld();
        chunkView.setWorld(world);
    }

    public void setWorld(ClientWorld world) {
        OpenverseClient.get().getLogger().info("Setting world to: " + world.getName());
        this.world = world;
        chunkView.setWorld(world);
    }
}
