package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {
   private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
   private int footstepAge;
   private int footstepMaxAge;
   private TextureManager currentFootSteps;
   private static final long ab = 134025360371315L;

   public EntityFootStepFX(TextureManager var1, World var2, double var3, double var5, double var7) {
      super(var2, var3, var5, var7, 0.0, 0.0, 0.0);
      this.currentFootSteps = var1;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.footstepMaxAge = 200;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = ab ^ 38808343225001L;
      float var12 = (this.footstepAge + var3) / this.footstepMaxAge;
      var12 *= var12;
      float var13 = 2.0F - var12 * 2.0F;
      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      var13 *= 0.2F;
      GlStateManager.disableLighting();
      long var10000 = 6464372214510650607L ^ var9;
      float var15 = (float)(this.posX - interpPosX);
      float var16 = (float)(this.posY - interpPosY);
      float var17 = (float)(this.posZ - interpPosZ);
      float var18 = this.worldObj.getLightBrightness(new BlockPos(this));
      this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      var1.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      var1.pos(var15 - 0.125F, var16, var17 + 0.125F).tex(0.0, 1.0).color(var18, var18, var18, var13).endVertex();
      var1.pos(var15 + 0.125F, var16, var17 + 0.125F).tex(1.0, 1.0).color(var18, var18, var18, var13).endVertex();
      var1.pos(var15 + 0.125F, var16, var17 - 0.125F).tex(1.0, 0.0).color(var18, var18, var18, var13).endVertex();
      var1.pos(var15 - 0.125F, var16, var17 - 0.125F).tex(0.0, 0.0).color(var18, var18, var18, var13).endVertex();
      Tessellator.getInstance().draw();
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
   }

   @Override
   public void onUpdate() {
      this.footstepAge++;
      if (this.footstepAge == this.footstepMaxAge) {
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
         return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), var2, var3, var5, var7);
      }
   }
}
