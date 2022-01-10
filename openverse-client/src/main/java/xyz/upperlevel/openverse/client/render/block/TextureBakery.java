package xyz.upperlevel.openverse.client.render.block;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.gl.GLUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.opengl.GL43.glCopyImageSubData;
import static org.lwjgl.stb.STBImage.*;

public class TextureBakery {
    public static final int TEXTURE_ALLOC_BLOCK_SIZE = 128; // in number of layers
    public static final int MIPMAP_LEVELS = 4;
    public static final int MAX_TEXTURE_WIDTH  = 16; // todo what if a user wants a different tex size?
    public static final int MAX_TEXTURE_HEIGHT = 16;
    public static final int MAX_TEXTURE_SIZE   = MAX_TEXTURE_WIDTH * MAX_TEXTURE_HEIGHT;

    private static TextureBakery instance;

    private final Map<String, Integer> layerByTexName = new HashMap<>();

    @Getter
    private int textureArray = -1;

    private int allocTexCount = 0;
    private int texCount = 0;

    public TextureBakery() {
        storeTexture("null", createNullTexture());
    }

    public void destroy() {
        if (textureArray >= 0) {
            glDeleteTextures(textureArray);
        }
    }

    protected ByteBuffer createNullTexture() {
        ByteBuffer byteBuf = BufferUtils.createByteBuffer(MAX_TEXTURE_SIZE * 4);
        for (int i = 0; i < MAX_TEXTURE_SIZE; i++) {
            byteBuf.put((byte) 255);
            byteBuf.put((byte) 255);
            byteBuf.put((byte) 255);
            byteBuf.put((byte) 255);
        }
        byteBuf.flip();
        return byteBuf;
    }

    public void storeTexture(String texName, ByteBuffer texData) {
        if (layerByTexName.containsKey(texName)) {
            return;
        }

        if (texCount == allocTexCount) { // Realloc (increase capacity by TEXTURE_ALLOC_BLOCK_SIZE)
            int newTexArr = glGenTextures();
            glBindTexture(GL_TEXTURE_2D_ARRAY, newTexArr);
            glTexStorage3D(GL_TEXTURE_2D_ARRAY, MIPMAP_LEVELS, GL_RGBA8, MAX_TEXTURE_WIDTH, MAX_TEXTURE_HEIGHT, allocTexCount + TEXTURE_ALLOC_BLOCK_SIZE);

            if (textureArray >= 0) {
                for (int mipmapLevel = 0; mipmapLevel < MIPMAP_LEVELS; mipmapLevel++) {
                    glCopyImageSubData(
                            textureArray,
                            GL_TEXTURE_2D_ARRAY,
                            mipmapLevel,
                            0, 0, 0,
                            newTexArr,
                            GL_TEXTURE_2D_ARRAY,
                            mipmapLevel,
                            0, 0, 0,
                            MAX_TEXTURE_WIDTH, MAX_TEXTURE_HEIGHT, allocTexCount
                    );
                }

                glDeleteTextures(textureArray);
            }

            textureArray = newTexArr;

            allocTexCount += TEXTURE_ALLOC_BLOCK_SIZE;
        }

        glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray);
        glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY,
                0,
                0, 0, texCount,
                MAX_TEXTURE_WIDTH, MAX_TEXTURE_HEIGHT, 1,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                texData
        );

        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        layerByTexName.put(texName, texCount);

        OpenverseClient.logger().fine("[TextureBakery] Registered texture: " + texName);

        texCount++;
    }

    public void loadAndStoreTexture(String texName, ByteBuffer formattedTexData) {
        if (layerByTexName.containsKey(texName)) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuf  = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer compBuf   = stack.mallocInt(1);

            if (!stbi_info_from_memory(formattedTexData, widthBuf, heightBuf, compBuf)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }

            Logger logger = OpenverseClient.logger();
            logger.info("Texture: " + texName + " - Width: " + widthBuf.get(0) + " - Height: " + heightBuf.get(0) + " - Components: " + compBuf.get(0));

            ByteBuffer texBuf = stbi_load_from_memory(formattedTexData, widthBuf, heightBuf, compBuf, STBI_rgb_alpha);
            if (texBuf == null) {
                throw new RuntimeException("STB image loading failed: " + stbi_failure_reason());
            }

            storeTexture(texName, texBuf);

            stbi_image_free(texBuf);
        }
    }

    public void loadAndStoreTexture(File file) throws IOException {
        loadAndStoreTexture(file.getPath(), GLUtil.read(file));
    }

    public int getLayer(String texName) {
        return layerByTexName.get(texName);
    }

    public int getLayer(File file) {
        return getLayer(file.getPath());
    }

    public static TextureBakery get() {
        if (instance == null) {
            instance = new TextureBakery();
        }
        return instance;
    }
}
