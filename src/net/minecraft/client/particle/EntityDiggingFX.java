package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
   private IBlockState sourceState;
   private BlockPos sourcePos;

   public EntityDiggingFX(
      World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.sourceState = state;
      this.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
      this.particleGravity = state.getBlock().blockParticleGravity;
      this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
      this.particleScale /= 2.0F;
   }

   public EntityDiggingFX setBlockPos(BlockPos pos) {
      this.sourcePos = pos;
      if (this.sourceState.getBlock() == Blocks.grass) {
         return this;
      }

      int i = this.sourceState.getBlock().colorMultiplier(this.worldObj, pos);
      this.particleRed *= (i >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (i >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (i & 0xFF) / 255.0F;
      return this;
   }

   public EntityDiggingFX func_174845_l() {
      this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
      Block block = this.sourceState.getBlock();
      if (block == Blocks.grass) {
         return this;
      }

      int i = block.getRenderColor(this.sourceState);
      this.particleRed *= (i >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (i >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (i & 0xFF) / 255.0F;
      return this;
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
      float f = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float g = f + 0.015609375F;
      float h = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float f2 = h + 0.015609375F;
      float f3 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         g = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         h = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         f2 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float f4 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f5 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f6 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      worldRendererIn.pos(f4 - rotationX * f3 - rotationXY * f3, f5 - rotationZ * f3, f6 - rotationYZ * f3 - rotationXZ * f3)
         .tex(f, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 - rotationX * f3 + rotationXY * f3, f5 + rotationZ * f3, f6 - rotationYZ * f3 + rotationXZ * f3)
         .tex(f, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 + rotationX * f3 + rotationXY * f3, f5 + rotationZ * f3, f6 + rotationYZ * f3 + rotationXZ * f3)
         .tex(g, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 + rotationX * f3 - rotationXY * f3, f5 - rotationZ * f3, f6 + rotationYZ * f3 - rotationXZ * f3)
         .tex(g, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(j, k)
         .endVertex();
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      int i = super.getBrightnessForRender(partialTicks);
      int j = 0;
      if (this.worldObj.isBlockLoaded(this.sourcePos)) {
         j = this.worldObj.getCombinedLight(this.sourcePos, 0);
      }

      return i == 0 ? j : i;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityDiggingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Block.getStateById(ints[0])).func_174845_l();
      }
   }
}
