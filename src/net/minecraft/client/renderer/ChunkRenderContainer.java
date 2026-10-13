package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;

public abstract class ChunkRenderContainer {
   private double viewEntityX;
   private double viewEntityY;
   private double viewEntityZ;
   public List<RenderChunk> renderChunks;
   public boolean initialized;
   private static final long a = 16741837276567L;
   private static final long d = 6196764624096871440L;

   public ChunkRenderContainer() {
      this.renderChunks = Lists.newArrayListWithCapacity((int)d);
   }

   public void initialize(double var1, double var3, double var5) {
      this.initialized = true;
      this.renderChunks.clear();
      this.viewEntityX = var1;
      this.viewEntityY = var3;
      this.viewEntityZ = var5;
   }

   public void preRenderChunk(RenderChunk var1) {
      BlockPos var2 = var1.getPosition();
      GlStateManager.translate((float)(var2.getX() - this.viewEntityX), (float)(var2.getY() - this.viewEntityY), (float)(var2.getZ() - this.viewEntityZ));
   }

   public void addRenderChunk(RenderChunk var1, EnumWorldBlockLayer var2) {
      this.renderChunks.add(var1);
   }

   public abstract void renderChunkLayer(EnumWorldBlockLayer var1);
}
