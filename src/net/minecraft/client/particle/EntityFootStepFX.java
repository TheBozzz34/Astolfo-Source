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

   public EntityFootStepFX(TextureManager currentFootStepsIn, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.currentFootSteps = currentFootStepsIn;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.footstepMaxAge = 200;
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
      float f = (this.footstepAge + partialTicks) / this.footstepMaxAge;
      f *= f;
      float g = 2.0F - f * 2.0F;
      if (g > 1.0F) {
         g = 1.0F;
      }

      g *= 0.2F;
      GlStateManager.disableLighting();
      float h = (float)(this.posX - interpPosX);
      float f2 = (float)(this.posY - interpPosY);
      float f3 = (float)(this.posZ - interpPosZ);
      float f4 = this.worldObj.getLightBrightness(new BlockPos(this));
      this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRendererIn.pos(h - 0.125F, f2, f3 + 0.125F).tex(0.0, 1.0).color(f4, f4, f4, g).endVertex();
      worldRendererIn.pos(h + 0.125F, f2, f3 + 0.125F).tex(1.0, 1.0).color(f4, f4, f4, g).endVertex();
      worldRendererIn.pos(h + 0.125F, f2, f3 - 0.125F).tex(1.0, 0.0).color(f4, f4, f4, g).endVertex();
      worldRendererIn.pos(h - 0.125F, f2, f3 - 0.125F).tex(0.0, 0.0).color(f4, f4, f4, g).endVertex();
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
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
