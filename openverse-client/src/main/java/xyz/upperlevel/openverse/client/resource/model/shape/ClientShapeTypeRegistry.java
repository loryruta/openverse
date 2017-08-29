package xyz.upperlevel.openverse.client.resource.model.shape;

import xyz.upperlevel.openverse.resource.model.shape.ShapeTypeRegistry;

/**
 * This class registries only {@link ClientShapeType} that every one generate {@link ClientShape} objects.
 */
public class ClientShapeTypeRegistry extends ShapeTypeRegistry<ClientShapeType> {
    public ClientShapeTypeRegistry() {
        // registers default shape types
        // todo URGENT, FIX THIS! register(new Identifier<>("cube", config -> new TexturedCube(config, ...)));
        // todo register(new Identifier<>("sphere", ClientSphere::new));
    }
}