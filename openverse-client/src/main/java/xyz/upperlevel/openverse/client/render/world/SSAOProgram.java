package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.gl.GLUtil;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gl.Shader;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class SSAOProgram {
    public static final int UNIFORM_GBUFFER_POSITION = 0;
    public static final int UNIFORM_GBUFFER_NORMAL = 1;
    public static final int UNIFORM_VIEW_MATRIX = 2;
    public static final int UNIFORM_PROJ_MATRIX = 3;
    public static final int UNIFORM_NOISE_TEXTURE = 4;
    public static final int UNIFORM_SCREEN_DIM = 5;

    public static final int BUFFER_BINDING_KERNEL_SAMPLES = 0;

    public static final int KERNEL_SIZE = 256;

    @Getter
    private final Program program;

    public SSAOProgram() throws IOException {
        this.program = Program.create();

        Shader vtxShader = Shader.create(GL_VERTEX_SHADER);
        vtxShader.loadSource(new FileInputStream("resources/shaders/screen_quad.vert"));
        vtxShader.compile();
        program.attachShader(vtxShader);

        Shader fragShader = Shader.create(GL_FRAGMENT_SHADER);
        fragShader.loadSource(new FileInputStream("resources/shaders/ssao.frag"));
        fragShader.compile();
        program.attachShader(fragShader);

        program.link();

        vtxShader.destroy();
        fragShader.destroy();
    }

    public void destroy() {
        program.destroy();
    }
}
