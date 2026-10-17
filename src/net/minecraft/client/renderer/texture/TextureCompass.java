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

   public TextureCompass(String iconName) {
      super(iconName);
      locationSprite = iconName;
   }

   @Override
   public void updateAnimation() {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (minecraft.theWorld != null && minecraft.thePlayer != null) {
         this.updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, false, false);
      } else {
         this.updateCompass((World)null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public void updateCompass(World worldIn, double d, double e, double d2, boolean bl, boolean bl2) {
      if (!this.framesTextureData.isEmpty()) {
         double d3 = 0.0;
         if (worldIn != null && !bl) {
            BlockPos blockPos = worldIn.getSpawnPoint();
            double d4 = blockPos.getX() - d;
            double d5 = blockPos.getZ() - e;
            d2 %= 360.0;
            d3 = -((d2 - 90.0) * Math.PI / 180.0 - Math.atan2(d5, d4));
            if (!worldIn.provider.isSurfaceWorld()) {
               d3 = Math.random() * Math.PI * 2.0;
            }
         }

         if (bl2) {
            this.currentAngle = d3;
         } else {
            double d6 = d3 - this.currentAngle;

            while (d6 < -Math.PI) {
               d6 += Math.PI * 2;
            }

            while (d6 >= Math.PI) {
               d6 -= Math.PI * 2;
            }

            d6 = MathHelper.clamp_double(d6, -1.0, 1.0);
            this.angleDelta += d6 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle = this.currentAngle + this.angleDelta;
         }

         int i = (int)((this.currentAngle / (Math.PI * 2) + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

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
