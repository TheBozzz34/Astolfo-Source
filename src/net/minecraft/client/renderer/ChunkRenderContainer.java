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
   public List<RenderChunk> renderChunks = Lists.newArrayListWithCapacity(17424);
   public boolean initialized;

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
