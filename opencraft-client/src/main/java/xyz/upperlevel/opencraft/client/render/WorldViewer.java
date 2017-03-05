package xyz.upperlevel.opencraft.client.render;

import lombok.Getter;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import xyz.upperlevel.opencraft.server.network.SingleplayerClient;
import xyz.upperlevel.opencraft.server.network.packet.PlayerTeleportPacket;
import xyz.upperlevel.ulge.opengl.shader.Uniformer;
import xyz.upperlevel.ulge.util.math.AngleUtil;
import xyz.upperlevel.ulge.util.math.CameraUtil;

public class WorldViewer {

    @Getter
    private float x, y, z;

    @Getter
    private float yaw, pitch;

    public WorldViewer() {
    }

    @Getter
    private Matrix4f camera = new Matrix4f();

    @Getter
    private RenderArea renderarea = new RenderArea(this);

    private void buildCamera() {
        camera = CameraUtil.getCamera(
                45f,
                1f,
                0.01f,
                100f,
                (float) Math.toRadians(yaw),
                (float) Math.toRadians(pitch),
                x * 2f - 1f,
                y * 2f - 1f,
                z * 2f + 1f
        );
        frustum.set(camera);
    }

    private FrustumIntersection frustum = new FrustumIntersection();

    public boolean isInFrustum(float x, float y, float z) {
        return frustum.testPoint(x, y, z);
    }

    public boolean isInFrustum(float x1, float y1, float z1, float x2, float y2, float z2) {
        return frustum.testAab(x1, y1, z1, x2, y2, z2);
    }

    public Matrix4f getOrientation() {
        Matrix4f result = new Matrix4f();
        result.rotate((float) Math.toRadians(pitch), new Vector3f(1f, 0, 0));
        result.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1f, 0));
        return result;
    }

    public Vector3f getForward() {
        return getOrientation().invert(new Matrix4f()).transformDirection(new Vector3f(0f, 0f, -1f));
    }

    public Vector3f getRight() {
        return getOrientation().invert(new Matrix4f()).transformDirection(new Vector3f(1f, 0, 0));
    }

    public Vector3f getUp() {
        return getOrientation().invert(new Matrix4f()).transformDirection(new Vector3f(0, 1f, 0));
    }

    public WorldViewer move(Vector3f position) {
        move(position.x, position.y, position.z);
        return this;
    }

    public WorldViewer setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        update();
        return this;
    }

    public WorldViewer setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        update();
        return this;
    }

    public WorldViewer move(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        update();
        // todo update frustum
        // todo update render-area
        return this;
    }

    public WorldViewer forward(float sensitivity) {
        move(getForward().mul(sensitivity));
        return this;
    }

    public WorldViewer backward(float sensitivity) {
        move(getForward().mul(-sensitivity));
        return this;
    }

    public WorldViewer left(float sensitivity) {
        move(getRight().mul(-sensitivity));
        return this;
    }

    public WorldViewer right(float sensitivity) {
        move(getRight().mul(sensitivity));
        return this;
    }

    public WorldViewer up(float sensitivity) {
        move(getUp().mul(sensitivity));
        return this;
    }

    public WorldViewer down(float sensitivity) {
        move(getUp().mul(-sensitivity));
        return this;
    }

    public WorldViewer rotate(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;

        if (this.pitch > 90)
            this.pitch = 90;
        else if (this.pitch < -90)
            this.pitch = -90;

        AngleUtil.normalizeDegreesAngle(this.yaw);
        AngleUtil.normalizeDegreesAngle(this.pitch);

        updatePosition();
        // todo update frustum
        buildCamera();
        return this;
    }

    @Getter
    private int chunkX, chunkY, chunkZ; // last chunk coordinates

    public void attemptBuild() { // updates position and send changes
        float rx = x / 16f;
        float ry = y / 16f;
        float rz = z / 16f;

        int acx = (int) Math.floor(rx);
        int acy = (int) Math.floor(ry);
        int acz = (int) Math.ceil(rz);

        if (chunkX != acx || chunkY != acy || chunkZ != acz)
            renderarea.setCenter(acx, acy, acz);

        chunkX = acx;
        chunkY = acy;
        chunkZ = acz;
    }

    public void updatePosition() {
        SingleplayerClient.connection().sendPacket(new PlayerTeleportPacket(x, y, z, yaw, pitch));
    }

    public void update() {
        buildCamera();
        attemptBuild();
        updatePosition();
    }

    public WorldViewer teleport(float x, float y, float z, float yaw, float pitch, boolean send) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        update();
        return this;
    }

    public void draw(Uniformer uniformer) {
        uniformer.setUniformMatrix4("cam", camera.get(BufferUtils.createFloatBuffer(16)));
        renderarea.draw(uniformer);
    }
}