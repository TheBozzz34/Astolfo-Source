package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader {
   private VertexBuffer vertexBuffer = null;

   @Override
   public void draw(WorldRenderer var1) {
      var1.reset();
      this.vertexBuffer.bufferData(var1.getByteBuffer());
   }

   public void setVertexBuffer(VertexBuffer var1) {
      this.vertexBuffer = var1;
   }
}
