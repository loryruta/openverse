package xyz.upperlevel.openverse.client.resource;

import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.gl.Shader;
import xyz.upperlevel.openverse.resource.Identifier;
import xyz.upperlevel.openverse.resource.ResourceLoader;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class ShaderLoader implements ResourceLoader<Shader> {
    @Override
    public Identifier<Shader> load(File file) {
        String filename = file.toString();
        String ext = filename.substring(filename.lastIndexOf(".") + 1);

        int shaderType;
        switch (ext.toLowerCase()) {
            case "vs":
            case "vert":
                shaderType = GL_VERTEX_SHADER;
                break;
            case "fs":
            case "frag":
                shaderType = GL_FRAGMENT_SHADER;
                break;
            default:
                throw new IllegalArgumentException("Shader extension not recognized for: " + ext);
        }

        Shader shader = Shader.create(shaderType);
        OpenverseClient.get().getLogger().info("Loading shader at: " + file.getName());

        try {
            shader.loadSource(file);
        } catch (IOException e) {
            throw new IllegalStateException("Shader source loading failed: " + file);
        }
        shader.compile();

        return new Identifier<>(file.getName(), shader);
    }
}
