package xyz.upperlevel.openverse.client.render.world;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.client.Launcher;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.client.render.block.TextureBakery;
import xyz.upperlevel.openverse.client.render.world.util.VertexBufferPool;
import xyz.upperlevel.openverse.client.util.GLUtil;
import xyz.upperlevel.openverse.client.world.ClientWorld;
import xyz.upperlevel.openverse.event.BlockUpdateEvent;
import xyz.upperlevel.openverse.event.ShutdownEvent;
import xyz.upperlevel.openverse.world.chunk.ChunkLocation;
import xyz.upperlevel.openverse.world.chunk.event.ChunkLightChangeEvent;
import xyz.upperlevel.openverse.world.event.ChunkLoadEvent;
import xyz.upperlevel.openverse.world.event.ChunkUnloadEvent;
import xyz.upperlevel.ulge.window.event.ResizeEvent;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.glClearTexImage;

public class ChunkViewRenderer implements Listener {
    public static final int MAX_RENDER_DISTANCE = 3;

    private static GBufferProgram     gBufferProgram;
    private static ApplyLightsProgram applyLightsProgram;
    private static SSAOPass           ssaoPass;

    @Getter
    private ClientWorld world;

    @Getter
    private final VertexBufferPool vertexProvider = new VertexBufferPool(50);

    @Getter
    private final VertexBufferPool syncProvider = new VertexBufferPool(1);

    @Getter
    private final ExecutorService detachedChunkCompiler = Executors.newSingleThreadExecutor(t -> new Thread(t, "Chunk Compiler thread"));

    @Getter
    private final Queue<ChunkCompileTask> pendingTasks = new ArrayDeque<>(10);

    @Getter
    private int distance;

    @Getter
    private GBuffer gBuffer;

    private final Map<ChunkLocation, ChunkRenderer> chunks = new HashMap<>();

    public ChunkViewRenderer() {
        this.distance = 1;

        int initScreenWidth  = Launcher.get().getGame().getWindow().getWidth();
        int initScreenHeight = Launcher.get().getGame().getWindow().getHeight();
        this.gBuffer = new GBuffer(initScreenWidth, initScreenHeight);

        OpenverseClient.get().getEventManager().register(this);
    }

    public void loadChunk(ChunkRenderer chunk) {
        ChunkRenderer previous = chunks.put(chunk.getChunk().getLocation(), chunk);
        if (previous != null) {
            previous.destroy();
            throw new IllegalStateException("Chunk Renderer already loaded: " + chunk.getChunk().getLocation());
        }
    }

    public void unloadChunk(ChunkLocation location) {
        ChunkRenderer chunk = chunks.remove(location);
        if (chunk != null) {
            chunk.destroy();
        }
    }

    public ChunkRenderer getChunk(ChunkLocation location) {
        return chunks.get(location);
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
        //destroy();
    }

    public void recompileChunk(ChunkRenderer chunk, ChunkCompileMode mode) {
        if (mode == ChunkCompileMode.INSTANT) {
            pendingTasks.add(new ChunkCompileTask(syncProvider, chunk));
        } else {
            ChunkCompileTask task = chunk.createCompileTask(vertexProvider);
            detachedChunkCompiler.execute(() -> {
                if (task.compile()) {
                    // If the task compiled successfully
                    // Put it in the uploading queue
                    pendingTasks.add(task);
                }
            });
        }
    }

