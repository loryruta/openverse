package xyz.upperlevel.openverse.client.world;

import lombok.Getter;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.network.ChunkCreatePacket;
import xyz.upperlevel.openverse.network.UniversePacket;
import xyz.upperlevel.openverse.resource.BlockType;
import xyz.upperlevel.openverse.world.Universe;
import xyz.upperlevel.openverse.world.World;
import xyz.upperlevel.openverse.world.chunk.ChunkLocation;

import static java.lang.System.*;
import static xyz.upperlevel.openverse.Openverse.getResourceManager;

public class PacketListener implements Listener {

    @Getter
    private boolean initialized = false;

    public PacketListener() {
        OpenverseClient.get().getChannel().register(this);
    }

    // when the client receives this packet it initializes universe's worlds
    @EventHandler
    public void onUniverseReceive(UniversePacket packet) {
        out.println("It has been received the universe packet.");
        out.println("Initializing " + packet.getWorlds().size() + " worlds...");

        packet.getWorlds().forEach(world -> Universe.get().addWorld(new World(world)));
        initialized = true;
    }

    // when the client receives a chunk it sets it into the referred client world
    @EventHandler
    public void onChunkReceive(ChunkCreatePacket packet) {
        long startAt = currentTimeMillis();

        ChunkLocation location = packet.getLocation();
        out.println("It has been received the chunk at: " +
                "x=" + location.x + " " +
                "y=" + location.y + " " +
                "z=" + location.z);

        World world = OpenverseClient.get().getPlayer().getWorld();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    String id = packet.getBlockType(x, y, z);
                    BlockType type = getResourceManager()
                            .getBlockTypeManager()
                            .get(id);

                    if (type == null) {
                        err.println("The block type '" + id + "' has not been found in client block type manager.");
                        continue;
                    }

                    world.getChunk(location)
                            .getBlock(x, y, z)
                            .setType(type);
                }
            }
        }

        out.println("Chunk setup successfully in " + (currentTimeMillis() - startAt) + " ms.");
    }
}