package xyz.upperlevel.openverse.client.resource.texture;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import xyz.upperlevel.openverse.resource.ResourceLoader;
import xyz.upperlevel.openverse.resource.ResourceRegistry;
import xyz.upperlevel.ulge.opengl.texture.Texture2dArray;
import xyz.upperlevel.ulge.opengl.texture.TextureParameter;
import xyz.upperlevel.ulge.opengl.texture.TextureParameters;
import xyz.upperlevel.ulge.opengl.texture.loader.ImageContent;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_RGBA8;

/**
 * The texture manager contains a list of all textures, loaded.
 * It can be used anywhere.
 */
@Getter
public class TextureRegistry extends ResourceRegistry<Texture> {
    public static final TextureLoader LOADER = new TextureLoader();
    private static final TextureParameters PARAMS = new TextureParameters()
            .addParameter(TextureParameter.Type.Wrapping.S, TextureParameter.Value.Wrapping.REPEAT)
            .addParameter(TextureParameter.Type.Wrapping.T, TextureParameter.Value.Wrapping.REPEAT)
            .addParameter(TextureParameter.Type.Filter.MIN, TextureParameter.Value.Filter.NEAREST)
            .addParameter(TextureParameter.Type.Filter.MAG, TextureParameter.Value.Filter.NEAREST);

    private int nextId = 0;
    private final Texture2dArray atlas;

    public TextureRegistry(File folder, Logger logger, int size) {
        super(new File(folder, "textures"), logger);
        this.atlas = new Texture2dArray();
        this.atlas.allocate(4, GL_RGBA8, 16, 16, size);

        atlas.setup(PARAMS);

        register(
                "null",
                new Texture(
                        new ImageContent(
                                1,
                                1,
                                (ByteBuffer) BufferUtils.createByteBuffer(4)
                                        .put(new byte[]{
                                                (byte) 255,
                                                (byte) 255,
                                                (byte) 255,
                                                (byte) 255
                                        })
                                        .flip()
                        )
                )
        );
    }

    @Override
    public void register(String id, Texture texture) {
        super.register(id, texture);
        atlas.load(nextId, texture.getImage());
        texture.setLayer(nextId);
        nextId++;
    }

    @Override
    public boolean unregister(String id) {
        throw new UnsupportedOperationException("Cannot unregister texture!");
    }

    @Override
    protected ResourceLoader<Texture> getDefaultLoader() {
        return LOADER;
    }

    @Override
    protected void onFileLoad(Logger logger, File file) {
        logger.info("Loaded texture at: " + file);
    }

    @Override
    protected void onFolderLoad(Logger logger, int loaded, File folder) {
        logger.info("Loaded " + loaded + " textures at: " + folder);
    }
}