    private void clearGBuffer(GBuffer gBuffer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glBindFramebuffer(GL_FRAMEBUFFER, gBuffer.getFramebuffer());

            glClear(GL_DEPTH_BUFFER_BIT);

            FloatBuffer black = stack.floats(0, 0, 0, 0);
            glClearTexImage(gBuffer.getPositionTexture(),      0, GL_RGBA, GL_FLOAT, black);
            glClearTexImage(gBuffer.getNormalTexture(),        0, GL_RGBA, GL_FLOAT, black);
            glClearTexImage(gBuffer.getAlbedoTexture(),        0, GL_RGBA, GL_FLOAT, black);
            glClearTexImage(gBuffer.getBlockLightTexture(),    0, GL_RGBA, GL_FLOAT, black);
            glClearTexImage(gBuffer.getBlockSkylightTexture(), 0, GL_RGBA, GL_FLOAT, black);
            glClearTexImage(ssaoPass.getSsaoTexture(),         0, GL_RGBA, GL_FLOAT, black);
        }

    }

    private void fillGBuffer(GBuffer gBuffer, Matrix4f transform, Matrix4f camera) {
        try (MemoryStack stack = MemoryStack.stackPush()) { // todo
            glUseProgram(gBufferProgram.getProgramName());

            glBindFramebuffer(GL_FRAMEBUFFER, gBuffer.getFramebuffer());

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_ALPHA_TEST);
            glEnable(GL_CULL_FACE);

            FloatBuffer transformBuf = stack.callocFloat(16);
            transform.get(transformBuf);
            glUniformMatrix4fv(GBufferProgram.UNIFORM_TRANSFORM, false, transformBuf);

            FloatBuffer cameraBuf = stack.callocFloat(16);
            camera.get(cameraBuf);
            glUniformMatrix4fv(GBufferProgram.UNIFORM_CAMERA, false, cameraBuf);

            float worldSkylight = world.getSkylight();
            glUniform1f(GBufferProgram.UNIFORM_WORLD_SKYLIGHT, worldSkylight);

            glUniform1i(GBufferProgram.UNIFORM_BLOCK_TEXTURES, 0);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D_ARRAY, TextureBakery.textureArray.getId());

            for (ChunkRenderer bakedChunk : chunks.values()) { // todo ChunkRenderer -> BakedChunk
                if (bakedChunk.getDrawVerticesCount() > 0) {
                    glBindVertexArray(bakedChunk.getVao());
                    glDrawArrays(GL_TRIANGLES, 0, bakedChunk.getDrawVerticesCount());
                }
            }
        }
    }

    private void applyLights(GBuffer gBuffer) {
        glUseProgram(applyLightsProgram.getProgramName());

        glBindFramebuffer(GL_FRAMEBUFFER, 0); // draw to screen

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_CULL_FACE);

        // position
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_POSITION, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gBuffer.getPositionTexture());

        // normal
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_NORMAL, 1);
        glActiveTexture(GL_TEXTURE0 + 1);
        glBindTexture(GL_TEXTURE_2D, gBuffer.getNormalTexture());

        // albedo
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_ALBEDO, 2);
        glActiveTexture(GL_TEXTURE0 + 2);
        glBindTexture(GL_TEXTURE_2D, gBuffer.getAlbedoTexture());

        // block light
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_BLOCK_LIGHT, 3);
        glActiveTexture(GL_TEXTURE0 + 3);
        glBindTexture(GL_TEXTURE_2D, gBuffer.getBlockLightTexture());

        // block skylight
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_BLOCK_SKYLIGHT, 4);
        glActiveTexture(GL_TEXTURE0 + 4);
        glBindTexture(GL_TEXTURE_2D, gBuffer.getBlockSkylightTexture());

        // ssao
        glUniform1i(ApplyLightsProgram.UNIFORM_GBUFFER_SSAO, 5);
        glActiveTexture(GL_TEXTURE0 + 5);
        glBindTexture(GL_TEXTURE_2D, ssaoPass.getSsaoTexture());

        glBindVertexArray(GLUtil.getEmptyVao());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void render(Matrix4f view, Matrix4f projection) {
        uploadPendingChunks();

        Matrix4f transform = new Matrix4f();

        Matrix4f camera = new Matrix4f(projection);
        camera.mul(view);

        clearGBuffer(gBuffer);
        fillGBuffer(gBuffer, transform, camera);

        if (OpenverseClient.get().isSsaoEnabled()) {
            ssaoPass.run(gBuffer, view, projection);
        }

        applyLights(gBuffer); // finally, apply lights and write output to screen's framebuffer
    }

    public void uploadPendingChunks() {
        ChunkCompileTask task;
        while ((task = pendingTasks.poll()) != null) {
            task.completeNow();
        }
    }

    public void recompileChunksAround(int x, int y, int z, ChunkCompileMode mode) {
        for (int chkX = x - 1; chkX <= x + 1; chkX++) {
            for (int chkY = y - 1; chkY <= y + 1; chkY++) {
                for (int chkZ = z - 1; chkZ <= z + 1; chkZ++) {
                    ChunkRenderer chunk = getChunk(ChunkLocation.of(chkX, chkY, chkZ));
                    if (chunk == null) {
                        continue;
                    }
                    recompileChunk(chunk, mode);
                }
            }
        }
    }

    public void recompileChunksAroundBlock(int x, int y, int z, ChunkCompileMode mode) {
        int minX = (x - 1) >> 4;
        int minY = (y - 1) >> 4;
        int minZ = (z - 1) >> 4;
        int maxX = (x + 1) >> 4;
        int maxY = (y + 1) >> 4;
        int maxZ = (z + 1) >> 4;
        for (int chkX = minX; chkX <= maxX; chkX++) {
            for (int chY = minY; chY <= maxY; chY++) {
                for (int chZ = minZ; chZ <= maxZ; chZ++) {
                    ChunkRenderer chunk = getChunk(ChunkLocation.of(chkX, chY, chZ));
                    if (chunk == null) {
                        continue;
                    }
                    recompileChunk(chunk, mode);
                }
            }
        }
    }

    public void destroy() {
        OpenverseClient.get().getLogger().fine("Shutting down chunk compiler");
        detachedChunkCompiler.shutdownNow();
        OpenverseClient.get().getLogger().fine("Done");
        chunks.values().forEach(ChunkRenderer::destroy);
        chunks.clear();

        gBuffer.destroy();
    }

    @EventHandler
    public void onWindowResize(ResizeEvent event) {
        OpenverseClient.logger().fine(String.format("[ChunkViewRenderer] Resizing window to: (%d, %d)", event.getWidth(), event.getHeight()));

        gBuffer.destroy();
        gBuffer = new GBuffer(event.getWidth(), event.getHeight());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        loadChunk(new ChunkRenderer(this, e.getChunk()));

        ChunkLocation loc = e.getChunk().getLocation();
        recompileChunksAround(loc.x, loc.y, loc.z, ChunkCompileMode.ASYNC);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        unloadChunk(e.getChunk().getLocation());
    }

    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent e) {
        if (world != e.getWorld()) {
            return;
        }
        recompileChunksAroundBlock(e.getX(), e.getY(), e.getZ(), ChunkCompileMode.INSTANT);
    }

    @EventHandler
    public void onChunkLightChange(ChunkLightChangeEvent e) {
        if (world != e.getWorld()) {
            return;
        }
        recompileChunk(getChunk(e.getChunk().getLocation()), ChunkCompileMode.ASYNC);
    }

    @EventHandler
    public void onShutdown(ShutdownEvent e) {
        destroy();
    }

    public static void init() throws IOException {
        gBufferProgram     = new GBufferProgram();
        ssaoPass           = new SSAOPass();
        applyLightsProgram = new ApplyLightsProgram();
    }

    public static void terminate() {
        gBufferProgram.destroy();
        ssaoPass.destroy();
        applyLightsProgram.destroy();
    }
}
