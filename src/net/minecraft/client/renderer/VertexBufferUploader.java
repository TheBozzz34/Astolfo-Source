package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader {
   private VertexBuffer vertexBuffer = null;

   @Override
   public void draw(WorldRenderer worldRenderer) {
      worldRenderer.reset();
      this.vertexBuffer.bufferData(worldRenderer.getByteBuffer());
   }

   public void setVertexBuffer(VertexBuffer vertexBufferIn) {
      this.vertexBuffer = vertexBufferIn;
   }
}
