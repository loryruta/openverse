package xyz.upperlevel.opencraft.server.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import xyz.upperlevel.opencraft.server.shape.Shape;

public class BlockType {

    @Getter
    private String id;

    @Getter
    @Setter
    private boolean solid;

    @Getter
    @Setter
    private boolean transparent;

    @Getter
    @Setter
    private Shape shape;

    public BlockType(@NonNull String id) {
        this.id = id;
    }
}
