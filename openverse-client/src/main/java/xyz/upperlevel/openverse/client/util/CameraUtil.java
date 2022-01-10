package xyz.upperlevel.openverse.client.util;

import org.joml.Matrix4f;

public final class CameraUtil {
    private CameraUtil() {
    }

    public static void createViewMatrix(
            Matrix4f dst,
            float yaw, float pitch,
            float x, float y, float z
    ) {
        dst.identity();
        dst.rotate(pitch, 1, 0, 0);
        dst.rotate(yaw, 0, 1, 0);
        dst.translate(-x, -y, -z);
    }

    public static void createPerspectiveMatrix(
            Matrix4f dst,
            float fov, float aspectRatio,
            float nearPlane, float farPlane
    ) {
        dst.identity();
        dst.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}
