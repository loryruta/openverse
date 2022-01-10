package xyz.upperlevel.openverse.client.render.inventory;

import org.joml.Matrix4f;
import xyz.upperlevel.openverse.client.gl.Program;
import xyz.upperlevel.openverse.client.gui.GuiBounds;
import xyz.upperlevel.openverse.client.render.block.TextureBakery;
import xyz.upperlevel.openverse.client.window.Window;
import xyz.upperlevel.openverse.item.ItemStack;
import xyz.upperlevel.openverse.util.exceptions.NotImplementedException;
import xyz.upperlevel.openverse.world.block.BlockFace;
import xyz.upperlevel.openverse.world.block.BlockType;
import xyz.upperlevel.openverse.world.block.state.BlockState;

import java.util.List;

public class BlockItemRenderer implements ItemRenderer { // TODO
    private static Program program;
    //private static Uniform boundsLoc;
    private StateRenderData[] renderersByState;

    static {
        //program = OpenverseClient.get().getResources().programs().entry("gui_item_shader");
        //program.use();
        //boundsLoc = program.getUniform("bounds");
        //if (boundsLoc == null) throw new IllegalStateException("Cannot find Uniform 'bounds'");
    }

    public BlockItemRenderer(BlockType type) {
        this(type, BlockFace.FRONT);
    }

    public BlockItemRenderer(BlockType type, BlockFace displayFace) {
        setupStateRenderers(type, displayFace);
    }

    private void setupStateRenderers(BlockType type, BlockFace displayFace) {
        List<? extends BlockState> states = type.getStateRegistry().getStates();
        renderersByState = new StateRenderData[states.size()];
        for (int i = 0; i < states.size(); i++) {
            renderersByState[i] = new StateRenderData();
            renderersByState[i].setup(states.get(i), displayFace);
        }
    }

    @Override
    public void renderInSlot(ItemStack item, Window window, GuiBounds bounds, SlotGui slot) {
        /* render the item in slot
        program.use();
        float invWidth = 1f / window.getWidth();
        float invHeight = 1f / window.getHeight();

        boundsLoc.set(
                (float) bounds.minX * invWidth,
                1.0f - (float) bounds.minY * invHeight,         // Invert y
                (float) (bounds.maxX - bounds.minX) * invWidth,    // Convert maxX to width
                (float) (bounds.minY - bounds.maxY) * invHeight     // Convert maxY to height & Invert y: 1 - (max - min) = (min - max)
        );
        //TextureBakery.bind();

        StateRenderData data = renderersByState[item.getState()];

        if (data.vertices > 0) {
            //data.vao.bind();
            //data.vao.draw(DrawMode.QUADS, 0, data.vertices);
        }*/
    }

    @Override
    public void renderInHand(ItemStack item, Matrix4f trans) {
        throw new NotImplementedException();
    }

    public void destroy() {
        for (StateRenderData data : renderersByState) {
            data.destroy();
        }
    }

    private static class StateRenderData {
        //public Vao vao;
        //public Vbo vbo;
        public int vertices;

        public void setup(BlockState state, BlockFace displayFace) {
            /*
            BlockModel model = BlockTypeModelMapper.model(state);
            if (model == null) {
                throw new IllegalStateException("Cannot find model for " + state);
            }
            List<BlockPart> parts = model.getParts();
            int facesNum = 6;
            ByteBuffer buffer = BufferUtils.createByteBuffer(facesNum * 4 * 6 * Float.BYTES);
            vertices = 0;
            // todo
            buffer.flip();

            vao = new Vao();
            vao.bind();

            vbo = new Vbo();
            vbo.bind();

            VertexLinker linker = new VertexLinker();
            linker.attrib(program.getAttribLocation("position"), 3, DataType.FLOAT, false, 0);
            linker.attrib(program.getAttribLocation("texCoords"), 3, DataType.FLOAT, false, 3 * DataType.FLOAT.getByteCount());
            linker.setup();

            vbo.loadData(buffer, VboDataUsage.STATIC_DRAW);
            */
        }

        public void destroy() {
        }
    }
}
