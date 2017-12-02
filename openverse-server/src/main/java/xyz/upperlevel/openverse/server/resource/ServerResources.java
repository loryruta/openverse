package xyz.upperlevel.openverse.server.resource;

import xyz.upperlevel.openverse.resource.Resources;
import xyz.upperlevel.openverse.world.block.BlockTypeRegistry;

import java.io.File;
import java.util.logging.Logger;

public class ServerResources extends Resources {
    public ServerResources(File folder, Logger logger) {
        super(folder, logger);
    }

    @Override
    public ServerBlockTypeRegistry createBlockTypeRegistry() {
        return new ServerBlockTypeRegistry();
    }

    @Override
    public ServerItemTypeRegistry createItemTypeRegistry(BlockTypeRegistry blockTypes) {
        return new ServerItemTypeRegistry(blockTypes);
    }

    @Override
    public ServerBlockTypeRegistry blockTypes() {
        return (ServerBlockTypeRegistry) super.blockTypes();
    }

    @Override
    public ServerItemTypeRegistry itemTypes() {
        return (ServerItemTypeRegistry) super.itemTypes();
    }
}
