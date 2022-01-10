package xyz.upperlevel.openverse.client.resource;

import lombok.Getter;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gl.Shader;
import xyz.upperlevel.openverse.resource.Identifier;
import xyz.upperlevel.openverse.resource.ResourceLoader;
import xyz.upperlevel.openverse.util.config.Config;

import java.io.File;

public class ProgramLoader implements ResourceLoader<Program> {
    @Getter
    private final OpenverseClient client;

    public ProgramLoader(OpenverseClient client) {
        this.client = client;
    }

    private Program load(Config config) {
        Program program = Program.create();
        if (config.has("shaders")) {
            for (String shader : config.getStringList("shaders")) {
                Shader shad = client.getResources().shaders().entry(shader);

                if (shad != null) {
                    program.attachShader(shad);
                    client.getLogger().info("Attached shader: " + shader);
                }
            }
            program.link();
            client.getLogger().info("Program linked.");
        }
        return program;
    }

    @Override
    public Identifier<Program> load(File file) {
        OpenverseClient.get().getLogger().info("Loading program at: " + file.getName());

        String filename = file.getName();
        return new Identifier<>(filename.substring(filename.lastIndexOf(".")), load(Config.json(file)));
    }
}
