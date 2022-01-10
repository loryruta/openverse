package xyz.upperlevel.openverse.client.gui;

import lombok.Getter;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.client.window.WindowCursorEnterEvent;
import xyz.upperlevel.openverse.client.window.WindowCursorMoveEvent;
import xyz.upperlevel.openverse.client.window.WindowMouseButtonChangeEvent;
import xyz.upperlevel.openverse.client.window.WindowResizeEvent;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Used to wrap a Gui to simpify it's usage
 * <br>This class manages the input from the window and the Gui opening/closing
 * <br>use {@link #open(Gui)}, {@link #close()} and {@link #render()} for some basic usage
 */
public class GuiViewer implements Listener {
    @Getter
    private GuiContainer handle;
    private double lastMouseX, lastMouseY;

    /**
     * Creates the viewer and sets up the hooks for the user interaction
     * @param window the windows used to interact
     */
    public GuiViewer(Window window) {
        handle = new GuiContainer(window);
        handle.setOffset(0,0);
        handle.setSize(window.getWidth(), window.getHeight());
        handle.reloadLayout();
        handle.onOpen();
        window.getEventManager().register(this);

        lastMouseX = window.getCursorX();
        lastMouseY = window.getCursorY();
    }

    /**
     * Opens the Gui and, if necessary, closes the old one
     * @param gui
     */
    public void open(Gui gui) {
        if (!handle.isEmpty()) {
            close();
        }
        gui.onOpen();
        handle.addChild(gui);
    }

    /**
     * Returns the currently open Gui (if any is present)
     * @return the currently open Gui or null
     */
    public Gui getCurrent() {
        List<Gui> children = handle.getChildren();
        return children.isEmpty() ? null : children.get(0);
    }

    /**
     * Closes the currently open Gui, if any is out
     * <br>If no Gui is found it does nothing
     */
    public void close() {
        Gui gui = getCurrent();
        if (gui != null) {
            gui.onClose();
            handle.removeChild(gui);
        }
    }

    /**
     * Renders the Gui using the whole screen
     */
    public void render() {
        handle.render();
    }

    @EventHandler
    public void onClickBegin(WindowMouseButtonChangeEvent e) {
        Window window = e.getWindow();

        double curX = window.getCursorX();
        double curY = window.getCursorY();

        if (e.getAction() == GLFW_PRESS) {
            handle.onClickBegin(curX, curY, e.getButton());
        } else { // action == RELEASE
            handle.onClickEnd(curX, curY, e.getButton());
        }
    }

    @EventHandler
    public void onCursorEnter(WindowCursorEnterEvent e) {
        Window w = e.getWindow();
        handle.onCursorEnter(w.getCursorX(), w.getCursorY());
    }

    @EventHandler
    public void onCursorExit(WindowCursorEnterEvent e) {
        Window w = e.getWindow();
        handle.onCursorExit(w.getCursorX(), w.getCursorY());
    }

    @EventHandler
    public void onCursorPos(WindowCursorMoveEvent e) {
        double mouseX = e.getXPos();
        double mouseY = e.getYPos();
        handle.onCursorMove(lastMouseX, lastMouseY, mouseX, mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @EventHandler
    public void onResize(WindowResizeEvent e) {
        handle.onResize();
        handle.reloadLayout();
    }
}
