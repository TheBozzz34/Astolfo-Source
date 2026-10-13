package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework$StarterFX extends EntityFX {
   private int fireworkAge;
   private final EffectRenderer theEffectRenderer;
   private NBTTagList fireworkExplosions;
   public boolean twinkle;
   private static final long ab = 33939280585200L;

   public EntityFirework$StarterFX(
      World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14, NBTTagCompound var15
   ) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.theEffectRenderer = var14;
      this.particleMaxAge = 8;
      if (var15 != null) {
         this.fireworkExplosions = var15.getTagList("Explosions", 10);
         if (this.fireworkExplosions.tagCount() == 0) {
            this.fireworkExplosions = null;
         } else {
            this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

            for (int var19 = 0; var19 < this.fireworkExplosions.tagCount(); var19++) {
               NBTTagCompound var20 = this.fireworkExplosions.getCompoundTagAt(var19);
               if (var20.getBoolean("Flicker")) {
                  this.twinkle = true;
                  this.particleMaxAge += 15;
                  break;
               }
            }
         }
      }
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   @Override
   public void onUpdate() {
      if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
         boolean var4 = this.func_92037_i();
         boolean var5 = false;
         if (this.fireworkExplosions.tagCount() >= 3) {
            var5 = true;
         } else {
            for (int var6 = 0; var6 < this.fireworkExplosions.tagCount(); var6++) {
               NBTTagCompound var7 = this.fireworkExplosions.getCompoundTagAt(var6);
               if (var7.getByte("Type") == 1) {
                  var5 = true;
                  break;
               }
            }
         }

         String var20 = "fireworks." + (var5 ? "largeBlast" : "blast") + (var4 ? "_far" : "");
         this.worldObj.playSound(this.posX, this.posY, this.posZ, var20, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
      }

      if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
         int var16 = this.fireworkAge / 2;
         NBTTagCompound var18 = this.fireworkExplosions.getCompoundTagAt(var16);
         byte var21 = var18.getByte("Type");
         boolean var22 = var18.getBoolean("Trail");
         boolean var8 = var18.getBoolean("Flicker");
         int[] var9 = var18.getIntArray("Colors");
         int[] var10 = var18.getIntArray("FadeColors");
         if (var9.length == 0) {
            var9 = new int[]{ItemDye.dyeColors[0]};
         }

         if (var21 == 1) {
            this.createBall(0.5, 4, var9, var10, var22, var8);
         } else if (var21 == 2) {
            this.createShaped(
               0.5,
               new double[][]{
                  {0.0, 1.0},
                  {0.3455, 0.309},
                  {0.9511, 0.309},
                  {0.3795918367346939, -0.12653061224489795},
                  {0.6122448979591837, -0.8040816326530612},
                  {0.0, -0.35918367346938773}
               },
               var9,
               var10,
               var22,
               var8,
               false
            );
         } else if (var21 == 3) {
            this.createShaped(
               0.5,
               new double[][]{
                  {0.0, 0.2},
                  {0.2, 0.2},
                  {0.2, 0.6},
                  {0.6, 0.6},
                  {0.6, 0.2},
                  {0.2, 0.2},
                  {0.2, 0.0},
                  {0.4, 0.0},
                  {0.4, -0.6},
                  {0.2, -0.6},
                  {0.2, -0.4},
                  {0.0, -0.4}
               },
               var9,
               var10,
               var22,
               var8,
               true
            );
         } else if (var21 == 4) {
            this.createBurst(var9, var10, var22, var8);
         } else {
            this.createBall(0.25, 2, var9, var10, var22, var8);
         }

         int var11 = var9[0];
         float var12 = ((var11 & 0xFF0000) >> 16) / 255.0F;
         float var13 = ((var11 & 0xFF00) >> 8) / 255.0F;
         float var14 = ((var11 & 0xFF) >> 0) / 255.0F;
         EntityFirework$OverlayFX var15 = new EntityFirework$OverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
         var15.setRBGColorF(var12, var13, var14);
         this.theEffectRenderer.addEffect(var15);
      }

      this.fireworkAge++;
      if (this.fireworkAge > this.particleMaxAge) {
         if (this.twinkle) {
            boolean var17 = this.func_92037_i();
            String var19 = "fireworks." + (var17 ? "twinkle_far" : "twinkle");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, var19, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
         }

         this.setDead();
      }
   }

   private boolean func_92037_i() {
      Minecraft var4 = Minecraft.getMinecraft();
      return var4 == null || var4.getRenderViewEntity() == null || var4.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
   }

   private void createParticle(
      double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16
   ) {
      EntityFirework$SparkFX var20 = new EntityFirework$SparkFX(this.worldObj, var1, var3, var5, var7, var9, var11, this.theEffectRenderer);
      var20.setAlphaF(0.99F);
      var20.setTrail(var15);
      var20.setTwinkle(var16);
      int var21 = this.rand.nextInt(var13.length);
      var20.setColour(var13[var21]);
      if (var14 != null && var14.length > 0) {
         var20.setFadeColour(var14[this.rand.nextInt(var14.length)]);
      }

      this.theEffectRenderer.addEffect(var20);
   }

   private void createBall(double var1, int var3, int[] var4, int[] var5, boolean var6, boolean var7) {
      double var11 = this.posX;
      double var13 = this.posY;
      double var15 = this.posZ;

      for (int var17 = -var3; var17 <= var3; var17++) {
         for (int var18 = -var3; var18 <= var3; var18++) {
            for (int var19 = -var3; var19 <= var3; var19++) {
               double var20 = var18 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var22 = var17 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var24 = var19 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var26 = MathHelper.sqrt_double(var20 * var20 + var22 * var22 + var24 * var24) / var1 + this.rand.nextGaussian() * 0.05;
               this.createParticle(var11, var13, var15, var20 / var26, var22 / var26, var24 / var26, var4, var5, var6, var7);
               if (var17 != -var3 && var17 != var3 && var18 != -var3 && var18 != var3) {
                  var19 += var3 * 2 - 1;
               }
            }
         }
      }
   }

   private void createShaped(double var1, double[][] var3, int[] var4, int[] var5, boolean var6, boolean var7, boolean var8) {
      double var12 = var3[0][0];
      double var14 = var3[0][1];
      this.createParticle(this.posX, this.posY, this.posZ, var12 * var1, var14 * var1, 0.0, var4, var5, var6, var7);
      float var16 = this.rand.nextFloat() * (float) Math.PI;
      double var17 = var8 ? 0.034 : 0.34;

      for (int var19 = 0; var19 < 3; var19++) {
         double var20 = var16 + var19 * (float) Math.PI * var17;
         double var22 = var12;
         double var24 = var14;

         for (int var26 = 1; var26 < var3.length; var26++) {
            double var27 = var3[var26][0];
            double var29 = var3[var26][1];

            for (double var31 = 0.25; var31 <= 1.0; var31 += 0.25) {
               double var33 = (var22 + (var27 - var22) * var31) * var1;
               double var35 = (var24 + (var29 - var24) * var31) * var1;
               double var37 = var33 * Math.sin(var20);
               var33 *= Math.cos(var20);

               for (double var39 = -1.0; var39 <= 1.0; var39 += 2.0) {
                  this.createParticle(this.posX, this.posY, this.posZ, var33 * var39, var35, var37 * var39, var4, var5, var6, var7);
               }
            }

            var22 = var27;
            var24 = var29;
         }
      }
   }

   private void createBurst(int[] var1, int[] var2, boolean var3, boolean var4) {
      double var8 = this.rand.nextGaussian() * 0.05;
      double var10 = this.rand.nextGaussian() * 0.05;

      for (int var12 = 0; var12 < 70; var12++) {
         double var13 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + var8;
         double var15 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + var10;
         double var17 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
         this.createParticle(this.posX, this.posY, this.posZ, var13, var17, var15, var1, var2, var3, var4);
      }
   }

   @Override
   public int getFXLayer() {
      return 0;
   }
}
