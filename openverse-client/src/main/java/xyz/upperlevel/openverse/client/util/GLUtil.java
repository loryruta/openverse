package xyz.upperlevel.openverse.client.util;

import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class GLUtil {
    public static int emptyVao = -1;

    private GLUtil() {
    }

    public static int getEmptyVao() {
        if (emptyVao < 0) {
            emptyVao = glGenVertexArrays();
        }
        return emptyVao;
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

    public static String getShaderCompilationInfoLog(int shader) {
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (compiled == GL_FALSE) {
            return glGetShaderInfoLog(shader, GL_INFO_LOG_LENGTH);
        } else {
            return null;
        }
    }

    public static String getProgramLinkingInfoLog(int program) {
        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if (linked == GL_FALSE) {
            return glGetProgramInfoLog(program, GL_INFO_LOG_LENGTH);
        } else {
            return null;
        }
    }
}
