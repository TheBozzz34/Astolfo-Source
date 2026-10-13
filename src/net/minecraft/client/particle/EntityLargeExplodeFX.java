package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLargeExplodeFX extends EntityFX {
   private static final ResourceLocation EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
   private static final VertexFormat field_181549_az = new VertexFormat()
      .addElement(DefaultVertexFormats.POSITION_3F)
      .addElement(DefaultVertexFormats.TEX_2F)
      .addElement(DefaultVertexFormats.COLOR_4UB)
      .addElement(DefaultVertexFormats.TEX_2S)
      .addElement(DefaultVertexFormats.NORMAL_3B)
      .addElement(DefaultVertexFormats.PADDING_1B);
   private int field_70581_a;
   private int field_70584_aq;
   private TextureManager theRenderEngine;
   private float field_70582_as;
   private static final long ab = 84790336623627L;

   public EntityLargeExplodeFX(TextureManager var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      super(var2, var3, var5, var7, 0.0, 0.0, 0.0);
      this.theRenderEngine = var1;
      this.field_70584_aq = 6 + this.rand.nextInt(4);
      this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
      this.field_70582_as = 1.0F - (float)var9 * 0.5F;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      int var12 = (int)((this.field_70581_a + var3) * 15.0F / this.field_70584_aq);
      if (var12 <= 15) {
         this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
         float var13 = var12 % 4 / 4.0F;
         float var14 = var13 + 0.24975F;
         float var15 = var12 / 4 / 4.0F;
         float var16 = var15 + 0.24975F;
         float var17 = 2.0F * this.field_70582_as;
         float var18 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
         float var19 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
         float var20 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         RenderHelper.disableStandardItemLighting();
         var1.begin(7, field_181549_az);
         var1.pos(var18 - var4 * var17 - var7 * var17, var19 - var5 * var17, var20 - var6 * var17 - var8 * var17)
            .tex(var14, var16)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         var1.pos(var18 - var4 * var17 + var7 * var17, var19 + var5 * var17, var20 - var6 * var17 + var8 * var17)
            .tex(var14, var15)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         var1.pos(var18 + var4 * var17 + var7 * var17, var19 + var5 * var17, var20 + var6 * var17 + var8 * var17)
            .tex(var13, var15)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         var1.pos(var18 + var4 * var17 - var7 * var17, var19 - var5 * var17, var20 + var6 * var17 - var8 * var17)
            .tex(var13, var16)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableLighting();
      }
   }

   @Override
   public int getBrightnessForRender(float var1) {
      return 61680;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.field_70581_a++;
      if (this.field_70581_a == this.field_70584_aq) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityLargeExplodeFX(Minecraft.getMinecraft().getTextureManager(), var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
