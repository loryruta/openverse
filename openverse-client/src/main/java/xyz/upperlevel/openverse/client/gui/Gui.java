package xyz.upperlevel.openverse.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.openverse.client.gui.events.*;
import xyz.upperlevel.openverse.client.window.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gui {
    /**
     * Gui that contains this Gui instance
     *
     * @param parent the new Gui parent
     * @return the Gui's parent or null if this is a root gui
     */
    @Getter
    @Setter
    private Gui parent;

    /**
     * Window this Gui is displayed in
     *
     * @param window the new Window (used on initialization)
     * @return the window in which the Gui is drawn (null if not initialized)
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Window window;

    /**
     * The width of the Gui in pixels.
     *
     * @param width the new Gui's width
     * @return the current width of the Gui
     */
    @Getter
    @Setter
    private int width;

    /**
     * The height of the Gui in pixels.
     *
     * @param height the new Gui's height
     * @return the current height of the Gui
     */
    @Getter
    @Setter
    private int height;

    @Getter
    @Setter
    private int offsetLeft, offsetRight, offsetTop, offsetBottom;

    /**
     * The align used by the Gui.
     * <br>Ex. if set to {@link GuiAlign#CENTER} the Gui will try to stay in the center of the parent space.
     * <br>Note: the actual Gui implementation may ignore this.
     *
     * @paran align The new align that this Gui will use
     * @return the align that this Gui is currently using
     */
    @Getter
    @Setter
    private GuiAlign align = GuiAlign.CENTER;

    private List<Gui> children = new ArrayList<>();

    // LAYOUT DEPENDANT VARS

    @Getter
    private int realX, realY, relX, relY;

    /**
     * The {@link GuiBounds} in screen-coords used by the Gui.
     * <br>This variable is layout dependant, call {@link #reloadLayout()} to recalculate.
     *
     * @return The screen-coords bounds of the Gui
     */
    @Getter
    private GuiBounds bounds = GuiBounds.EMPTY;

    // END LAYOUT DEPENDANT VARS

    /**
     * True if the testMouseButton is currently inside of the gui (even if it's clicked).
     * @return true if currently hovered
     */
    @Getter
    private boolean hovered;
    /**
     * Ttrue if the Gui is being clicked by the testMouseButton.
     * @return true if clicked
     */
    @Getter
    private boolean clicked;

    /**
     * The {@link EventManager} to notify this Gui's events.
     * @return the {@link EventManager} used by this Gui
     */
    @Getter
    private EventManager eventManager;

    /**
     * The {@link GuiBackground} used to render the Gui.
     * <br>Note: The actual Gui implementation may ignore the background.
     * @param background the new {@link GuiBackground}
     * @return the currently used {@link GuiBackground}
     */
    @Getter
    @Setter
    private GuiBackground background = GuiBackground.TRANSPARENT;

    public Gui(Window window, EventManager handler) {
        this.eventManager = handler;
        if (window != null) {
            setWindow(window);
            reloadLayout();
        }
    }

    public Gui(Window window) {
        this(window, new EventManager());
    }

    public Gui () {
        this(null, new EventManager());
    }

    public void setOffset(int left, int right, int top, int bottom) {
        offsetLeft = left;
        offsetRight = right;
        offsetTop = top;
        offsetBottom = bottom;
    }

    public void setOffset(int vertical, int horizontal) {
        setOffset(horizontal, horizontal, vertical, vertical);
    }

    public void setOffset(int offset) {
        setOffset(offset, offset, offset, offset);
    }

    /**
     * Sets the dimension of the gui.
     * @param w the new width
     * @param h the new height
     */
    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    /**
     * Checks if a point is inside of the Gui using the screen reference.
     * @param x the point's x coordinate
     * @param y the point's y coordinate
     * @return true only if the point is inside of the Gui, false otherwise
     */
    public boolean isInside(double x, double y) {
        return bounds.isInside(x, y);
    }

    public void reloadLayout() {
        if (parent != null) {
            if (window == null) {
                setWindow(parent.getWindow());
            }
            reloadLayout(parent.relX, parent.relY, parent.width, parent.height);
        } else if (window != null) {
            reloadLayout(0, 0, window.getWidth(), window.getHeight());
        } else throw new IllegalStateException("Cannot reload layout without no parent nor window");
    }

    public void reloadLayout(int parX, int parY, int parWidth, int parHeight) {
        { // horizontal layout
            switch (align.getHorizontal()) {
                case 0: // Left
                    realX = parX + offsetLeft;
                    break;
                case 1: // Center
                    realX = parX + offsetLeft + (parWidth - (offsetLeft + width + offsetRight)) / 2;
                    break;
                case 2: // Right
                    realX = parX + parWidth - (width + offsetRight);
                    break;
            }
        }

        { // vertical layout
            switch (align.getVertical()) {
                case 0: // Left
                    realY = parY + offsetTop;
                    break;
                case 1: // Center
                    realY = parY + offsetTop + (parHeight - (offsetTop + height + offsetBottom)) / 2;
                    break;
                case 2: // Right
                    realY = parY + parHeight - (height + offsetBottom);
                    break;
            }
        }

        // Relative coordinates must always be relative to the parent
        // while parX and parY might be changed to create limit the gui bounds
        if (parent != null) {
            relX = realX - parent.realX;
            relY = realY - parent.realY;
        } else {
            relX = realX;
            relY = realY;
        }

        bounds = new GuiBounds(realX, realY, realX + width, realY + height);

        children.forEach(Gui::reloadLayout);
    }

    public List<Gui> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns true only if the container doesn't contain any child.
     * @return true if no child is found
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void addChild(Gui child) {
        children.add(child);
        child.setParent(this);
    }

    public void addChildren(Collection<? extends Gui> children) {
        this.children.addAll(children);
        children.forEach(i -> i.setParent(this));
    }

    public boolean removeChild(Gui child) {
        if (children.remove(child)) {
            child.setParent(null);
            return true;
        } else return false;
    }

    /**
     * Called when the cursor enters the Gui.
     * @param x the entry x coordinate
     * @param y the entry y coordinate
     */
    public void onCursorEnter(double x, double y) {
        for (Gui child : children) {
            if (child.isInside(realX + x, realY + y)) {
                child.onCursorEnter(x - child.relX, y - child.relY);
            }
        }
        getEventManager().call(new GuiCursorEnterEvent(this, x, y));
        hovered = true;
    }

    /**
     * Called when the cursor exits the Gui.
     * @param x the last x coordinate
     * @param y the last y coordinate
     */
    public void onCursorExit(double x, double y) {
        for (Gui child : children) {
            if (child.isHovered()) {
                child.onCursorExit(x - child.relX, y - child.relY);
            }
        }
        getEventManager().call(new GuiCursorExitEvent(this, x, y));
        hovered = false;
        clicked = false;
    }

    /**
     * Called when the cursor moves.
     * @param startX the x of the starting point
     * @param startY the y of the starting point
     * @param endX the x of the ending point
     * @param endY the y of the ending point
     */
    public void onCursorMove(double startX, double startY, double endX, double endY) {
        for (Gui child : children) {

            // Check if testMouseButton was or is inside of the handle
            boolean wasInside = child.isHovered();
            boolean isInside = child.isInside(realX + endX, realY + endY);

            // No exit nor enter, always outside
            if (!wasInside && !isInside) continue;

            if (wasInside && isInside) {
                // Mouse was and still is inside, just a move
                child.onCursorMove(
                        startX - child.relX, startY - child.relY,
                        endX - child.relX, endY - child.relY
                );
            } else if (isInside) {
                // Mouse was outside and came inside (enter)
                child.onCursorEnter(endX - child.relX, endY - child.relY);
            } else {
                // Mouse was inside and went outside (exit)
                child.onCursorExit(startX - child.relX, startY - child.relY);
            }
        }
        eventManager.call(new GuiCursorMoveEvent(this, startX, startY, endX, endY));
    }

    /**
     * Called when the cursor begins a click (button pressed is specified in MouseButton).
     * @param x the x of the cursor
     * @param y the y of the cursor
     * @param button the pressed button
     */
    public void onClickBegin(double x, double y, int button) {
        for (Gui child : children) {
            if (child.isInside(realX + x, realY + y)) {
                child.onClickBegin(x - child.relX, y - child.relY, button);
            }
        }
        eventManager.call(new GuiClickBeginEvent(this, x, y, button));
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            clicked = true;
        }
    }

    /**
     * Called when the cursor ends a click (button pressed is specified in MouseButton).
     * @param x the x of the cursor
     * @param y the y of the cursor
     * @param button the pressed button
     */
    public void onClickEnd(double x, double y, int button) {
        for (Gui child : children) {
            if (child.isHovered()) {
                child.onClickEnd(x - child.relX, y - child.relY, button);
            }
        }
        eventManager.call(new GuiClickEndEvent(this, x, y, button));
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            clicked = false;
        }
    }

    /**
     * Called when the gui is opened.
     */
    public void onOpen() {
        eventManager.call(new GuiOpenEvent(this));
        children.forEach(Gui::onOpen);
    }

    /**
     * Called when the gui is closed.
     */
    public void onClose() {
        children.forEach(Gui::onClose);
        eventManager.call(new GuiCloseEvent(this));
    }

    /**
     * Called when the window is resized (before the layout call).
     */
    public void onResize() {
        for (Gui child : children) {
            child.onResize();
        }
    }

    /**
     * Renders both this Gui and it's children.
     * <br>{@link #renderCurrent()} is used to render the local gui while {@link #renderChildren()} is used to render the children.
     */
    public void render() {
        renderCurrent();
        renderChildren();
    }

    /**
     * Renders only the current Gui.
     */
    protected void renderCurrent() {
        background.render(window, bounds);
    }

    /**
     * Renders the Gui's children.
     */
    protected void renderChildren() {
        children.forEach(Gui::render);
    }
}