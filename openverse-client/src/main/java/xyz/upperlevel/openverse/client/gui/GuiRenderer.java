package xyz.upperlevel.openverse.client.gui;

import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gl.Shader;
import xyz.upperlevel.openverse.client.util.Color;
import xyz.upperlevel.openverse.client.window.Window;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL44.glBufferStorage;

/**
 * Class used to have some (really basic) Gui rendering
 */
public class GuiRenderer {
    public static final int UNIFORM_BOUNDS_LOCATION  = 0;
    public static final int UNIFORM_DEPTH_LOCATION   = 1;
    public static final int UNIFORM_TEXTURE_LOCATION = 4;
    public static final int UNIFORM_COLOR_LOCATION   = 5;

    private static GuiRenderer instance;

    @Getter
    private Program program;

    @Getter
    private int vertexLayout, vertexBuffer; // Screen quad

    private void initProgram() throws IOException {
        this.program = Program.create();

        Shader vtxShader = Shader.create(GL_VERTEX_SHADER);
        vtxShader.loadSource(new FileInputStream("resources/shaders/gui_basic_shader.vert"));
        vtxShader.compile();
        program.attachShader(vtxShader);

        Shader fragShader = Shader.create(GL_FRAGMENT_SHADER);
        fragShader.loadSource(new FileInputStream("resources/shaders/gui_basic_shader.frag"));
        fragShader.compile();
        program.attachShader(fragShader);

        program.link();

        vtxShader.destroy();
        fragShader.destroy();
    }

    private void initScreenQuad() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // VAO
            vertexLayout = glGenVertexArrays();
            glBindVertexArray(vertexLayout);

            // VBO
            FloatBuffer vboData = stack.floats(
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            );
            vertexBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            glBufferStorage(GL_ARRAY_BUFFER, vboData, 0);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        }
    }

    public GuiRenderer() {
        try {
            initProgram();
        } catch (IOException e) {
            throw new IllegalStateException("Shader program creation failed", e);
        }

        initScreenQuad();
    }

    /**
     * Changes the {@link Color} that will be used in the next {@link #render(Window, GuiBounds)} call
     *
     * @param color the color that will be used
     */
    public void setColor(Color color) {
        program.use();
        glUniform4f(UNIFORM_COLOR_LOCATION, color.r, color.g, color.b, color.a);
    }

    /**
     * Changes the depth that the next {@link #render(Window, GuiBounds)} will use (default: 0)
     *
     * @param depth the depth that will be used
     */
    public void setDepth(float depth) {
        program.use();
        glUniform1f(UNIFORM_DEPTH_LOCATION, depth);
    }

    /**
     * Changes the texture that will be used in the next {@link #render(Window, GuiBounds)} call
     *
     * @param texture the texture that will be used
     */
    public void setTexture(int texture) {
        program.use();

        glUniform1i(UNIFORM_TEXTURE_LOCATION, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    /**
     * Renders the Gui in the bounds using the parameters set using {@link #setColor(Color)}, {@link #setDepth(float)} and {@link #setTexture(int)}
     * <br>Warning: the uniforms should be always set or another call might overwrite them
     *
     * @param bounds the bounds that will be used to render
     */
    public void render(Window window, GuiBounds bounds) {
        program.use();

        // Two operations are being made:
        // - Converting the min-max system to the xywh system
        // - Inverting the y axis
        float invWidth = 1f / window.getWidth();
        float invHeight = 1f / window.getHeight();

        glUniform4f(
                UNIFORM_BOUNDS_LOCATION,
                (float) bounds.minX * invWidth,
                1.0f - (float) bounds.minY * invHeight,     // Invert y
                (float) (bounds.maxX - bounds.minX) * invWidth, // Convert maxX to width
                (float) (bounds.minY - bounds.maxY) * invHeight // Convert maxY to height & Invert y: 1 - (max - min) = (min - max)
        );

        glBindVertexArray(vertexLayout);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    /**
     * Returns the GuiRenderer instance used by all the guis
     *
     * @return the common instance
     */
    public static GuiRenderer get() {
        if (instance == null) {
            return (instance = new GuiRenderer());
        }
        return instance;
    }

    /**
     * Overwrites the GuiRenderer instance used by all the guis
     *
     * @param renderer the new common GuiRenderer instance
    public static void set(GuiRenderer renderer) {
        instance = renderer;
    }
     */
}
