package xyz.upperlevel.openverse.world.entity;

import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.openverse.resource.EntityType;
import xyz.upperlevel.openverse.world.Location;

public interface Player extends Entity {

    EntityType TYPE = new EntityType("player");


    default EntityType getType() {
        return TYPE;
    }

    String getName();

    Location getLocation();

    Connection getConnection();
}
