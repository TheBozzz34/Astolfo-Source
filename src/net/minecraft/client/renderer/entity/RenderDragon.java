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

   public RenderDragon(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelDragon(0.0F), 0.5F);
      this.addLayer(new LayerEnderDragonEyes(this));
      this.addLayer(new LayerEnderDragonDeath());
   }

   public void rotateCorpse(EntityDragon bat, float g, float h, float partialTicks) {
      float f = (float)bat.getMovementOffsets(7, partialTicks)[0];
      float f2 = (float)(bat.getMovementOffsets(5, partialTicks)[1] - bat.getMovementOffsets(10, partialTicks)[1]);
      GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, 1.0F);
      if (bat.deathTime > 0) {
         float f3 = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f3 = MathHelper.sqrt_float(f3);
         if (f3 > 1.0F) {
            f3 = 1.0F;
         }

         GlStateManager.rotate(f3 * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
      }
   }

   public void renderModel(EntityDragon entitylivingbaseIn, float g, float h, float f2, float f3, float f4, float scaleFactor) {
      long l = e ^ 91337012075079L;
      if (entitylivingbaseIn.deathTicks > 0) {
         float f = entitylivingbaseIn.deathTicks / 200.0F;
         GlStateManager.depthFunc(515);
         GlStateManager.enableAlpha();
         long m = 8773314378204344674L ^ l;
         GlStateManager.alphaFunc(516, f);
         this.bindTexture(enderDragonExplodingTextures);
         m = 3566960171468376034L ^ l;
         this.mainModel.render(entitylivingbaseIn, g, h, f2, f3, f4, scaleFactor);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthFunc(514);
      }

      this.bindEntityTexture(entitylivingbaseIn);
      this.mainModel.render(entitylivingbaseIn, g, h, f2, f3, f4, scaleFactor);
      if (entitylivingbaseIn.hurtTime > 0) {
         GlStateManager.depthFunc(514);
         GlStateManager.disableTexture2D();
         long n = 170345369150231529L ^ l;
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
         n = 3229095188248009408L ^ l;
         this.mainModel.render(entitylivingbaseIn, g, h, f2, f3, f4, scaleFactor);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.depthFunc(515);
      }
   }

   public void doRender(EntityDragon entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BossStatus.setBossStatus(entity, false);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (entity.healingEnderCrystal != null) {
         this.drawRechargeRay(entity, x, y, z, partialTicks);
      }
   }

   public void drawRechargeRay(EntityDragon dragon, double d, double d2, double d3, float g) {
      long l = e ^ 53083357347742L;
      float f = dragon.healingEnderCrystal.innerRotation + g;
      float h = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      h = (h * h + h) * 0.2F;
      float f2 = (float)(dragon.healingEnderCrystal.posX - dragon.posX - (dragon.prevPosX - dragon.posX) * (1.0F - g));
      float f3 = (float)(h + dragon.healingEnderCrystal.posY - 1.0 - dragon.posY - (dragon.prevPosY - dragon.posY) * (1.0F - g));
      float f4 = (float)(dragon.healingEnderCrystal.posZ - dragon.posZ - (dragon.prevPosZ - dragon.posZ) * (1.0F - g));
      float f5 = MathHelper.sqrt_float(f2 * f2 + f4 * f4);
      float f6 = MathHelper.sqrt_float(f2 * f2 + f3 * f3 + f4 * f4);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)d, (float)d2 + 2.0F, (float)d3);
      GlStateManager.rotate((float)(-Math.atan2(f4, f2)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(-Math.atan2(f5, f3)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableCull();
      this.bindTexture(enderDragonCrystalBeamTextures);
      GlStateManager.shadeModel(7425);
      float f7 = 0.0F - (dragon.ticksExisted + g) * 0.01F;
      float f8 = MathHelper.sqrt_float(f2 * f2 + f3 * f3 + f4 * f4) / 32.0F - (dragon.ticksExisted + g) * 0.01F;
      worldRenderer.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);

      for (int i = 0; i <= 8; i++) {
         float f9 = MathHelper.sin(i % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float f10 = MathHelper.cos(i % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float f11 = i % 8 * 1.0F / 8.0F;
         worldRenderer.pos(f9 * 0.2F, f10 * 0.2F, 0.0).tex(f11, f8).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(f9, f10, f6).tex(f11, f7).color(255, 255, 255, 255).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableCull();
      long m = 6277333321990017845L ^ l;
      GlStateManager.shadeModel(7424);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public ResourceLocation getEntityTexture(EntityDragon entity) {
      return enderDragonTextures;
   }
}
