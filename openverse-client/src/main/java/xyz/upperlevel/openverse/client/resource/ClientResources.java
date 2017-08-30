package xyz.upperlevel.openverse.client.resource;

import xyz.upperlevel.openverse.client.resource.model.ClientModelRegistry;
import xyz.upperlevel.openverse.client.resource.model.shape.ClientShapeTypeRegistry;
import xyz.upperlevel.openverse.client.resource.program.ProgramRegistry;
import xyz.upperlevel.openverse.client.resource.shader.ShaderRegistry;
import xyz.upperlevel.openverse.client.resource.texture.TextureRegistry;
import xyz.upperlevel.openverse.resource.Resources;

import java.io.File;
import java.util.logging.Logger;

/**
 * The {@link ClientResources} is a manager for all client resources.
 * Differently from the {@link Resources} it has a {@link TextureRegistry}, {@link ShaderRegistry} and a {@link ProgramRegistry}
 * classes that are needed only in client-side (and other different implementations).
 */
public class ClientResources extends Resources {
    private final TextureRegistry textureRegistry;
    private final ShaderRegistry shaderRegistry;
    private final ProgramRegistry programRegistry;
    // overrides
    private final ClientShapeTypeRegistry shapeFactoryRegistry;
    private final ClientModelRegistry modelRegistry;

    /**
     * The constructor of {@link ClientResources} initializes all sub resource managers.
     */
    public ClientResources(Logger logger) {
        super(new File("client/resources"), logger);
        this.textureRegistry = new TextureRegistry(folder, logger, 100 /*todo find texture size in some way*/);
        this.shaderRegistry = new ShaderRegistry(folder, logger);
        this.programRegistry = new ProgramRegistry(folder, logger);
        // overrides
        this.shapeFactoryRegistry = new ClientShapeTypeRegistry();
        this.modelRegistry = new ClientModelRegistry(folder, logger);
    }

    /**
     * Returns the {@link TextureRegistry} object.
     */
    public TextureRegistry textures() {
        return textureRegistry;
    }

    /**
     * Returns the {@link ShaderRegistry} object.
     */
    public ShaderRegistry shaders() {
        return shaderRegistry;
    }

    /**
     * Returns the {@link ProgramRegistry} object.
     */
    public ProgramRegistry programs() {
        return programRegistry;
    }

    @Override
    public ClientShapeTypeRegistry shapes() {
        return shapeFactoryRegistry;
    }

    @Override
    public ClientModelRegistry models() {
        return modelRegistry;
    }

    @Override
    protected void onSetup() {
        textureRegistry.setup();
        shaderRegistry.setup();
        programRegistry.setup();
    }

    @Override
    protected int onLoad() {
        int cnt = 0;
        cnt += textureRegistry.loadFolder();
        cnt += shaderRegistry.loadFolder();
        cnt += programRegistry.loadFolder();
        return cnt;
    }

    @Override
    protected void onUnload() {
        textureRegistry.unload();
        shaderRegistry.unload();
        programRegistry.unload();
    }
}
