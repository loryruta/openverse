package xyz.upperlevel.openverse.client.resource;


import xyz.upperlevel.openverse.client.gl.Shader;
import xyz.upperlevel.openverse.resource.ResourceLoader;
import xyz.upperlevel.openverse.resource.ResourceRegistry;

import java.io.File;
import java.util.logging.Logger;

/**
 * The shader manager contains all shaders and loads them.
 * It may be used after have set a context for OpenGL.
 */
public class ShaderRegistry extends ResourceRegistry<Shader> {
    public static final ShaderLoader LOADER = new ShaderLoader();

    public ShaderRegistry(File folder, Logger logger) {
        super(new File(folder, "shaders"), logger);
    }

    @Override
    protected void onUnload(Shader entry) {
        entry.destroy();
    }

    @Override
    protected ResourceLoader<Shader> getDefaultLoader() {
        return LOADER;
    }

    @Override
    protected void onFileLoad(Logger logger, File file) {
        logger.info("Loaded shader for file: " + file);
    }

    @Override
    protected void onFolderLoad(Logger logger, int loaded, File folder) {
        logger.info("Loaded " + loaded + " shaders for folder: " + folder);
    }
}
