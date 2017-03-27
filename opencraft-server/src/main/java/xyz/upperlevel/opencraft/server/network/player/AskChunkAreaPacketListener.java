package xyz.upperlevel.opencraft.server.network.player;

import xyz.upperlevel.opencraft.server.network.packet.AskChunkAreaPacket;
import xyz.upperlevel.utils.event.EventListener;

public class AskChunkAreaPacketListener extends EventListener<AskChunkAreaPacket> {

    public AskChunkAreaPacketListener() {
    }

    @Override
    public byte getPriority() {
        return 0;
    }

    @Override
    public void call(AskChunkAreaPacket packet) {
        int cx = packet.getX();
        int cy = packet.getY();
        int cz = packet.getZ();

        /*
        SingleplayerServer.connection().sendPacket(new ChunkAreaPacket(
                cx, cy, cz,
                OpenCraftServer.getChunk().getWorld()
                        .getChunk(cx, cy, cz)
                        .getArea())
        );
        */
    }
}
