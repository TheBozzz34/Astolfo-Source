package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersRender;

public class VboRenderList extends ChunkRenderContainer {
   private static final long c = 79065654443946L;

   @Override
   public void renderChunkLayer(EnumWorldBlockLayer layer) {
      if (this.initialized) {
         for (RenderChunk renderChunk : this.renderChunks) {
            VertexBuffer vertexBuffer = renderChunk.getVertexBufferByLayer(layer.ordinal());
            GlStateManager.pushMatrix();
            this.preRenderChunk(renderChunk);
            renderChunk.multModelviewMatrix();
            vertexBuffer.bindBuffer();
            this.setupArrayPointers();
            vertexBuffer.drawArrays(7);
            GlStateManager.popMatrix();
         }

         OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   private void setupArrayPointers() {
      if (Config.isShaders()) {
         ShadersRender.setupArrayPointersVbo();
      } else {
         GL11.glVertexPointer(3, 5126, 28, 0L);
         GL11.glColorPointer(4, 5121, 28, 12L);
         GL11.glTexCoordPointer(2, 5126, 28, 16L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glTexCoordPointer(2, 5122, 28, 24L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      }
   }
}
