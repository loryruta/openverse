package xyz.upperlevel.openverse.client.render.inventory;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.openverse.client.gl.GLUtil;
import xyz.upperlevel.openverse.client.gui.GuiAlign;
import xyz.upperlevel.openverse.client.gui.GuiBackground;
import xyz.upperlevel.openverse.client.gui.GuiBounds;
import xyz.upperlevel.openverse.client.gui.GuiRenderer;
import xyz.upperlevel.openverse.client.util.Color;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.world.entity.player.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class PlayerInventoryGui extends InventoryGui<PlayerInventory> {
    /**
     * Distance from every side of the selected slot to render the background
     */
    private static final int SELECTION_DISTANCE = 5;

    private static final int backgroundTexture;
    private static final int selectionTexture;

    private SlotContainerGui slotGui = new SlotContainerGui(9, 4);
    private SlotGui handSlot;
    private GuiBounds handSlotTexBounds;

    static {
        backgroundTexture = loadTexture("resources/guis/player_gui.png");
        selectionTexture  = loadTexture("resources/guis/selection.png");
    }

    public PlayerInventoryGui(PlayerInventory handle) {
        super(handle);
        buildSlots(handle);
        slotGui.setAlign(GuiAlign.CENTER);
        slotGui.setOffset(0);
        slotGui.setBackground(GuiBackground.texture(backgroundTexture));
        addChild(slotGui);
        setAlign(GuiAlign.CENTER);
        setOffset(30);
    }

    protected void buildSlots(PlayerInventory inv) {
        int w = slotGui.getHorizontalSlots();
        int h = slotGui.getVerticalSlots();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                slotGui.setSlot(x, y, new SlotGui(inv.get(y * w + x)));
            }
        }
    }

    public void recalcHandSlot() {
        PlayerInventory inventory = getHandle();
        int slotIndex = 9 * 3 + inventory.getHandSlot();
        handSlot = slotGui.getSlot(slotIndex);
        GuiBounds slotBounds = handSlot.getBounds();
        int distanceX = (int) ((slotGui.getWidth() / (float)slotGui.getRelWidthPixels()) * SELECTION_DISTANCE);
        int distanceY = (int) ((slotGui.getHeight() / (float)slotGui.getRelHeightPixels()) * SELECTION_DISTANCE);
        handSlotTexBounds = new GuiBounds(
                slotBounds.minX - distanceX,
                slotBounds.minY - distanceY,
                slotBounds.maxX + distanceX,
                slotBounds.maxY + distanceY
        );
    }

    // I'm hating inheritance :(

    @Override
    public void setWindow(Window window) {
        super.setWindow(window);
        onResize();
    }

    @Override
    public void reloadLayout() {
        onResize();
        super.reloadLayout();
        recalcHandSlot();
    }

    @Override
    public void onResize() {
        Window w = getWindow();
        if (w != null) {
            setSize(getParent().getWidth() - (getOffsetLeft() + getOffsetRight()), getParent().getHeight() - (getOffsetTop() + getOffsetBottom()));
            int width = (getWidth() - (slotGui.getOffsetLeft() + slotGui.getOffsetRight())) / slotGui.getHorizontalSlots();
            int height = (getHeight() - (slotGui.getOffsetTop() + slotGui.getOffsetBottom())) / slotGui.getVerticalSlots();
            int value = Math.min(width, height);
            slotGui.setSize(value * slotGui.getHorizontalSlots(), value * slotGui.getVerticalSlots());
        }
        super.onResize();
    }

    @Override
    public void render() {
        super.render();
        GuiRenderer r = GuiRenderer.get();
        r.setColor(Color.WHITE);
        r.setTexture(selectionTexture);
        r.render(getWindow(), handSlotTexBounds);
    }

    private static int loadTexture(String path) { // TODO centralize texture management?
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuf  = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer compBuf   = stack.mallocInt(1);

            ByteBuffer fmtImgBuf;
            try {
                fmtImgBuf = GLUtil.read(new File(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ByteBuffer imgBuf = stbi_load_from_memory(fmtImgBuf, widthBuf, heightBuf, compBuf, STBImage.STBI_rgb_alpha);
            if (imgBuf == null) {
                throw new RuntimeException("STB image loading failed: " + stbi_failure_reason());
            }

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthBuf.get(0), heightBuf.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, imgBuf);

            stbi_image_free(imgBuf);
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        return tex;
    }
}
