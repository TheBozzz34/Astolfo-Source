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

   public EntityFX(World var1, double var2, double var4, double var6) {
      super(var1);
      this.setSize(0.2F, 0.2F);
      this.setPosition(var2, var4, var6);
      this.lastTickPosX = this.prevPosX = var2;
      this.lastTickPosY = this.prevPosY = var4;
      this.lastTickPosZ = this.prevPosZ = var6;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.particleAge = 0;
   }

   public EntityFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.motionX = var8 + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionY = var10 + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.motionZ = var12 + (Math.random() * 2.0 - 1.0) * 0.4F;
      float var14 = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
      float var15 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / var15 * var14 * 0.4F;
      this.motionY = this.motionY / var15 * var14 * 0.4F + 0.1F;
      this.motionZ = this.motionZ / var15 * var14 * 0.4F;
   }

   public EntityFX multiplyVelocity(float var1) {
      this.motionX *= var1;
      this.motionY = (this.motionY - 0.1F) * var1 + 0.1F;
      this.motionZ *= var1;
      return this;
   }

   public EntityFX multipleParticleScaleBy(float var1) {
      this.setSize(0.2F * var1, 0.2F * var1);
      this.particleScale *= var1;
      return this;
   }

   public void setRBGColorF(float var1, float var2, float var3) {
      this.particleRed = var1;
      this.particleGreen = var2;
      this.particleBlue = var3;
   }

   public void setAlphaF(float var1) {
      if (this.particleAlpha == 1.0F && var1 < 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
      } else if (this.particleAlpha < 1.0F && var1 == 1.0F) {
         Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
      }

      this.particleAlpha = var1;
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

   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var12 = this.particleTextureIndexX / 16.0F;
      float var13 = var12 + 0.0624375F;
      float var14 = this.particleTextureIndexY / 16.0F;
      float var15 = var14 + 0.0624375F;
      float var16 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         var12 = this.particleIcon.getMinU();
         var13 = this.particleIcon.getMaxU();
         var14 = this.particleIcon.getMinV();
         var15 = this.particleIcon.getMaxV();
      }

      float var17 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var18 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
      float var19 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
      int var20 = this.getBrightnessForRender(var3);
      int var21 = var20 >> 16 & 65535;
      int var22 = var20 & 65535;
      var1.pos(var17 - var4 * var16 - var7 * var16, var18 - var5 * var16, var19 - var6 * var16 - var8 * var16)
         .tex(var13, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 - var4 * var16 + var7 * var16, var18 + var5 * var16, var19 - var6 * var16 + var8 * var16)
         .tex(var13, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 + var4 * var16 + var7 * var16, var18 + var5 * var16, var19 + var6 * var16 + var8 * var16)
         .tex(var12, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 + var4 * var16 - var7 * var16, var18 - var5 * var16, var19 + var6 * var16 - var8 * var16)
         .tex(var12, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var21, var22)
         .endVertex();
   }

   public int getFXLayer() {
      return 0;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   public void setParticleIcon(TextureAtlasSprite var1) {
      int var5 = this.getFXLayer();
      if (var5 == 1) {
         this.particleIcon = var1;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void setParticleTextureIndex(int var1) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      }

      this.particleTextureIndexX = var1 % 16;
      this.particleTextureIndexY = var1 / 16;
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
