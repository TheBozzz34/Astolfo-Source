package shadersmod.client;

import java.util.Iterator;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.BlockPosM;
import net.minecraft.util.BlockPos;

public class IteratorRenderChunks implements Iterator<RenderChunk> {
   private ViewFrustum q;
   private Iterator3d d;
   private BlockPosM s = new BlockPosM(0, 0, 0);
   private static final long a = 129009965012957L;

   public IteratorRenderChunks(ViewFrustum viewFrustum, BlockPos posStart, BlockPos posEnd, int width, int height) {
      this.q = viewFrustum;
      this.d = new Iterator3d(posStart, posEnd, width, height);
   }

   @Override
   public boolean hasNext() {
      return this.d.hasNext();
   }

   public RenderChunk next() {
      BlockPos pos = this.d.next();
      this.s.setXyz(pos.getX() << 4, pos.getY() << 4, pos.getZ() << 4);
      return this.q.getRenderChunk(this.s);
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not implemented");
   }
}
