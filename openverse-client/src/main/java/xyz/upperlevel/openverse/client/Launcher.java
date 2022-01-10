package xyz.upperlevel.openverse.client;

import lombok.Getter;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import xyz.upperlevel.openverse.client.game.Stage;
import xyz.upperlevel.openverse.client.launcher.SingleplayerScene;
import xyz.upperlevel.openverse.client.window.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Launcher {
    public static final int WINDOW_WIDTH    = 1024;
    public static final int WINDOW_HEIGHT   = 720;
    public static final String WINDOW_TITLE = "openverse";

    public static final long TICK_EACH = 50L; // ms

    public static Launcher instance;

    @Getter
    private Window window;

    @Getter
    private Stage stage;

    private Callback debugProc = null;

    @Getter
    private int fps;

    protected Launcher() {
        Launcher.instance = this;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Couldn't initialize GLFW");
        }

        initWindow();
        initGL();

        this.stage = new Stage();
    }

    public void destroy() {
        if (debugProc != null) {
            debugProc.free();
        }

        GL.destroy();

        window.destroy();

        glfwTerminate();
    }

    private void initWindow() {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_NO_ERROR, GLFW_FALSE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        long windowId = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, 0, 0);
        if (windowId == 0) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        this.window = new Window(windowId);

        glfwMakeContextCurrent(windowId);

        glfwShowWindow(windowId);
    }

    private void initGL() {
        GL.createCapabilities();

        //this.debugProc = GLUtil.setupDebugMessageCallback();
    }

    public void launch() {
        stage.setScene(new SingleplayerScene());

        long lastTickTime = 0;
        long lastFpsTime = 0;

        while (!window.shouldClose()) {
            glfwPollEvents();

            long now;

            if (((now = System.currentTimeMillis()) - lastTickTime) >= TICK_EACH) {
                stage.onTick();
                lastTickTime = now;
            }

            if (((now = System.currentTimeMillis()) - lastFpsTime) >= 1000) {
                stage.onFps();
                fps = 0;
                lastFpsTime = now;
            }

            stage.onRender();

            fps++;

            glfwSwapBuffers(window.getWindowId());
            glfwSwapInterval(1);
        }
    }

    public void halt() {
        window.setShouldClose(true);
    }

    public static Launcher get() {
        return instance;
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();

        launcher.launch();

        launcher.destroy();
    }
}
