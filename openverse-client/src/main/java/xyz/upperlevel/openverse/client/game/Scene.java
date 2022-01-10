package xyz.upperlevel.openverse.client.game;

public interface Scene {
    void onEnable(Scene previous);

    void onDisable(Scene next);

    void onTick();

    void onRender();

    void onFps();
}
