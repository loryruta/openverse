package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.util.GLUtil;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class SSAOProgram {
    public static final int FRAG_GBUFFER_POSITION_UNIFORM = 0;
    public static final int FRAG_GBUFFER_NORMAL_UNIFORM   = 1;
    public static final int FRAG_SAMPLES_UNIFORM          = 2;
    public static final int FRAG_CAMERA_UNIFORM           = 3;

    public static final int KERNEL_SIZE = 64;

    @Getter
    private final int programName;

    public SSAOProgram() throws IOException {
        programName = glCreateProgram();

        String log;

        int vtxShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vtxShader, GLUtil.readText(new FileInputStream("resources/shaders/screen_quad.vert")));
        glCompileShader(vtxShader);
        if ((log = GLUtil.getShaderCompilationInfoLog(vtxShader)) != null) {
            throw new IllegalStateException(log);
        }
        glAttachShader(programName, vtxShader);

        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, GLUtil.readText(new FileInputStream("resources/shaders/ssao.frag")));
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
