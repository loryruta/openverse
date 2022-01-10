package xyz.upperlevel.openverse.client.window;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import xyz.upperlevel.event.EventManager;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    @Getter
    private final long windowId;

    @Getter
    private final EventManager eventManager;

    private final IntBuffer widthBuf, heightBuf;
    private final DoubleBuffer cursorXBuf, cursorYBuf;

    public Window(long windowId) {
        this.windowId = windowId;

        this.eventManager = new EventManager();
        subscribeInputCallbacks();

        this.widthBuf   = BufferUtils.createIntBuffer(1);
        this.heightBuf  = BufferUtils.createIntBuffer(1);
        this.cursorXBuf = BufferUtils.createDoubleBuffer(1);
        this.cursorYBuf = BufferUtils.createDoubleBuffer(1);
    }

    public void destroy() {
        glfwDestroyWindow(windowId);
    }

    protected void subscribeInputCallbacks() {
        // Key change
        GLFW.glfwSetKeyCallback(windowId, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                //System.out.printf("WindowKeyEvent: window=%d, key=%d, scancode=%d, action=%d, mods=%d\n", window, key, scancode, action, mods);
                WindowKeyChangeEvent e = new WindowKeyChangeEvent(Window.this, key, scancode, action, mods);
                eventManager.call(e);
            }
        });

        // Cursor move
        GLFW.glfwSetCursorPosCallback(windowId, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                //System.out.printf("WindowCursorPosEvent: key=%d, xpos=%f, ypos=%f\n", window, xPos, yPos);
                WindowCursorMoveEvent e = new WindowCursorMoveEvent(Window.this, xPos, yPos);
                eventManager.call(e);
            }
        });

        // Window resize
        GLFW.glfwSetWindowSizeCallback(windowId, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WindowResizeEvent e = new WindowResizeEvent(Window.this, width, height);
                eventManager.call(e);
            }
        });

        // Mouse button change
        GLFW.glfwSetMouseButtonCallback(windowId, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                WindowMouseButtonChangeEvent e = new WindowMouseButtonChangeEvent(Window.this, button, action, mods);
                eventManager.call(e);
            }
        });

        // Scroll
        GLFW.glfwSetScrollCallback(windowId, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                WindowScrollEvent e = new WindowScrollEvent(Window.this, xOffset, yOffset);
                eventManager.call(e);
            }
        });

        // Cursor enter/exit
        GLFW.glfwSetCursorEnterCallback(windowId, new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                if (entered) {
                    WindowCursorEnterEvent e = new WindowCursorEnterEvent(Window.this);
                    eventManager.call(e);
                } else {
                    WindowCursorExitEvent e = new WindowCursorExitEvent(Window.this);
                    eventManager.call(e);
                }
            }
        });

        // Char
        GLFW.glfwSetCharCallback(windowId, new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                WindowCharEvent e = new WindowCharEvent(Window.this, codepoint);
                eventManager.call(e);
            }
        });
    }

    protected void updateSize() {
        glfwGetWindowSize(windowId, widthBuf, heightBuf);
    }

    public int getWidth() {
        updateSize();
        return widthBuf.get(0);
    }

    public int getHeight() {
        updateSize();
        return heightBuf.get(0);
    }

    protected void updateCursorPosition() {
        glfwGetCursorPos(windowId, cursorXBuf, cursorYBuf);
    }

    public double getCursorX() {
        updateCursorPosition();
        return cursorXBuf.get(0);
    }

    public double getCursorY() {
        updateCursorPosition();
        return cursorYBuf.get(0);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }

    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(windowId, shouldClose);
    }

    public int getInputMode(int mode) {
        return glfwGetInputMode(windowId, mode);
    }

    public void setInputMode(int mode, int value) {
        glfwSetInputMode(windowId, mode, value);
    }

    public int getKey(int key) {
        return glfwGetKey(windowId, key);
    }
}
