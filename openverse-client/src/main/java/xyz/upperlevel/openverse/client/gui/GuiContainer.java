package xyz.upperlevel.openverse.client.gui;

import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.openverse.client.window.Window;

public class GuiContainer extends Gui {
    public GuiContainer(Window window, EventManager handler) {
        super(window, handler);
    }

    public GuiContainer(Window window) {
        super(window);
    }

    public GuiContainer() {
        super();
    }

    @Override
    public void setWindow(Window window) {
        super.setWindow(window);
        onResize();
    }

    @Override
    public void reloadLayout() {
        onResize();
        super.reloadLayout();
    }

    @Override
    public void onResize() {
        Window w = getWindow();
        if (w != null) {
            setSize(w.getWidth() - (getOffsetLeft() + getOffsetRight()), w.getHeight() - (getOffsetTop() + getOffsetBottom()));
        }
        super.onResize();
    }
}
