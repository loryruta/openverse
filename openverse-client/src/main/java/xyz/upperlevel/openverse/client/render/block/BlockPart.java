package xyz.upperlevel.openverse.client.render.block;

import lombok.Getter;
import xyz.upperlevel.openverse.util.config.Config;
import xyz.upperlevel.openverse.util.math.Aabb3f;
import xyz.upperlevel.openverse.world.World;
import xyz.upperlevel.openverse.world.block.BlockFace;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public class BlockPart {
    @Getter
    private final Aabb3f aabb;

    @Getter
    private final Path[] faceTextures = new Path[6];

    @SuppressWarnings("unchecked")
    public BlockPart(Config cfg) {
        Config fromCfg = cfg.getConfigRequired("from");
        Config toCfg = cfg.getConfigRequired("to");

        this.aabb = new Aabb3f(
                fromCfg.getFloatRequired("x"),
                fromCfg.getFloatRequired("y"),
                fromCfg.getFloatRequired("z"),
                toCfg.getFloatRequired("x"),
                toCfg.getFloatRequired("y"),
                toCfg.getFloatRequired("z")
        );

        for (Map.Entry<String, Object> faceMap : cfg.getSectionRequired("faces").entrySet()) {
            Config faceCfg = Config.wrap((Map<String, Object>) faceMap.getValue());
            BlockFace face = BlockFace.valueOf(faceMap.getKey().toUpperCase(Locale.ENGLISH).replace("_", " "));
            
            faceTextures[face.ordinal()] = Paths.get(faceCfg.getString("texture"));
        }
    }

    public int getFaceTextureLayer(BlockFace blockFace) {
        return TextureBakery.getLayer(faceTextures[blockFace.ordinal()]);
    }

    public int getVerticesCount() {
        return 6 * 6;
    }

    public int bake(World world, int x, int y, int z, ByteBuffer buffer) {
        float blockLight    = world.getBlockLight(x, y, z) / (float) 0xFF;
        float blockSkylight = world.getBlockSkylight(x, y, z) / (float) 0xFF;

        float[] floatsArr = new float[]{
                // Left face
                x, y, z,         -1, 0, 0, 0, 1, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,
                x, y, z + 1,     -1, 0, 0, 1, 1, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,
                x, y + 1, z,     -1, 0, 0, 0, 0, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,
                x, y + 1, z,     -1, 0, 0, 0, 0, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,
                x, y, z + 1,     -1, 0, 0, 1, 1, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,
                x, y + 1, z + 1, -1, 0, 0, 1, 0, getFaceTextureLayer(BlockFace.LEFT), blockLight, blockSkylight,

                // Right face
                x + 1, y, z,         1, 0, 0, 0, 1, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,
                x + 1, y + 1, z,     1, 0, 0, 0, 0, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,
                x + 1, y, z + 1,     1, 0, 0, 1, 1, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,
                x + 1, y + 1, z,     1, 0, 0, 0, 0, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,
                x + 1, y + 1, z + 1, 1, 0, 0, 1, 0, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,
                x + 1, y, z + 1,     1, 0, 0, 1, 1, getFaceTextureLayer(BlockFace.RIGHT), blockLight, blockSkylight,

                // Bottom face
                x, y, z,         0, -1, 0, 0, 1, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,
                x + 1, y, z,     0, -1, 0, 1, 1, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,
                x, y, z + 1,     0, -1, 0, 0, 0, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,
                x + 1, y, z,     0, -1, 0, 1, 1, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,
                x + 1, y, z + 1, 0, -1, 0, 1, 0, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,
                x, y, z + 1,     0, -1, 0, 0, 0, getFaceTextureLayer(BlockFace.DOWN), blockLight, blockSkylight,

                // Top face
                x, y + 1, z,         0, 1, 0, 0, 1, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,
                x, y + 1, z + 1,     0, 1, 0, 0, 0, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,
                x + 1, y + 1, z,     0, 1, 0, 1, 1, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,
                x + 1, y + 1, z,     0, 1, 0, 1, 1, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,
                x, y + 1, z + 1,     0, 1, 0, 0, 0, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,
                x + 1, y + 1, z + 1, 0, 1, 0, 1, 0, getFaceTextureLayer(BlockFace.UP), blockLight, blockSkylight,

                // Back face
                x, y, z,         0, 0, -1, 0, 1, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,
                x + 1, y + 1, z, 0, 0, -1, 1, 0, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,
                x + 1, y, z,     0, 0, -1, 1, 1, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,
                x, y, z,         0, 0, -1, 0, 1, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,
                x, y + 1, z,     0, 0, -1, 0, 0, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,
                x + 1, y + 1, z, 0, 0, -1, 1, 0, getFaceTextureLayer(BlockFace.BACK), blockLight, blockSkylight,

                // Front face
                x, y, z + 1,         0, 0, 1, 0, 1, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
                x + 1, y, z + 1,     0, 0, 1, 1, 1, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
                x + 1, y + 1, z + 1, 0, 0, 1, 1, 0, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
                x + 1, y + 1, z + 1, 0, 0, 1, 1, 0, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
                x, y + 1, z + 1,     0, 0, 1, 0, 0, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
                x, y, z + 1,         0, 0, 1, 0, 1, getFaceTextureLayer(BlockFace.FRONT), blockLight, blockSkylight,
        };

        for (float f : floatsArr) {
            buffer.putFloat(f);
        }

        return 6 * 6;
    }

    public static BlockPart deserialize(Config config) {
        return new BlockPart(config);
    }
}
