package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.gl.GLUtil;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gl.Shader;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class ApplyLightsProgram {
    public static final int UNIFORM_GBUFFER_POSITION = 0;
    public static final int UNIFORM_GBUFFER_NORMAL = 1;
    public static final int UNIFORM_GBUFFER_ALBEDO = 2;
    public static final int UNIFORM_GBUFFER_BLOCK_LIGHT = 3;
    public static final int UNIFORM_GBUFFER_BLOCK_SKYLIGHT = 4;
    public static final int UNIFORM_GBUFFER_SSAO = 5;

    @Getter
    private final Program program;

    public ApplyLightsProgram() throws IOException {
        this.program = Program.create();

        Shader vtxShader = Shader.create(GL_VERTEX_SHADER);
        vtxShader.loadSource(new FileInputStream("resources/shaders/screen_quad.vert"));
        vtxShader.compile();
        program.attachShader(vtxShader);

        Shader fragShader = Shader.create(GL_FRAGMENT_SHADER);
        fragShader.loadSource(new FileInputStream("resources/shaders/apply_lights.frag"));
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
