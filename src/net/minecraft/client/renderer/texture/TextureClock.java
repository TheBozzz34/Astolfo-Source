package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import shadersmod.client.ShadersTex;

public class TextureClock extends TextureAtlasSprite {
   private double currentAngle;
   private double angleDelta;
   private static final long g = 134292956062522L;

   public TextureClock(String var1) {
      super(var1);
   }

   @Override
   public void updateAnimation() {
      if (!this.framesTextureData.isEmpty()) {
         Minecraft var4 = Minecraft.getMinecraft();
         double var5 = 0.0;
         if (var4.theWorld != null && var4.thePlayer != null) {
            var5 = var4.theWorld.getCelestialAngle(1.0F);
            if (!var4.theWorld.provider.isSurfaceWorld()) {
               var5 = Math.random();
            }
         }

         double var7 = var5 - this.currentAngle;

         while (var7 < -0.5) {
            var7++;
         }

         while (var7 >= 0.5) {
            var7--;
         }

         var7 = MathHelper.clamp_double(var7, -1.0, 1.0);
         this.angleDelta += var7 * 0.1;
         this.angleDelta *= 0.8;
         this.currentAngle = this.currentAngle + this.angleDelta;
         int var9 = (int)((this.currentAngle + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (var9 < 0) {
            var9 = (var9 + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (var9 != this.frameCounter) {
            this.frameCounter = var9;
            if (Config.isShaders()) {
               ShadersTex.uploadTexSub(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            } else {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
         }
      }
   }
}
