package xyz.upperlevel.opencraft.client.controller;

public interface ControllableEntity {

    void setX(double x);
    
    void setY(double y);
    
    void setZ(double z);

    void setYaw(float yaw);

    void setPitch(float pitch);

    void teleport(float yaw, float pitch);

    void teleport(double x, double y, double z);

    void teleport(double x, double y, double z, float yaw, float pitch);
}