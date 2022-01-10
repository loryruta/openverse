package xyz.upperlevel.openverse.client.game;

import lombok.Getter;

public class Stage implements Scene {
    @Getter
    private Scene currentScene = null;

    public Stage() {
    }

    public void setScene(Scene scene) {
        if (this.currentScene != null) {
            this.currentScene.onDisable(scene);
        }

        Scene oldScene = this.currentScene;
        this.currentScene = scene;

        if (scene != null) {
            scene.onEnable(oldScene);
        }
    }

    @Override
    public void onEnable(Scene previous) {
        if (currentScene != null) {
            currentScene.onEnable(previous);
        }
    }

    @Override
    public void onDisable(Scene next) {
        if (currentScene != null) {
            currentScene.onDisable(next);
        }
    }

    @Override
    public void onTick() {
        if (currentScene != null) {
            currentScene.onTick();
        }
    }

    @Override
    public void onFps() {
        if (currentScene != null) {
            currentScene.onFps();
        }
    }

    @Override
    public void onRender() {
        if (currentScene != null) {
            currentScene.onRender();
        }
    }
}
