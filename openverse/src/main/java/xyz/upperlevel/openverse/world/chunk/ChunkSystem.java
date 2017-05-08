package xyz.upperlevel.openverse.world.chunk;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.openverse.world.World;

@RequiredArgsConstructor
public abstract class ChunkSystem {

    @Getter
    @NonNull
    private final World world;

    public abstract Chunk getChunk(int x, int y, int z);

    public abstract Chunk getChunk(ChunkLocation location);
}
