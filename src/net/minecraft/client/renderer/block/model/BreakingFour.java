package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite texture;
   private static final long b = 3504162992722L;

   public BreakingFour(BakedQuad var1, TextureAtlasSprite var2) {
      super(Arrays.copyOf(var1.getVertexData(), var1.getVertexData().length), var1.tintIndex, FaceBakery.getFacingFromVertexData(var1.getVertexData()));
      this.texture = var2;
      this.remapQuad();
   }

   private void remapQuad() {
      for (int var4 = 0; var4 < 4; var4++) {
         this.remapVert(var4);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void remapVert(int var1) {
      int var5 = this.u.length / 4;
      int var6 = var5 * var1;
      float var7 = Float.intBitsToFloat(this.u[var6]);
      float var8 = Float.intBitsToFloat(this.u[var6 + 1]);
      float var9 = Float.intBitsToFloat(this.u[var6 + 2]);
      float var10 = 0.0F;
      float var11 = 0.0F;
      switch (this.face) {
         case DOWN:
            var10 = var7 * 16.0F;
            var11 = (1.0F - var9) * 16.0F;
            break;
         case UP:
            var10 = var7 * 16.0F;
            var11 = var9 * 16.0F;
            break;
         case NORTH:
            var10 = (1.0F - var7) * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case SOUTH:
            var10 = var7 * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case WEST:
            var10 = var9 * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case EAST:
            var10 = (1.0F - var9) * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
      }

      this.u[var6 + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(var10));
      this.u[var6 + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(var11));
   }
}
