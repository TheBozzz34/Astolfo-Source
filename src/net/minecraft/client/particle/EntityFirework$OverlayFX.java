package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework$OverlayFX extends EntityFX {
   public EntityFirework$OverlayFX(World world, double d, double e, double d2) {
      super(world, d, e, d2);
      this.particleMaxAge = 4;
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
      float f = 7.1F * MathHelper.sin((this.particleAge + partialTicks - 1.0F) * 0.25F * (float) Math.PI);
      this.particleAlpha = 0.6F - (this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
      float g = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float h = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f2 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      worldRendererIn.pos(g - rotationX * f - rotationXY * f, h - rotationZ * f, f2 - rotationYZ * f - rotationXZ * f)
         .tex(0.5, 0.375)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(g - rotationX * f + rotationXY * f, h + rotationZ * f, f2 - rotationYZ * f + rotationXZ * f)
         .tex(0.5, 0.125)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(g + rotationX * f + rotationXY * f, h + rotationZ * f, f2 + rotationYZ * f + rotationXZ * f)
         .tex(0.25, 0.125)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(g + rotationX * f - rotationXY * f, h - rotationZ * f, f2 + rotationYZ * f - rotationXZ * f)
         .tex(0.25, 0.375)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
   }
}
