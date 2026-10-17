package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite texture;

   public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn) {
      super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
      this.texture = textureIn;
      this.remapQuad();
   }

   private void remapQuad() {
      for (int i = 0; i < 4; i++) {
         this.remapVert(i);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void remapVert(int vertex) {
      int i = this.vertexData.length / 4;
      int j = i * vertex;
      float f = Float.intBitsToFloat(this.vertexData[j]);
      float g = Float.intBitsToFloat(this.vertexData[j + 1]);
      float h = Float.intBitsToFloat(this.vertexData[j + 2]);
      float f2 = 0.0F;
      float f3 = 0.0F;
      switch (this.face) {
         case DOWN:
            f2 = f * 16.0F;
            f3 = (1.0F - h) * 16.0F;
            break;
         case UP:
            f2 = f * 16.0F;
            f3 = h * 16.0F;
            break;
         case NORTH:
            f2 = (1.0F - f) * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case SOUTH:
            f2 = f * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case WEST:
            f2 = h * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case EAST:
            f2 = (1.0F - h) * 16.0F;
            f3 = (1.0F - g) * 16.0F;
      }

      this.vertexData[j + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(f2));
      this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(f3));
   }
}
