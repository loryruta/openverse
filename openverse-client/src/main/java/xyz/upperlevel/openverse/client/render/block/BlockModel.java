package xyz.upperlevel.openverse.client.render.block;

import com.google.common.base.Preconditions;
import lombok.Getter;
import xyz.upperlevel.openverse.util.math.Aabb3f;
import xyz.upperlevel.openverse.world.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BlockModel {
    @Getter
    private Aabb3f aabb = Aabb3f.ZERO;

    @Getter
    private final List<BlockPart> parts = new ArrayList<>();

    public BlockModel() {
    }

    public void addBlockPart(BlockPart blockPart) {
        Preconditions.checkNotNull(blockPart);
        parts.add(blockPart);
        aabb = aabb.union(blockPart.getAabb());
    }

    public int getVerticesCount() {
        int cnt = 0;
        for (BlockPart part : getParts())
            cnt += part.getVerticesCount();
        return cnt;
    }

    public int getDataCount() {
        return getVerticesCount() * (3 + 3 + 1 + 1);
    }

    public int bake(World world, int x, int y, int z, ByteBuffer buffer) {
        int v = 0;
        for (BlockPart blockPart : parts) {
            v += blockPart.bake(world, x, y, z, buffer);
        }
        return v;
    }
}