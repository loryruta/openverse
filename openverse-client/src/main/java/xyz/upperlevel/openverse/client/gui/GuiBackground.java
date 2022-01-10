package xyz.upperlevel.openverse.client.gui;


import xyz.upperlevel.openverse.client.gl.GLUtil;
import xyz.upperlevel.openverse.client.util.Color;
import xyz.upperlevel.openverse.client.window.Window;

public interface GuiBackground {
    void render(Window window, GuiBounds bounds);

    GuiBackground TRANSPARENT = (w, b) -> {};

    static GuiBackground color(Color color) {
        return (window, bounds) -> {
            GuiRenderer r = GuiRenderer.get();
            r.setColor(color);
            r.setTexture(GLUtil.getNullTexture2d());
            r.render(window, bounds);
        };
    }

    static GuiBackground texture(int texture) {
        return (window, bounds) -> {
            GuiRenderer r = GuiRenderer.get();
            r.setColor(Color.WHITE);
            r.setTexture(texture);
            r.render(window, bounds);
        };
    }

    static GuiBackground of(int texture, Color color) {
        return (window, bounds) -> {
            GuiRenderer r = GuiRenderer.get();
            r.setColor(color);
            r.setTexture(texture);
            r.render(window, bounds);
        };
    }
}
