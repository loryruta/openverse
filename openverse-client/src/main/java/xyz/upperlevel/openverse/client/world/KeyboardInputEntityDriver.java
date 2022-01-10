package xyz.upperlevel.openverse.client.world;

import lombok.Getter;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.client.window.WindowCursorMoveEvent;
import xyz.upperlevel.openverse.world.entity.input.LivingEntityDriver;

import static org.lwjgl.glfw.GLFW.*;

@Getter
public class KeyboardInputEntityDriver implements LivingEntityDriver, Listener {
    private static final float SPEED = 0.5f;
    private static final float SENSIBILITY = 0.5f;

    private final Window window;

    private float strafe, up, forward, yaw, pitch;

    private double lastCursorX, lastCursorY;
    private float cumulativeYaw, cumulativePitch;

    public KeyboardInputEntityDriver() {
        this.window = Launcher.get().getWindow();
        window.getEventManager().register(this);
    }

    @Override
    public void onTick() {
        if (!OpenverseClient.get().isCaptureInput()) {
            strafe = 0;
            up = 0;
            forward = 0;
            yaw = 0;
            pitch = 0;
            return;
        }
        strafe  = getMovement(GLFW_KEY_D,     GLFW_KEY_A,          SPEED);
        up      = getMovement(GLFW_KEY_SPACE, GLFW_KEY_LEFT_SHIFT, SPEED);
        forward = getMovement(GLFW_KEY_W,     GLFW_KEY_S,          SPEED);

        yaw   = cumulativeYaw;
        pitch = cumulativePitch;

        cumulativeYaw = 0f;
        cumulativePitch = 0f;
    }

    protected float getMovement(int posKey, int negKey, float speed) {
        boolean enPos = window.getKey(posKey) == GLFW_PRESS || window.getKey(posKey) == GLFW_REPEAT;
        boolean enNeg = window.getKey(negKey) == GLFW_PRESS || window.getKey(negKey) == GLFW_REPEAT;

        if (enPos == enNeg) {
            // All enabled or all disabled
            return 0;
        } else {
            return enPos ? speed : -speed;
        }
    }

    @EventHandler
    public void onCursorPos(WindowCursorMoveEvent e) {
        if (OpenverseClient.get().isCaptureInput()) {
            cumulativeYaw += (float) (e.getXPos() - lastCursorX) * SENSIBILITY;
            cumulativePitch += (float) (e.getYPos() - lastCursorY) * SENSIBILITY;
        }

        lastCursorX = e.getXPos();
        lastCursorY = e.getYPos();
    }
}
