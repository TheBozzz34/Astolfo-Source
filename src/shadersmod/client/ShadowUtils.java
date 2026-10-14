package shadersmod.client;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class ShadowUtils {
   private static final long a = 111988001170235L;
   private static final long b = 446684746336960528L;

   public static Iterator<RenderChunk> Q(WorldClient worldClient, double d, Entity entity, int i, ViewFrustum viewFrustum) {
      float f = Shaders.getShadowRenderDistance();
      if (f > 0.0F && f < (i - 1) * (int)b) {
         int j = MathHelper.ceiling_float_int(f / 16.0F) + 1;
         float g = worldClient.getCelestialAngleRadians((float)d);
         float h = Shaders.sunPathRotation * (float) (Math.PI / 180.0);
         float f2 = g > (float) (Math.PI / 2) && g < (float) (Math.PI * 3.0 / 2.0) ? g + (float) Math.PI : g;
         float f3 = -MathHelper.sin(f2);
         float f4 = MathHelper.cos(f2) * MathHelper.cos(h);
         float f5 = -MathHelper.cos(f2) * MathHelper.sin(h);
         BlockPos blockPos = new BlockPos(
            MathHelper.floor_double(entity.posX) >> 4, MathHelper.floor_double(entity.posY) >> 4, MathHelper.floor_double(entity.posZ) >> 4
         );
         BlockPos blockPos2 = blockPos.add(-f3 * j, -f4 * j, -f5 * j);
         BlockPos blockPos3 = blockPos.add(f3 * i, f4 * i, f5 * i);
         return new IteratorRenderChunks(viewFrustum, blockPos2, blockPos3, j, j);
      } else {
         List list = Arrays.asList(viewFrustum.renderChunks);
         return list.iterator();
      }
   }
}
