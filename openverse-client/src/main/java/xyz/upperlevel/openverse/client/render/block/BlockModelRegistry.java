package xyz.upperlevel.openverse.client.render.block;

import com.google.gson.Gson;
import xyz.upperlevel.openverse.Openverse;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.util.config.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class BlockModelRegistry {
    public static final Gson GSON = new Gson();
    private static Map<Path, BlockPart> models = new HashMap<>(); // todo at the moment loads only block parts

    private BlockModelRegistry() {
    }

    /**
     * Loads and registers all models for the given folder. If a model links a texture, loads its texture.
     */
    @SuppressWarnings("unchecked")
    public static BlockPart load(File file) {
        if (!file.isDirectory()) {
            Path path = file.toPath();
            BlockPart model;
            try {
                model = BlockPart.deserialize(Config.wrap(GSON.fromJson(new FileReader(path.toFile()), Map.class)));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
            register(path, model);
            return model;
        }
        OpenverseClient.get().getLogger().warning("Given file \"" + file + "\" is a directory!");
        return null;
    }

    public static List<BlockPart> loadFolder(File folder) {
        if (folder.isDirectory()) {
            List<BlockPart> parts = new ArrayList<>();
            File[] files = folder.listFiles();
            if (files != null) {
                for (File sub : files) {
                    if (sub.isFile()) {
                        parts.add(load(sub));
                    }
                    // ignores sub-dirs
                }
            }
            return parts;
        } else {
            OpenverseClient.get().getLogger().warning("Given folder \"" + folder + "\" is a file!");
            return Collections.emptyList();
        }
    }

    /**
     * Registers given model. If the model links a texture, loads its texture.
     */
    public static void register(Path path, BlockPart model) {
        for (File texFile : model.getFaceTextures()) {
            try {
                TextureBakery.get().loadAndStoreTexture(texFile);
            } catch (IOException e) {
                throw new IllegalStateException("Texture loading failed for: " + texFile);
            }
        }
        models.put(path, model);
    }

    public static BlockPart getModel(Path path) {
        return models.get(path);
    }

    public static Collection<BlockPart> getModels() {
        return models.values();
    }
}
