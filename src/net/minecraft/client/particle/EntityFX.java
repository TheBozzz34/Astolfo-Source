package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
   public int particleTextureIndexX;
   public int particleTextureIndexY;
   public float particleTextureJitterX;
   public float particleTextureJitterY;
   public int particleAge;
   public int particleMaxAge;
   public float particleScale;
   public float particleGravity;
   public float particleRed;
   public float particleGreen;
   public float particleBlue;
   public float particleAlpha = 1.0F;
   public TextureAtlasSprite particleIcon;
   public static double interpPosX;
   public static double interpPosY;
   public static double interpPosZ;
   private static final long bb = 19032685061255L;

   public EntityFX(World worldIn, double posXIn, double posYIn, double posZIn) {
      super(worldIn);
      this.setSize(0.2F, 0.2F);
      this.setPosition(posXIn, posYIn, posZIn);
      this.lastTickPosX = this.prevPosX = posXIn;
      this.lastTickPosY = this.prevPosY = posYIn;
      this.lastTickPosZ = this.prevPosZ = posZIn;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.particleAge = 0;
   }

   public EntityFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionY = ySpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionZ = zSpeedIn + (Math.random() * 2.0 - 1.0) * 0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
      float g = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / g * f * 0.4F;
      this.motionY = this.motionY / g * f * 0.4F + 0.1F;
      this.motionZ = this.motionZ / g * f * 0.4F;
   }

   public EntityFX multiplyVelocity(float multiplier) {
      this.motionX *= multiplier;
      this.motionY = (this.motionY - 0.1F) * multiplier + 0.1F;
      this.motionZ *= multiplier;
      return this;
   }

   public EntityFX multipleParticleScaleBy(float scale) {
      this.setSize(0.2F * scale, 0.2F * scale);
      this.particleScale *= scale;
      return this;
   }

   public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
      this.particleRed = particleRedIn;
      this.particleGreen = particleGreenIn;
      this.particleBlue = particleBlueIn;
   }

   public void setAlphaF(float alpha) {
      if (this.particleAlpha == 1.0F && alpha < 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
      } else if (this.particleAlpha < 1.0F && alpha == 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
      }

      this.particleAlpha = alpha;
   }

   public float getRedColorF() {
      return this.particleRed;
   }

   public float getGreenColorF() {
      return this.particleGreen;
   }

   public float getBlueColorF() {
      return this.particleBlue;
   }

   public float getAlpha() {
      return this.particleAlpha;
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.motionY = this.motionY - 0.04 * this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

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
      float f = this.particleTextureIndexX / 16.0F;
      float g = f + 0.0624375F;
      float h = this.particleTextureIndexY / 16.0F;
      float f2 = h + 0.0624375F;
      float f3 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getMinU();
         g = this.particleIcon.getMaxU();
         h = this.particleIcon.getMinV();
         f2 = this.particleIcon.getMaxV();
      }

      float f4 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f5 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f6 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      worldRendererIn.pos(f4 - rotationX * f3 - rotationXY * f3, f5 - rotationZ * f3, f6 - rotationYZ * f3 - rotationXZ * f3)
         .tex(g, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 - rotationX * f3 + rotationXY * f3, f5 + rotationZ * f3, f6 - rotationYZ * f3 + rotationXZ * f3)
         .tex(g, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 + rotationX * f3 + rotationXY * f3, f5 + rotationZ * f3, f6 + rotationYZ * f3 + rotationXZ * f3)
         .tex(f, h)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
      worldRendererIn.pos(f4 + rotationX * f3 - rotationXY * f3, f5 - rotationZ * f3, f6 + rotationYZ * f3 - rotationXZ * f3)
         .tex(f, f2)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(j, k)
         .endVertex();
   }

   public int getFXLayer() {
      return 0;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   public void setParticleIcon(TextureAtlasSprite icon) {
      int i = this.getFXLayer();
      if (i == 1) {
         this.particleIcon = icon;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      }

      this.particleTextureIndexX = particleTextureIndex % 16;
      this.particleTextureIndexY = particleTextureIndex / 16;
   }

   public void nextTextureIndexX() {
      this.particleTextureIndexX++;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName()
         + ", Pos ("
         + this.posX
         + ","
         + this.posY
         + ","
         + this.posZ
         + "), RGBA ("
         + this.particleRed
         + ","
         + this.particleGreen
         + ","
         + this.particleBlue
         + ","
         + this.particleAlpha
         + "), Age "
         + this.particleAge;
   }
}
