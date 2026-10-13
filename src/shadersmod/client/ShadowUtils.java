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

   public static Iterator<RenderChunk> Q(WorldClient var0, double var1, Entity var3, int var4, ViewFrustum var5) {
      float var9 = Shaders.getShadowRenderDistance();
      if (var9 > 0.0F && var9 < (var4 - 1) * (int)b) {
         int var21 = MathHelper.ceiling_float_int(var9 / 16.0F) + 1;
         float var11 = var0.getCelestialAngleRadians((float)var1);
         float var12 = Shaders.sunPathRotation * (float) (Math.PI / 180.0);
         float var13 = var11 > (float) (Math.PI / 2) && var11 < (float) (Math.PI * 3.0 / 2.0) ? var11 + (float) Math.PI : var11;
         float var14 = -MathHelper.sin(var13);
         float var15 = MathHelper.cos(var13) * MathHelper.cos(var12);
         float var16 = -MathHelper.cos(var13) * MathHelper.sin(var12);
         BlockPos var17 = new BlockPos(
            MathHelper.floor_double(var3.posX) >> 4, MathHelper.floor_double(var3.posY) >> 4, MathHelper.floor_double(var3.posZ) >> 4
         );
         BlockPos var18 = var17.add(-var14 * var21, -var15 * var21, -var16 * var21);
         BlockPos var19 = var17.add(var14 * var4, var15 * var4, var16 * var4);
         return new IteratorRenderChunks(var5, var18, var19, var21, var21);
      } else {
         List var10 = Arrays.asList(var5.renderChunks);
         return var10.iterator();
      }
   }
}
