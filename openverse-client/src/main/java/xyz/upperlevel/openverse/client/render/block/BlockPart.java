package xyz.upperlevel.openverse.client.render.block;

import lombok.Getter;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.openverse.util.config.Config;
import xyz.upperlevel.openverse.util.math.Aabb3f;
import xyz.upperlevel.openverse.world.World;
import xyz.upperlevel.openverse.world.block.BlockFace;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public class BlockPart {
    @Getter
    private final Aabb3f aabb;

    @Getter
    private final File[] faceTextures = new File[6];

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
            
            faceTextures[face.ordinal()] = new File(faceCfg.getString("texture"));
        }
    }

    public int getFaceTextureLayerIndex(BlockFace blockFace) {
        return TextureBakery.get().getLayer(faceTextures[blockFace.ordinal()]);
    }

    public int getVerticesCount() {
        return 6 * 6;
    }

    private float getBlockLight(World world, int x, int y, int z, BlockFace face) {
        return world.getBlockLight(x + face.offsetX, y + face.offsetY, z + face.offsetZ) / (float) 0xF;
    }

    private float getBlockSkylight(World world, int x, int y, int z, BlockFace face) {
        return world.getBlockSkylight(x + face.offsetX, y + face.offsetY, z + face.offsetZ) / (float) 0xF;
    }

    public int bake(World world, int x, int y, int z, ByteBuffer buffer) {
        float[] floatsArr = new float[]{
                // Left face
                x, y, z,         -1, 0, 0, 0, 1, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),
                x, y, z + 1,     -1, 0, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),
                x, y + 1, z,     -1, 0, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),
                x, y + 1, z,     -1, 0, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),
                x, y, z + 1,     -1, 0, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),
                x, y + 1, z + 1, -1, 0, 0, 1, 0, getFaceTextureLayerIndex(BlockFace.LEFT), getBlockLight(world, x, y, z, BlockFace.LEFT), getBlockSkylight(world, x, y, z, BlockFace.LEFT),

                // Right face
                x + 1, y, z,         1, 0, 0, 0, 1, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),
                x + 1, y + 1, z,     1, 0, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),
                x + 1, y, z + 1,     1, 0, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),
                x + 1, y + 1, z,     1, 0, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),
                x + 1, y + 1, z + 1, 1, 0, 0, 1, 0, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),
                x + 1, y, z + 1,     1, 0, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.RIGHT), getBlockLight(world, x, y, z, BlockFace.RIGHT), getBlockSkylight(world, x, y, z, BlockFace.RIGHT),

                // Bottom face
                x, y, z,         0, -1, 0, 0, 1, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),
                x + 1, y, z,     0, -1, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),
                x, y, z + 1,     0, -1, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),
                x + 1, y, z,     0, -1, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),
                x + 1, y, z + 1, 0, -1, 0, 1, 0, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),
                x, y, z + 1,     0, -1, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.DOWN), getBlockLight(world, x, y, z, BlockFace.DOWN), getBlockSkylight(world, x, y, z, BlockFace.DOWN),

                // Top face
                x, y + 1, z,         0, 1, 0, 0, 1, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),
                x, y + 1, z + 1,     0, 1, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),
                x + 1, y + 1, z,     0, 1, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),
                x + 1, y + 1, z,     0, 1, 0, 1, 1, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),
                x, y + 1, z + 1,     0, 1, 0, 0, 0, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),
                x + 1, y + 1, z + 1, 0, 1, 0, 1, 0, getFaceTextureLayerIndex(BlockFace.UP), getBlockLight(world, x, y, z, BlockFace.UP), getBlockSkylight(world, x, y, z, BlockFace.UP),

                // Back face
                x, y, z,         0, 0, -1, 0, 1, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),
                x + 1, y + 1, z, 0, 0, -1, 1, 0, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),
                x + 1, y, z,     0, 0, -1, 1, 1, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),
                x, y, z,         0, 0, -1, 0, 1, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),
                x, y + 1, z,     0, 0, -1, 0, 0, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),
                x + 1, y + 1, z, 0, 0, -1, 1, 0, getFaceTextureLayerIndex(BlockFace.BACK), getBlockLight(world, x, y, z, BlockFace.BACK), getBlockSkylight(world, x, y, z, BlockFace.BACK),

                // Front face
                x, y, z + 1,         0, 0, 1, 0, 1, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
                x + 1, y, z + 1,     0, 0, 1, 1, 1, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
                x + 1, y + 1, z + 1, 0, 0, 1, 1, 0, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
                x + 1, y + 1, z + 1, 0, 0, 1, 1, 0, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
                x, y + 1, z + 1,     0, 0, 1, 0, 0, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
                x, y, z + 1,         0, 0, 1, 0, 1, getFaceTextureLayerIndex(BlockFace.FRONT), getBlockLight(world, x, y, z, BlockFace.FRONT), getBlockSkylight(world, x, y, z, BlockFace.FRONT),
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
