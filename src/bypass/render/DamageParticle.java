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
   public String text;
   public boolean throughWalls = true;
   public boolean growing = true;
   public boolean grow;
   public float extraScale = 1.0F;
   private final float damage;
   private final float baseScale;
   private final Color healColor;
   private final Color damageColor;
   private final boolean staticParticle;

   public DamageParticle(
      int i,
      World world,
      double d,
      double e,
      double d2,
      double d3,
      double d4,
      double d5,
      float f,
      float g,
      int j,
      boolean bl,
      boolean bl2,
      boolean bl3,
      Color color,
      Color color2
   ) {
      super(world, d, e, d2, d3, d4, d5);
      this.particleTextureJitterX = 0.0F;
      this.particleTextureJitterY = 0.0F;
      this.particleGravity = f;
      this.particleScale = g;
      this.baseScale = g;
      this.grow = bl;
      this.particleMaxAge = j;
      this.staticParticle = bl2;
      this.healColor = color;
      this.damageColor = color2;
      this.damage = bl3 ? i / 2.0F : i;
      this.text = MathUtil.ONE_DECIMAL.format(Math.abs(this.damage));
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
      if (this.throughWalls) {
         GL11.glDepthFunc(519);
      } else {
         GL11.glDepthFunc(515);
      }

      GL11.glTranslatef(h, f2, f3);
      GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(g, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glScaled(this.particleScale * 0.008, this.particleScale * 0.008, this.particleScale * 0.008);
      GL11.glScaled(this.extraScale, this.extraScale, this.extraScale);
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
      int i = this.damageColor.getRGB();
      if (this.damage < 0.0F) {
         i = this.healColor.getRGB();
      }

      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
      fontRenderer.drawStringWithShadow(
         this.text, -MathHelper.floor_float(fontRenderer.getStringWidth(this.text) / 2.0F) + 1, -MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F) + 1, i
      );
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthFunc(515);
      GL11.glPopMatrix();
      if (this.grow) {
         if (this.growing) {
            this.particleScale *= 1.08F;
            if (this.particleScale > this.baseScale * 3.0) {
               this.growing = false;
            }
         } else {
            this.particleScale *= 0.96F;
         }
      }
   }

   @Override
   public void onUpdate() {
      if (this.staticParticle) {
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
