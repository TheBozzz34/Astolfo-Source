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

   public DamageParticle(
      int i,
      World world,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      float f,
      float g,
      int j,
      boolean bl,
      boolean bl2,
      boolean bl3,
      Color color,
      Color color2
   ) {
      super(world, d, d2, d3, d4, d5, d6);
      this.particleTextureJitterX = 0.0F;
      this.particleTextureJitterY = 0.0F;
      this.particleGravity = f;
      this.particleScale = g;
      this.oH = g;
      this.o4 = bl;
      this.particleMaxAge = j;
      this.oW = bl2;
      this.oM = color;
      this.oF = color2;
      this.oC = bl3 ? i / 2.0F : i;
      this.oU = MathUtil.ONE_DECIMAL.format(Math.abs(this.oC));
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      float f = -Minecraft.getMinecraft().thePlayer.rotationYaw;
      float g = Minecraft.getMinecraft().thePlayer.rotationPitch;
      float h = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f2 = (float)(this.prevPosY + (this.posY - this.prevPosY) * rotationX - interpPosY);
      float f3 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * rotationZ - interpPosZ);
      GL11.glPushMatrix();
      if (this.op) {
         GL11.glDepthFunc(519);
      } else {
         GL11.glDepthFunc(515);
      }

      GL11.glTranslatef(h, f2, f3);
      GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(g, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glScaled(this.particleScale * 0.008, this.particleScale * 0.008, this.particleScale * 0.008);
      GL11.glScaled(this.oo, this.oo, this.oo);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDisable(2896);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.oF.getRGB();
      if (this.oC < 0.0F) {
         i = this.oM.getRGB();
      }

      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
      fontRenderer.drawStringWithShadow(
         this.oU, -MathHelper.floor_float(fontRenderer.getStringWidth(this.oU) / 2.0F) + 1, -MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F) + 1, i
      );
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
