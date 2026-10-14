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

   public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
      this.initialized = true;
      this.renderChunks.clear();
      this.viewEntityX = viewEntityXIn;
      this.viewEntityY = viewEntityYIn;
      this.viewEntityZ = viewEntityZIn;
   }

   public void preRenderChunk(RenderChunk renderChunkIn) {
      BlockPos blockPos = renderChunkIn.getPosition();
      GlStateManager.translate((float)(blockPos.getX() - this.viewEntityX), (float)(blockPos.getY() - this.viewEntityY), (float)(blockPos.getZ() - this.viewEntityZ));
   }

   public void addRenderChunk(RenderChunk renderChunkIn, EnumWorldBlockLayer layer) {
      this.renderChunks.add(renderChunkIn);
   }

   public abstract void renderChunkLayer(EnumWorldBlockLayer layer);
}
