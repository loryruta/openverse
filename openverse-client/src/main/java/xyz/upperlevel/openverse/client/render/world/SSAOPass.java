package xyz.upperlevel.openverse.client.render.world;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import xyz.upperlevel.openverse.client.util.GLUtil;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL44.glBufferStorage;

// https://learnopengl.com/Advanced-Lighting/SSAO
// https://github.com/JoeyDeVries/LearnOpenGL/blob/master/src/5.advanced_lighting/9.ssao/ssao.cpp#L166

public class SSAOPass {
   public static final int KERNEL_SIZE = 64;

   private final SSAOProgram program;

   private final Random random;

   private int kernelSamplesBuf;
   private int noiseTexture;

   public SSAOPass() {
      this.random = new Random();

      try {
         this.program = new SSAOProgram();
      } catch (IOException e) {
         throw new IllegalStateException("Couldn't load SSAO program", e);
      }

      initKernelSamples();
      initNoiseTexture();
   }

   public void destroy() {
      glDeleteBuffers(kernelSamplesBuf);
      glDeleteTextures(noiseTexture);
   }

   private void initKernelSamples() {
      FloatBuffer kernelSamples = BufferUtils.createFloatBuffer(KERNEL_SIZE * 4);

      Vector3f sample = new Vector3f();

      for (int i = 0; i < KERNEL_SIZE; i++) {
         sample.x = random.nextFloat();
         sample.y = random.nextFloat();
         sample.z = random.nextFloat();

         sample.normalize();

         // todo

         kernelSamples.put(sample.x);
         kernelSamples.put(sample.y);
         kernelSamples.put(sample.z);
         kernelSamples.put(0); // pad
      }

      kernelSamples.flip();

      kernelSamplesBuf = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, kernelSamplesBuf);
      glBufferStorage(GL_UNIFORM_BUFFER, kernelSamples, 0);
   }

   private void initNoiseTexture() {
      FloatBuffer randomVectors = BufferUtils.createFloatBuffer(16 * 3);

      for (int i = 0; i < 16; i++) {
         float vecX = random.nextFloat() * 2.0f - 1.0f;
         float vecY = random.nextFloat() * 2.0f - 1.0f;
         float vecZ = random.nextFloat() * 2.0f - 1.0f;

         randomVectors.put(vecX);
         randomVectors.put(vecY);
         randomVectors.put(vecZ);
      }

      this.noiseTexture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, noiseTexture);
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, randomVectors);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
   }

   public void run(GBuffer gBuffer, Matrix4f camera) {
      try (MemoryStack stack = MemoryStack.stackPush()) { // todo is using stack efficient ? how does it work ?
         glUseProgram(program.getProgramName());

         glBindFramebuffer(GL_FRAMEBUFFER, gBuffer.getFramebuffer());

         glDisable(GL_DEPTH_TEST);
         glDisable(GL_ALPHA_TEST);
         glDisable(GL_CULL_FACE);

         // gbuffer position
         glUniform1i(SSAOProgram.UNIFORM_GBUFFER_POSITION, 0);
         glActiveTexture(GL_TEXTURE0);
         glBindTexture(GL_TEXTURE_2D, gBuffer.getPositionTexture());

         // gbuffer normal
         glUniform1i(SSAOProgram.UNIFORM_GBUFFER_NORMAL, 1);
         glActiveTexture(GL_TEXTURE0 + 1);
         glBindTexture(GL_TEXTURE_2D, gBuffer.getNormalTexture());

         // camera
         FloatBuffer cameraBuf = stack.callocFloat(16);
         camera.get(cameraBuf);
         glUniformMatrix4fv(SSAOProgram.UNIFORM_CAMERA, false, cameraBuf);

         // kernel samples
         glBindBufferBase(GL_UNIFORM_BUFFER, SSAOProgram.BUFFER_BINDING_KERNEL_SAMPLES, kernelSamplesBuf);

         // noise texture
         glUniform1i(SSAOProgram.UNIFORM_NOISE_TEXTURE, 2);
         glActiveTexture(GL_TEXTURE0 + 2);
         glBindTexture(GL_TEXTURE_2D, noiseTexture);

         glBindVertexArray(GLUtil.getEmptyVao());
         glBindBuffer(GL_ARRAY_BUFFER, 0);
         glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

         glDrawArrays(GL_TRIANGLES, 0, 6);
      }
   }
}
