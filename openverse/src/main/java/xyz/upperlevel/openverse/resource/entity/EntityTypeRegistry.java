package xyz.upperlevel.openverse.resource.entity;

import xyz.upperlevel.openverse.resource.ResourceLoader;
import xyz.upperlevel.openverse.resource.ResourceRegistry;

import java.io.File;
import java.util.logging.Logger;

public class EntityTypeRegistry extends ResourceRegistry<EntityType> {
    public static final File FOLDER = new File("resources/entities");
    public static final EntityTypeLoader LOADER = new EntityTypeLoader();

    public EntityTypeRegistry(Logger logger) {
        super(FOLDER, logger);
    }

    @Override
    protected ResourceLoader<EntityType> getDefaultLoader() {
        return LOADER;
    }

    @Override
    protected void onFileLoad(Logger logger, File file) {
        logger.info("Loaded entity type at: " + file);
    }

    @Override
    protected void onFolderLoad(Logger logger, int loaded, File folder) {
        logger.info("Loaded " + loaded + " entities in: " + folder);
    }
}