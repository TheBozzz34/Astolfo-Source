package shadersmod.client;

import java.util.Iterator;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.BlockPosM;
import net.minecraft.util.BlockPos;

public class IteratorRenderChunks implements Iterator<RenderChunk> {
   private ViewFrustum viewFrustum;
   private Iterator3d iterator3d;
   private BlockPosM posBlock = new BlockPosM(0, 0, 0);

   public IteratorRenderChunks(ViewFrustum viewFrustum, BlockPos posStart, BlockPos posEnd, int width, int height) {
      this.viewFrustum = viewFrustum;
      this.iterator3d = new Iterator3d(posStart, posEnd, width, height);
   }

   @Override
   public boolean hasNext() {
      return this.iterator3d.hasNext();
   }

   public RenderChunk next() {
      BlockPos pos = this.iterator3d.next();
      this.posBlock.setXyz(pos.getX() << 4, pos.getY() << 4, pos.getZ() << 4);
      return this.viewFrustum.getRenderChunk(this.posBlock);
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not implemented");
   }
}
