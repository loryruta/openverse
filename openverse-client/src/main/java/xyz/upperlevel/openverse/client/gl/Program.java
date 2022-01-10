package xyz.upperlevel.openverse.client.gl;


import lombok.Getter;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Program {
    public static final boolean CHECK_PROGRAM_ATTRIB_LOCATION  = true;
    public static final boolean CHECK_PROGRAM_UNIFORM_LOCATION = true;
    public static final boolean CHECK_PROGRAM_LINK_STATUS      = true;

    @Getter
    private final int name;

    protected Program(int name) {
        this.name = name;
    }

    public void destroy() {
        glDeleteProgram(name);
    }

    public void attachShader(int shader) {
        glAttachShader(name, shader);
    }

    public void attachShader(Shader shader) {
        attachShader(shader.getName());
    }

    public void link() {
        glLinkProgram(name);

        if (CHECK_PROGRAM_LINK_STATUS) {
            int linked = glGetProgrami(name, GL_LINK_STATUS);
            if (linked == GL_FALSE) {
                String log = glGetProgramInfoLog(name, GL_INFO_LOG_LENGTH);
                throw new IllegalStateException("Program linking failed: " + log);
            }
        }
    }

    public void use() {
        glUseProgram(name);
    }

    public static void unuse() {
        glUseProgram(0);
    }

    public int getAttribLocation(String attribName) {
        int loc = glGetAttribLocation(name, attribName);
        if (CHECK_PROGRAM_ATTRIB_LOCATION && loc < 0) {
            throw new IllegalArgumentException("Invalid attrib name: " + attribName);
        }
        return loc;
    }

    public int getUniformLocation(String uniformName) {
        int loc = glGetUniformLocation(name, uniformName);
        if (CHECK_PROGRAM_UNIFORM_LOCATION && loc < 0) {
            throw new IllegalArgumentException("Invalid uniform name: %s" + uniformName);
        }
        return loc;
    }

    public static Program create() {
        return new Program(glCreateProgram());
    }

    public static Program wrap(int program) {
        return new Program(program);
    }
}

