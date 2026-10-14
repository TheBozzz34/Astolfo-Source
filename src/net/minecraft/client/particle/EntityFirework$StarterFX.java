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
      World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer, NBTTagCompound nbtTagCompound
   ) {
      super(world, d, d2, d3, 0.0, 0.0, 0.0);
      this.motionX = d4;
      this.motionY = d5;
      this.motionZ = d6;
      this.theEffectRenderer = effectRenderer;
      this.particleMaxAge = 8;
      if (nbtTagCompound != null) {
         this.fireworkExplosions = nbtTagCompound.getTagList("Explosions", 10);
         if (this.fireworkExplosions.tagCount() == 0) {
            this.fireworkExplosions = null;
         } else {
            this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

            for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
               NBTTagCompound nbtTagCompound2 = this.fireworkExplosions.getCompoundTagAt(i);
               if (nbtTagCompound2.getBoolean("Flicker")) {
                  this.twinkle = true;
                  this.particleMaxAge += 15;
                  break;
               }
            }
         }
      }
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
   }

   @Override
   public void onUpdate() {
      if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
         boolean bl = this.func_92037_i();
         boolean bl2 = false;
         if (this.fireworkExplosions.tagCount() >= 3) {
            bl2 = true;
         } else {
            for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
               NBTTagCompound nbtTagCompound = this.fireworkExplosions.getCompoundTagAt(i);
               if (nbtTagCompound.getByte("Type") == 1) {
                  bl2 = true;
                  break;
               }
            }
         }

         String string = "fireworks." + (bl2 ? "largeBlast" : "blast") + (bl ? "_far" : "");
         this.worldObj.playSound(this.posX, this.posY, this.posZ, string, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
      }

      if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
         int j = this.fireworkAge / 2;
         NBTTagCompound nbtTagCompound2 = this.fireworkExplosions.getCompoundTagAt(j);
         byte b = nbtTagCompound2.getByte("Type");
         boolean bl3 = nbtTagCompound2.getBoolean("Trail");
         boolean bl4 = nbtTagCompound2.getBoolean("Flicker");
         int[] ints = nbtTagCompound2.getIntArray("Colors");
         int[] ints2 = nbtTagCompound2.getIntArray("FadeColors");
         if (ints.length == 0) {
            ints = new int[]{ItemDye.dyeColors[0]};
         }

         if (b == 1) {
            this.createBall(0.5, 4, ints, ints2, bl3, bl4);
         } else if (b == 2) {
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
               ints,
               ints2,
               bl3,
               bl4,
               false
            );
         } else if (b == 3) {
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
               ints,
               ints2,
               bl3,
               bl4,
               true
            );
         } else if (b == 4) {
            this.createBurst(ints, ints2, bl3, bl4);
         } else {
            this.createBall(0.25, 2, ints, ints2, bl3, bl4);
         }

         int k = ints[0];
         float f = ((k & 0xFF0000) >> 16) / 255.0F;
         float g = ((k & 0xFF00) >> 8) / 255.0F;
         float h = ((k & 0xFF) >> 0) / 255.0F;
         EntityFirework$OverlayFX entityFirework$OverlayFX = new EntityFirework$OverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
         entityFirework$OverlayFX.setRBGColorF(f, g, h);
         this.theEffectRenderer.addEffect(entityFirework$OverlayFX);
      }

      this.fireworkAge++;
      if (this.fireworkAge > this.particleMaxAge) {
         if (this.twinkle) {
            boolean bl5 = this.func_92037_i();
            String string2 = "fireworks." + (bl5 ? "twinkle_far" : "twinkle");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, string2, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
         }

         this.setDead();
      }
   }

   private boolean func_92037_i() {
      Minecraft minecraft = Minecraft.getMinecraft();
      return minecraft == null || minecraft.getRenderViewEntity() == null || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
   }

   private void createParticle(double d, double d2, double d3, double d4, double d5, double d6, int[] ints, int[] ints2, boolean bl, boolean bl2) {
      EntityFirework$SparkFX entityFirework$SparkFX = new EntityFirework$SparkFX(this.worldObj, d, d2, d3, d4, d5, d6, this.theEffectRenderer);
      entityFirework$SparkFX.setAlphaF(0.99F);
      entityFirework$SparkFX.setTrail(bl);
      entityFirework$SparkFX.setTwinkle(bl2);
      int i = this.rand.nextInt(ints.length);
      entityFirework$SparkFX.setColour(ints[i]);
      if (ints2 != null && ints2.length > 0) {
         entityFirework$SparkFX.setFadeColour(ints2[this.rand.nextInt(ints2.length)]);
      }

      this.theEffectRenderer.addEffect(entityFirework$SparkFX);
   }

   private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
      double d = this.posX;
      double e = this.posY;
      double d2 = this.posZ;

      for (int i = -size; i <= size; i++) {
         for (int j = -size; j <= size; j++) {
            for (int k = -size; k <= size; k++) {
               double d3 = j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double d4 = i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double d5 = k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5) / speed + this.rand.nextGaussian() * 0.05;
               this.createParticle(d, e, d2, d3 / d6, d4 / d6, d5 / d6, colours, fadeColours, trail, twinkleIn);
               if (i != -size && i != size && j != -size && j != size) {
                  k += size * 2 - 1;
               }
            }
         }
      }
   }

   private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean bl) {
      double d = shape[0][0];
      double e = shape[0][1];
      this.createParticle(this.posX, this.posY, this.posZ, d * speed, e * speed, 0.0, colours, fadeColours, trail, twinkleIn);
      float f = this.rand.nextFloat() * (float) Math.PI;
      double d2 = bl ? 0.034 : 0.34;

      for (int i = 0; i < 3; i++) {
         double d3 = f + i * (float) Math.PI * d2;
         double d4 = d;
         double d5 = e;

         for (int j = 1; j < shape.length; j++) {
            double d6 = shape[j][0];
            double d7 = shape[j][1];

            for (double d8 = 0.25; d8 <= 1.0; d8 += 0.25) {
               double d9 = (d4 + (d6 - d4) * d8) * speed;
               double d10 = (d5 + (d7 - d5) * d8) * speed;
               double d11 = d9 * Math.sin(d3);
               d9 *= Math.cos(d3);

               for (double d12 = -1.0; d12 <= 1.0; d12 += 2.0) {
                  this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkleIn);
               }
            }

            d4 = d6;
            d5 = d7;
         }
      }
   }

   private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
      double d = this.rand.nextGaussian() * 0.05;
      double e = this.rand.nextGaussian() * 0.05;

      for (int i = 0; i < 70; i++) {
         double d2 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + d;
         double d3 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + e;
         double d4 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
         this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, colours, fadeColours, trail, twinkleIn);
      }
   }

   @Override
   public int getFXLayer() {
      return 0;
   }
}
