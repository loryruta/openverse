package xyz.upperlevel.opencraft.renderer;

import lombok.Getter;
import lombok.NonNull;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import world.Block;
import world.Chunk;
import world.World;
import xyz.upperlevel.ulge.util.math.CameraUtil;

import java.util.Objects;

public class WorldViewer {

    @Getter
    @NonNull
    private World world;

    @Getter
    public double x = 0, y = 0, z = 0;

    @Getter
    private float yaw = 0, pitch = 0;

    @Getter
    private float fov = 90f;

    @Getter
    private float nearPlane = 0.01f, farPlane = 100f;

    @Getter
    private float aspectRatio = 1f / 2f;

    @Getter
    private Matrix4f cameraMatrix = new Matrix4f();

    @Getter
    private FrustumIntersection frustum = new FrustumIntersection();

    public WorldViewer(World world) {
        this(world, 0, 0, 0);
    }

    public WorldViewer(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public WorldViewer(World world, double x, double y, double z, float yaw, float pitch) {
        Objects.requireNonNull(world, "World cannot be null.");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public WorldViewer setWorld(World world) {
        Objects.requireNonNull(world, "World cannot be null.");
        this.world = world;
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setX(double x) {
        this.x = x;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setY(double y) {
        this.y = y;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setZ(double z) {
        this.z = z;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setYaw(float yaw) {
        this.yaw = yaw;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setPitch(float pitch) {
        this.pitch = pitch;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setFov(float fov) {
        this.fov = fov;
        calculateCameraMatrix();
        // todo updateFragments buffer
        return this;
    }

    public WorldViewer setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        calculateCameraMatrix();
        return this;
    }

    public WorldViewer setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
        calculateCameraMatrix();
        return this;
    }

    public WorldViewer setFarPlane(float farPlane) {
        this.farPlane = farPlane;
        calculateCameraMatrix();
        return this;
    }

    public void calculateCameraMatrix() {
        Matrix4f proj = CameraUtil.getProjection(fov, aspectRatio, nearPlane, farPlane);
        Matrix4f view = CameraUtil.getView(yaw, pitch, (float) x, (float) y, (float) z);
        cameraMatrix = CameraUtil.getCamera(proj, view);
        frustum.set(cameraMatrix);
    }

    public Block getBlock() {
        return world.getBlock((int) x, (int) y, (int) z);
    }

    public Chunk getChunk() {
        return getBlock().getChunk();
    }
}