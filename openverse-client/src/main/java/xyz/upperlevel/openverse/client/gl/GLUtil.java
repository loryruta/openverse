package xyz.upperlevel.openverse.client.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.Channels;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class GLUtil {
    private static int emptyVao = -1;
    private static int nullTexture2d = -1;

    private GLUtil() {
    }

    public static int getEmptyVao() {
        if (emptyVao < 0) {
            emptyVao = glGenVertexArrays();
        }
        return emptyVao;
    }

    public static int getNullTexture2d() {
        if (nullTexture2d < 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                nullTexture2d = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, nullTexture2d);

                FloatBuffer imgBuf = stack.floats(1, 1, 1, 1);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_FLOAT, imgBuf);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            }
        }
        return nullTexture2d;
    }

    public static String readText(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);

        StringBuilder src = new StringBuilder();
        int c;
        while ((c = reader.read()) >= 0) {
            src.append((char) c);
        }
        return src.toString();
    }

    public static ByteBuffer resizeBuffer(ByteBuffer buf, int newCapacity) {
        ByteBuffer newBuf = BufferUtils.createByteBuffer(newCapacity);
        buf.flip();
        newBuf.put(buf);
        return newBuf;
    }

    public static ByteBuffer read(File file) throws IOException {
        long fileSize = file.length();
        ByteBuffer buf = BufferUtils.createByteBuffer((int) fileSize);
        InputStream in = new FileInputStream(file);
        int c;
        while ((c = in.read()) >= 0) {
            buf.put((byte) c);
        }

        buf.flip();

        return MemoryUtil.memSlice(buf);
    }
}
