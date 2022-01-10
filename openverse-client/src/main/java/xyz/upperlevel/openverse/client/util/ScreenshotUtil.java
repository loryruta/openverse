package xyz.upperlevel.openverse.client.util;

import org.lwjgl.BufferUtils;
import xyz.upperlevel.openverse.client.window.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public final class ScreenshotUtil {
    private ScreenshotUtil() {
    }

    public static BufferedImage take(int x, int y, int width, int height) {
        glReadBuffer(GL_FRONT);
        final int bitsPerPixel = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bitsPerPixel);
        glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                int i = (ix + (width * iy)) * bitsPerPixel;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(ix, height - (iy + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return image;
    }

    public static BufferedImage take(Window window) {
        return take(0, 0, window.getWidth(), window.getHeight());
    }

    public static void saveToFile(Window window, File file, String format) throws IOException {
        ImageIO.write(take(window), format, file);
    }

    public static void saveToFile(Window window, File file) throws IOException {
        saveToFile(window, file, getExtension(file));
    }

    private static String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : name;
    }
}

