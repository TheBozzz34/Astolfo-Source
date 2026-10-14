package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {
   public Barrier(World worldIn, double d, double d2, double d3, Item item) {
      super(worldIn, d, d2, d3, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 80;
   }

   @Override
   public int getFXLayer() {
      return 1;
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
      float f = this.particleIcon.getMinU();
      float g = this.particleIcon.getMaxU();
      float h = this.particleIcon.getMinV();
      float f2 = this.particleIcon.getMaxV();
      float f3 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f4 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f5 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      worldRendererIn.pos(f3 - rotationX * 0.5F - rotationXY * 0.5F, f4 - rotationZ * 0.5F, f5 - rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(g, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f3 - rotationX * 0.5F + rotationXY * 0.5F, f4 + rotationZ * 0.5F, f5 - rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(g, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f3 + rotationX * 0.5F + rotationXY * 0.5F, f4 + rotationZ * 0.5F, f5 + rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(f, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f3 + rotationX * 0.5F - rotationXY * 0.5F, f4 - rotationZ * 0.5F, f5 + rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(f, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new Barrier(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(Blocks.barrier));
      }
   }
}
