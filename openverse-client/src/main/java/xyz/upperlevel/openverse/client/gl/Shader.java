package xyz.upperlevel.openverse.client.gl;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public static final boolean CHECK_SHADER_COMPILE_STATUS = true;

    @Getter
    private final int name;

    protected Shader(int name) {
        this.name = name;
    }

    public void destroy() {
        glDeleteShader(name);
    }

    public void setSource(String src) {
        glShaderSource(name, src);
    }

    public void loadSource(InputStream in) throws IOException {
        String src = GLUtil.readText(in);
        setSource(src);
    }

    public void loadSource(File file) throws IOException {
        loadSource(new FileInputStream(file));
    }

    public void compile() {
        glCompileShader(name);

        if (CHECK_SHADER_COMPILE_STATUS) {
            int compiled = glGetShaderi(name, GL_COMPILE_STATUS);
            if (compiled == GL_FALSE) {
                String log = glGetShaderInfoLog(name, GL_INFO_LOG_LENGTH);
                throw new IllegalStateException("Shader compilation failed: " + log);
            }
        }
    }

    public static Shader create(int shaderType) {
        return new Shader(glCreateShader(shaderType));
    }

    public static Shader wrap(int shader) {
        return new Shader(shader);
    }
}
