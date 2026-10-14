package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import shadersmod.client.ShadersTex;

public class TextureClock extends TextureAtlasSprite {
   private double currentAngle;
   private double angleDelta;
   private static final long g = 134292956062522L;

   public TextureClock(String iconName) {
      super(iconName);
   }

   @Override
   public void updateAnimation() {
      if (!this.framesTextureData.isEmpty()) {
         Minecraft minecraft = Minecraft.getMinecraft();
         double d = 0.0;
         if (minecraft.theWorld != null && minecraft.thePlayer != null) {
            d = minecraft.theWorld.getCelestialAngle(1.0F);
            if (!minecraft.theWorld.provider.isSurfaceWorld()) {
               d = Math.random();
            }
         }

         double e = d - this.currentAngle;

         while (e < -0.5) {
            e++;
         }

         while (e >= 0.5) {
            e--;
         }

         e = MathHelper.clamp_double(e, -1.0, 1.0);
         this.angleDelta += e * 0.1;
         this.angleDelta *= 0.8;
         this.currentAngle = this.currentAngle + this.angleDelta;
         int i = (int)((this.currentAngle + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (i < 0) {
            i = (i + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (i != this.frameCounter) {
            this.frameCounter = i;
            if (Config.isShaders()) {
               ShadersTex.uploadTexSub(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            } else {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
         }
      }
   }
}
