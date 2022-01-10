package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.gl.GLUtil;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gl.Shader;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class GBufferProgram {
    public static final int UNIFORM_TRANSFORM = 0;
    public static final int UNIFORM_CAMERA = 1;
    public static final int UNIFORM_BLOCK_TEXTURES = 2;
    public static final int UNIFORM_WORLD_SKYLIGHT = 3;

    @Getter
    private final Program program;

    public GBufferProgram() throws IOException {
        this.program = Program.create();

        Shader vtxShader = Shader.create(GL_VERTEX_SHADER);
        vtxShader.loadSource(new FileInputStream("resources/shaders/gbuffer.vert"));
        vtxShader.compile();
        program.attachShader(vtxShader);

        Shader fragShader = Shader.create(GL_FRAGMENT_SHADER);
        fragShader.loadSource(new FileInputStream("resources/shaders/gbuffer.frag"));
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
