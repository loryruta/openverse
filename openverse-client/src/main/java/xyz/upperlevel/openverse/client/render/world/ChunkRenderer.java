package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import xyz.upperlevel.openverse.client.render.block.BlockModel;
import xyz.upperlevel.openverse.client.render.block.BlockTypeModelMapper;
import xyz.upperlevel.openverse.client.render.world.util.VertexBufferPool;
import xyz.upperlevel.openverse.world.block.state.BlockState;
import xyz.upperlevel.openverse.world.chunk.Chunk;
import xyz.upperlevel.openverse.world.chunk.storage.BlockStorage;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ChunkRenderer {
    public static final int ATTRIB_POSITION = 0;
    public static final int ATTRIB_NORMAL = 1;
    public static final int ATTRIB_TEX_COORDS = 2;
    public static final int ATTRIB_BLOCK_LIGHT = 3;
    public static final int ATTRIB_BLOCK_SKYLIGHT = 4;

    public static final int VERTEX_SIZE = 3 + 3 + (2 + 1) + 1 + 1;
    public static final int VERTEX_BYTE_SIZE = VERTEX_SIZE * Float.BYTES;

    @Getter
    private final ChunkViewRenderer view;

    @Getter
    private final Chunk chunk;

    @Getter
    private int vao = -1;

    @Getter
    private int vbo = -1;

    private ChunkCompileTask compileTask;

    @Getter
    private int
            allocateVerticesCount = 0, // vertices to allocate on vbo init
            allocateDataCount = 0; // data to allocate on vbo init

    @Getter
    private int
            drawVerticesCount = 0; // draw vertices count on drawing

    public ChunkRenderer(ChunkViewRenderer view, Chunk chunk) {
        this.view = view;
        this.chunk = chunk;

        initBuffers();
        reloadVertexSize();
    }

    public void destroy() {
        //Openverse.getLogger().warning("Destroying VBO for chunk: " + location);
        if (compileTask != null) {
            compileTask.abort();
        }

        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    private void initBuffers() {
        if (this.vao >= 0 || this.vbo >= 0) {
            throw new IllegalStateException("Chunk has already been initialized");
        }

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        int attribPtr = 0;

        // Position
        glEnableVertexAttribArray(ATTRIB_POSITION);
        glVertexAttribPointer(ATTRIB_POSITION, 3, GL_FLOAT, false, VERTEX_BYTE_SIZE, attribPtr);
        attribPtr += 3 * Float.BYTES;

        // Normal
        glEnableVertexAttribArray(ATTRIB_NORMAL);
        glVertexAttribPointer(ATTRIB_NORMAL, 3, GL_FLOAT, false, VERTEX_BYTE_SIZE, attribPtr);
        attribPtr += 3 * Float.BYTES;

        // Tex coords
        glEnableVertexAttribArray(ATTRIB_TEX_COORDS);
        glVertexAttribPointer(ATTRIB_TEX_COORDS, 3, GL_FLOAT, false, VERTEX_BYTE_SIZE, attribPtr);
        attribPtr += 3 * Float.BYTES;

        // Block light
        glEnableVertexAttribArray(ATTRIB_BLOCK_LIGHT);
        glVertexAttribPointer(ATTRIB_BLOCK_LIGHT, 1, GL_FLOAT, false, VERTEX_BYTE_SIZE, attribPtr);
        attribPtr += Float.BYTES;

        // Block skylight
        glEnableVertexAttribArray(ATTRIB_BLOCK_SKYLIGHT);
        glVertexAttribPointer(ATTRIB_BLOCK_SKYLIGHT, 1, GL_FLOAT, false, VERTEX_BYTE_SIZE, attribPtr);
        //ptr += Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /*
    public void onBlockChange(BlockState oldState, BlockState newState) {//TODO: call whenever a block changes
        BlockModel om = BlockTypeModelMapper.model(oldState);
        BlockModel nm = BlockTypeModelMapper.model(newState);

        // gets vertices/data count for old and new model
        int oldVrt = om == null ? 0 : om.getVerticesCount();
        int newVrt = nm == null ? 0 : nm.getVerticesCount();

        int oldData = om == null ? 0 : om.getDataCount();
        int newData = nm == null ? 0 : nm.getDataCount();

        // updates vertices/data count
        allocateVerticesCount += newVrt - oldVrt;
        allocateDataCount += newData - oldData;

        // rebuilds chunk if requested
    }*/

    public void reloadVertexSize() {
        BlockStorage storage = chunk.getBlockStorage();
        int vertexCount = 0;
        int dataCount = 0;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockModel model = BlockTypeModelMapper.model(storage.getBlockState(x, y, z));
                    if (model != null) {
                        vertexCount += model.getVerticesCount();
                        dataCount += model.getVerticesCount() * VERTEX_SIZE;
                    }
                }
            }
        }
        allocateVerticesCount = vertexCount;
        allocateDataCount = dataCount;
    }

    public ChunkCompileTask createCompileTask(VertexBufferPool pool) {
        if (compileTask != null) {
            compileTask.abort();
            compileTask = null;
        }
        compileTask = new ChunkCompileTask(pool, this);
        return compileTask;
    }

    public int compile(ByteBuffer buffer) {
        BlockStorage storage = chunk.getBlockStorage();
        int vertexCount = 0;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockState state = storage.getBlockState(x, y, z);
                    if (state != null) {
                        BlockModel model = BlockTypeModelMapper.model(state);
                        if (model != null) {
                            vertexCount += model.bake(chunk.getWorld(), chunk.getX() * 16 + x, chunk.getY() * 16 + y, chunk.getZ() * 16 + z, buffer);
                        }
                    }
                }
            }
        }
        buffer.flip();
        //Openverse.getLogger().info("Vertices computed for chunk at: " + vertexCount + "(" + buffer.remaining() + ")");
        return vertexCount;
    }

    public void setVertices(ByteBuffer vertices, int vertexCount) {
        if (vertexCount > 0) {
            if (vertices.remaining() == 0) {
                throw new IllegalStateException("No vertex in buffer but " + vertexCount + " to draw");
            }

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }
        this.drawVerticesCount = vertexCount;
    }
}
