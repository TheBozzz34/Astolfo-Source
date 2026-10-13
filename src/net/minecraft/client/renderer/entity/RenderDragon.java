package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonDeath;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonEyes;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderDragon extends RenderLiving<EntityDragon> {
   private static final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
   private static final ResourceLocation enderDragonExplodingTextures = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   public ModelDragon modelDragon = (ModelDragon)this.mainModel;
   private static final long e = 31232384825370L;

   public RenderDragon(RenderManager var1) {
      super(var1, new ModelDragon(0.0F), 0.5F);
      this.addLayer(new LayerEnderDragonEyes(this));
      this.addLayer(new LayerEnderDragonDeath());
   }

   public void rotateCorpse(EntityDragon var1, float var2, float var3, float var4) {
      float var8 = (float)var1.getMovementOffsets(7, var4)[0];
      float var9 = (float)(var1.getMovementOffsets(5, var4)[1] - var1.getMovementOffsets(10, var4)[1]);
      GlStateManager.rotate(-var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var9 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, 1.0F);
      if (var1.deathTime > 0) {
         float var10 = (var1.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
         var10 = MathHelper.sqrt_float(var10);
         if (var10 > 1.0F) {
            var10 = 1.0F;
         }

         GlStateManager.rotate(var10 * this.getDeathMaxRotation(var1), 0.0F, 0.0F, 1.0F);
      }
   }

   public void renderModel(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      long var8 = e ^ 91337012075079L;
      if (var1.deathTicks > 0) {
         float var11 = var1.deathTicks / 200.0F;
         GlStateManager.depthFunc(515);
         GlStateManager.enableAlpha();
         long var10000 = 8773314378204344674L ^ var8;
         GlStateManager.alphaFunc(516, var11);
         this.bindTexture(enderDragonExplodingTextures);
         var10000 = 3566960171468376034L ^ var8;
         this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthFunc(514);
      }

      this.bindEntityTexture(var1);
      this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
      if (var1.hurtTime > 0) {
         GlStateManager.depthFunc(514);
         GlStateManager.disableTexture2D();
         long var13 = 170345369150231529L ^ var8;
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
         var13 = 3229095188248009408L ^ var8;
         this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.depthFunc(515);
      }
   }

   public void doRender(EntityDragon var1, double var2, double var4, double var6, float var8, float var9) {
      BossStatus.setBossStatus(var1, false);
      super.doRender(var1, var2, var4, var6, var8, var9);
      if (var1.healingEnderCrystal != null) {
         this.drawRechargeRay(var1, var2, var4, var6, var9);
      }
   }

   public void drawRechargeRay(EntityDragon var1, double var2, double var4, double var6, float var8) {
      long var9 = e ^ 53083357347742L;
      float var12 = var1.healingEnderCrystal.innerRotation + var8;
      float var13 = MathHelper.sin(var12 * 0.2F) / 2.0F + 0.5F;
      var13 = (var13 * var13 + var13) * 0.2F;
      float var14 = (float)(var1.healingEnderCrystal.posX - var1.posX - (var1.prevPosX - var1.posX) * (1.0F - var8));
      float var15 = (float)(var13 + var1.healingEnderCrystal.posY - 1.0 - var1.posY - (var1.prevPosY - var1.posY) * (1.0F - var8));
      float var16 = (float)(var1.healingEnderCrystal.posZ - var1.posZ - (var1.prevPosZ - var1.posZ) * (1.0F - var8));
      float var17 = MathHelper.sqrt_float(var14 * var14 + var16 * var16);
      float var18 = MathHelper.sqrt_float(var14 * var14 + var15 * var15 + var16 * var16);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4 + 2.0F, (float)var6);
      GlStateManager.rotate((float)(-Math.atan2(var16, var14)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(-Math.atan2(var17, var15)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
      Tessellator var19 = Tessellator.getInstance();
      WorldRenderer var20 = var19.getWorldRenderer();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableCull();
      this.bindTexture(enderDragonCrystalBeamTextures);
      GlStateManager.shadeModel(7425);
      float var21 = 0.0F - (var1.ticksExisted + var8) * 0.01F;
      float var22 = MathHelper.sqrt_float(var14 * var14 + var15 * var15 + var16 * var16) / 32.0F - (var1.ticksExisted + var8) * 0.01F;
      var20.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);

      for (int var24 = 0; var24 <= 8; var24++) {
         float var25 = MathHelper.sin(var24 % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float var26 = MathHelper.cos(var24 % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float var27 = var24 % 8 * 1.0F / 8.0F;
         var20.pos(var25 * 0.2F, var26 * 0.2F, 0.0).tex(var27, var22).color(0, 0, 0, 255).endVertex();
         var20.pos(var25, var26, var18).tex(var27, var21).color(255, 255, 255, 255).endVertex();
      }

      var19.draw();
      GlStateManager.enableCull();
      long var10000 = 6277333321990017845L ^ var9;
      GlStateManager.shadeModel(7424);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public ResourceLocation getEntityTexture(EntityDragon var1) {
      return enderDragonTextures;
   }
}
