package xyz.upperlevel.openverse.client;

import lombok.Getter;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import xyz.upperlevel.openverse.client.launcher.SingleplayerScene;
import xyz.upperlevel.ulge.game.Game;
import xyz.upperlevel.ulge.game.GameSettings;

import java.io.*;

public class Launcher {
    public static Launcher instance;

    @Getter
    private final Game game;

    private final Callback debugProc;

    protected Launcher() {
        Launcher.instance = this;

        this.game = new Game(new GameSettings()
                .width(1024)
                .height(720)
                .title("Openverse")
                .fullscreen(false)
                .createWindow()
        );

        this.debugProc = GLUtil.setupDebugMessageCallback();
    }

    public void destroy() {
        this.debugProc.free();

        // todo destroy ALL
    }

    public void launch() {
        game.getStage().setScene(new SingleplayerScene());
        game.start();
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
