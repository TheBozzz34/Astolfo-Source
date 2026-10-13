package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import shadersmod.client.ShadersTex;

public class TextureCompass extends TextureAtlasSprite {
   public double currentAngle;
   public double angleDelta;
   public static String locationSprite;
   private static final long d = 23185956586156L;

   public TextureCompass(String var1) {
      super(var1);
      locationSprite = var1;
   }

   @Override
   public void updateAnimation() {
      Minecraft var4 = Minecraft.getMinecraft();
      if (var4.theWorld != null && var4.thePlayer != null) {
         this.updateCompass(var4.theWorld, var4.thePlayer.posX, var4.thePlayer.posZ, var4.thePlayer.rotationYaw, false, false);
      } else {
         this.updateCompass((World)null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public void updateCompass(World var1, double var2, double var4, double var6, boolean var8, boolean var9) {
      if (!this.framesTextureData.isEmpty()) {
         double var13 = 0.0;
         if (var1 != null && !var8) {
            BlockPos var15 = var1.getSpawnPoint();
            double var16 = var15.getX() - var2;
            double var18 = var15.getZ() - var4;
            var6 %= 360.0;
            var13 = -((var6 - 90.0) * Math.PI / 180.0 - Math.atan2(var18, var16));
            if (!var1.provider.isSurfaceWorld()) {
               var13 = Math.random() * Math.PI * 2.0;
            }
         }

         if (var9) {
            this.currentAngle = var13;
         } else {
            double var21 = var13 - this.currentAngle;

            while (var21 < -Math.PI) {
               var21 += Math.PI * 2;
            }

            while (var21 >= Math.PI) {
               var21 -= Math.PI * 2;
            }

            var21 = MathHelper.clamp_double(var21, -1.0, 1.0);
            this.angleDelta += var21 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle = this.currentAngle + this.angleDelta;
         }

         int var23 = (int)((this.currentAngle / (Math.PI * 2) + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (var23 < 0) {
            var23 = (var23 + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (var23 != this.frameCounter) {
            this.frameCounter = var23;
            if (Config.isShaders()) {
               ShadersTex.uploadTexSub(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            } else {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
         }
      }
   }
}
