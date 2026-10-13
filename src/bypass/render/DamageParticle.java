package bypass.render;

import bypass.util.MathUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class DamageParticle extends EntityFX {
   public String oU;
   public boolean op = true;
   public boolean oh = true;
   public boolean o4;
   public float oo = 1.0F;
   private final float oC;
   private final float oH;
   private final Color oM;
   private final Color oF;
   private final boolean oW;
   private static final long ab = 99500009999976L;

   public DamageParticle(
      int var1,
      World var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      float var15,
      float var16,
      int var17,
      boolean var18,
      boolean var19,
      boolean var20,
      Color var21,
      Color var22
   ) {
      super(var2, var3, var5, var7, var9, var11, var13);
      this.particleTextureJitterX = 0.0F;
      this.particleTextureJitterY = 0.0F;
      this.particleGravity = var15;
      this.particleScale = var16;
      this.oH = var16;
      this.o4 = var18;
      this.particleMaxAge = var17;
      this.oW = var19;
      this.oM = var21;
      this.oF = var22;
      this.oC = var20 ? var1 / 2.0F : var1;
      this.oU = MathUtil.ONE_DECIMAL.format(Math.abs(this.oC));
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = ab ^ 894808457906L;
      float var13 = -Minecraft.getMinecraft().thePlayer.rotationYaw;
      float var14 = Minecraft.getMinecraft().thePlayer.rotationPitch;
      float var15 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var16 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var4 - interpPosY);
      float var17 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var5 - interpPosZ);
      GL11.glPushMatrix();
      if (this.op) {
         GL11.glDepthFunc(519);
      } else {
         GL11.glDepthFunc(515);
      }

      GL11.glTranslatef(var15, var16, var17);
      GL11.glRotatef(var13, 0.0F, 1.0F, 0.0F);
      long var10000 = 7893993772087282653L ^ var9;
      GL11.glRotatef(var14, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glScaled(this.particleScale * 0.008, this.particleScale * 0.008, this.particleScale * 0.008);
      GL11.glScaled(this.oo, this.oo, this.oo);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
      var10000 = 3742998667494746246L ^ var9;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDisable(2896);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var18 = this.oF.getRGB();
      if (this.oC < 0.0F) {
         var18 = this.oM.getRGB();
      }

      FontRenderer var19 = Minecraft.getMinecraft().fontRendererObj;
      var19.drawStringWithShadow(
         this.oU, -MathHelper.floor_float(var19.getStringWidth(this.oU) / 2.0F) + 1, -MathHelper.floor_float(var19.FONT_HEIGHT / 2.0F) + 1, var18
      );
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      var10000 = 320336853644347103L ^ var9;
      GL11.glDepthFunc(515);
      GL11.glPopMatrix();
      if (this.o4) {
         if (this.oh) {
            this.particleScale *= 1.08F;
            if (this.particleScale > this.oH * 3.0) {
               this.oh = false;
            }
         } else {
            this.particleScale *= 0.96F;
         }
      }
   }

   @Override
   public void onUpdate() {
      if (this.oW) {
         if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
         }
      } else {
         super.onUpdate();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }
}
