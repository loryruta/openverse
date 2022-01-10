package xyz.upperlevel.openverse.client.game;

import lombok.Getter;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.client.window.WindowKeyChangeEvent;
import xyz.upperlevel.openverse.event.ShutdownEvent;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class GameScene extends Stage implements Listener {
    @Getter
    private final OpenverseClient client;

    @Getter
    private final ClientScene parent;

    @Getter
    private final Window window;

    public GameScene(OpenverseClient client, ClientScene parent) {
        this.client = client;

        this.parent = parent;

        this.window = Launcher.get().getWindow();
        window.getEventManager().register(this);
    }

    @Override
    public void onEnable(Scene previous) {
        client.getLogger().info("Listening for world packets...");

        setScene(new ReceivingWorldScene(client, this));
    }

    @Override
    public void onRender() {
        getCurrentScene().onRender();
    }

    @Override
    public void onDisable(Scene next) {
        getCurrentScene().onDisable(next);
        client.getEventManager().call(new ShutdownEvent());
        System.exit(0);//Damn it jline, really?
    }

    @Override
    public void onTick() {
        OpenverseClient.get().onTick();
        getCurrentScene().onTick();
    }

    @Override
    public void onFps() {
        getCurrentScene().onFps();
    }

    @EventHandler
    public void onWindowKey(WindowKeyChangeEvent e) {
        if (!OpenverseClient.get().isCaptureInput()) return;
        if (e.getAction() == GLFW_PRESS) {
            switch (e.getKey()) {
                case GLFW_KEY_ESCAPE:
                    Launcher.get().halt();
                    break;
            }
        }
    }
}
