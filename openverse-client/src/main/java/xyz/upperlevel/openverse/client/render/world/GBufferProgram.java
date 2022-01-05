package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.util.GLUtil;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class GBufferProgram {
    public static final int UNIFORM_TRANSFORM = 0;
    public static final int UNIFORM_CAMERA = 1;
    public static final int UNIFORM_BLOCK_TEXTURES = 2;
    public static final int UNIFORM_WORLD_SKYLIGHT = 3;

    @Getter
    private final int programName;

    public GBufferProgram() throws IOException {
        programName = glCreateProgram();

        String log;

        int vtxShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vtxShader, GLUtil.readText(new FileInputStream("resources/shaders/gbuffer.vert")));
        glCompileShader(vtxShader);
        if ((log = GLUtil.getShaderCompilationInfoLog(vtxShader)) != null) {
            throw new IllegalStateException(log);
        }
        glAttachShader(programName, vtxShader);

        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, GLUtil.readText(new FileInputStream("resources/shaders/gbuffer.frag")));
        glCompileShader(fragShader);
        if ((log = GLUtil.getShaderCompilationInfoLog(fragShader)) != null) {
            throw new IllegalStateException(log);
        }
        glAttachShader(programName, fragShader);

        glLinkProgram(programName);
        if ((log = GLUtil.getProgramLinkingInfoLog(programName)) != null) {
            throw new IllegalStateException(log);
        }

        glDeleteShader(vtxShader);
        glDeleteShader(fragShader);
    }

    public void destroy() {
        glDeleteProgram(programName);
    }


}
