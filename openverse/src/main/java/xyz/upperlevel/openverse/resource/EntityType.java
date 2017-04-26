package xyz.upperlevel.openverse.resource;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import xyz.upperlevel.openverse.resource.model.Model;

public class EntityType {

    @Getter
    private String id;

    @Getter
    @Setter
    private Model model;

    public EntityType(@NonNull String id) {
        this.id = id;
    }
}
