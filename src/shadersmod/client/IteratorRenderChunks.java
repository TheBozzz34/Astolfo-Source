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

   public IteratorRenderChunks(ViewFrustum var1, BlockPos var2, BlockPos var3, int var4, int var5) {
      this.q = var1;
      this.d = new Iterator3d(var2, var3, var4, var5);
   }

   @Override
   public boolean hasNext() {
      return this.d.hasNext();
   }

   public RenderChunk next() {
      BlockPos var1 = this.d.next();
      this.s.setXyz(var1.getX() << 4, var1.getY() << 4, var1.getZ() << 4);
      return this.q.getRenderChunk(this.s);
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not implemented");
   }
}
