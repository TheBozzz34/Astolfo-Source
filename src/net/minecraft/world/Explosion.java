package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
   private final boolean isFlaming;
   private final boolean isSmoking;
   private final Random explosionRNG = new Random();
   private final World worldObj;
   private final double explosionX;
   private final double explosionY;
   private final double explosionZ;
   private final Entity exploder;
   private final float explosionSize;
   private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
   private final Map<EntityPlayer, Vec3> playerKnockbackMap = Maps.newHashMap();
   private static final long a = 117867496254259L;

   public Explosion(World var1, Entity var2, double var3, double var5, double var7, float var9, List<BlockPos> var10) {
      this(var1, var2, var3, var5, var7, var9, false, true, var10);
   }

   public Explosion(World var1, Entity var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11, List<BlockPos> var12) {
      this(var1, var2, var3, var5, var7, var9, var10, var11);
      this.affectedBlockPositions.addAll(var12);
   }

   public Explosion(World var1, Entity var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11) {
      this.worldObj = var1;
      this.exploder = var2;
      this.explosionSize = var9;
      this.explosionX = var3;
      this.explosionY = var5;
      this.explosionZ = var7;
      this.isFlaming = var10;
      this.isSmoking = var11;
   }

   public void doExplosionA() {
      HashSet var4 = Sets.newHashSet();

      for (int var6 = 0; var6 < 16; var6++) {
         for (int var7 = 0; var7 < 16; var7++) {
            for (int var8 = 0; var8 < 16; var8++) {
               if (var6 == 0 || var6 == 15 || var7 == 0 || var7 == 15 || var8 == 0 || var8 == 15) {
                  double var9 = var6 / 15.0F * 2.0F - 1.0F;
                  double var11 = var7 / 15.0F * 2.0F - 1.0F;
                  double var13 = var8 / 15.0F * 2.0F - 1.0F;
                  double var15 = Math.sqrt(var9 * var9 + var11 * var11 + var13 * var13);
                  var9 /= var15;
                  var11 /= var15;
                  var13 /= var15;
                  float var17 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  double var18 = this.explosionX;
                  double var20 = this.explosionY;
                  double var22 = this.explosionZ;

                  while (var17 > 0.0F) {
                     BlockPos var25 = new BlockPos(var18, var20, var22);
                     IBlockState var26 = this.worldObj.getBlockState(var25);
                     if (var26.getBlock().getMaterial() != Material.air) {
                        float var27 = this.exploder != null
                           ? this.exploder.getExplosionResistance(this, this.worldObj, var25, var26)
                           : var26.getBlock().getExplosionResistance((Entity)null);
                        var17 -= (var27 + 0.3F) * 0.3F;
                     }

                     if (var17 > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, var25, var26, var17))) {
                        var4.add(var25);
                     }

                     var18 += var9 * 0.3F;
                     var20 += var11 * 0.3F;
                     var22 += var13 * 0.3F;
                     var17 -= 0.22500001F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(var4);
      float var33 = this.explosionSize * 2.0F;
      int var34 = MathHelper.floor_double(this.explosionX - var33 - 1.0);
      int var35 = MathHelper.floor_double(this.explosionX + var33 + 1.0);
      int var37 = MathHelper.floor_double(this.explosionY - var33 - 1.0);
      int var10 = MathHelper.floor_double(this.explosionY + var33 + 1.0);
      int var39 = MathHelper.floor_double(this.explosionZ - var33 - 1.0);
      int var12 = MathHelper.floor_double(this.explosionZ + var33 + 1.0);
      List var41 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(var34, var37, var39, var35, var10, var12));
      Vec3 var14 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

      for (int var42 = 0; var42 < var41.size(); var42++) {
         Entity var16 = (Entity)var41.get(var42);
         if (!var16.isImmuneToExplosions()) {
            double var43 = var16.getDistance(this.explosionX, this.explosionY, this.explosionZ) / var33;
            if (var43 <= 1.0) {
               double var19 = var16.posX - this.explosionX;
               double var21 = var16.posY + var16.getEyeHeight() - this.explosionY;
               double var23 = var16.posZ - this.explosionZ;
               double var47 = MathHelper.sqrt_double(var19 * var19 + var21 * var21 + var23 * var23);
               if (var47 != 0.0) {
                  var19 /= var47;
                  var21 /= var47;
                  var23 /= var47;
                  double var48 = this.worldObj.getBlockDensity(var14, var16.getEntityBoundingBox());
                  double var29 = (1.0 - var43) * var48;
                  var16.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((var29 * var29 + var29) / 2.0 * 8.0 * var33 + 1.0));
                  double var31 = EnchantmentProtection.func_92092_a(var16, var29);
                  var16.motionX += var19 * var31;
                  var16.motionY += var21 * var31;
                  var16.motionZ += var23 * var31;
                  if (var16 instanceof EntityPlayer && !((EntityPlayer)var16).capabilities.disableDamage) {
                     this.playerKnockbackMap.put((EntityPlayer)var16, new Vec3(var19 * var29, var21 * var29, var23 * var29));
                  }
               }
            }
         }
      }
   }

   public void doExplosionB(boolean var1) {
      this.worldObj
         .playSoundEffect(
            this.explosionX,
            this.explosionY,
            this.explosionZ,
            "random.explode",
            4.0F,
            (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
         );
      if (this.explosionSize >= 2.0F && this.isSmoking) {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      } else {
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      }

      if (this.isSmoking) {
         for (BlockPos var6 : this.affectedBlockPositions) {
            Block var7 = this.worldObj.getBlockState(var6).getBlock();
            if (var1) {
               double var8 = var6.getX() + this.worldObj.rand.nextFloat();
               double var10 = var6.getY() + this.worldObj.rand.nextFloat();
               double var12 = var6.getZ() + this.worldObj.rand.nextFloat();
               double var14 = var8 - this.explosionX;
               double var16 = var10 - this.explosionY;
               double var18 = var12 - this.explosionZ;
               double var20 = MathHelper.sqrt_double(var14 * var14 + var16 * var16 + var18 * var18);
               var14 /= var20;
               var16 /= var20;
               var18 /= var20;
               double var22 = 0.5 / (var20 / this.explosionSize + 0.1);
               var22 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
               var14 *= var22;
               var16 *= var22;
               var18 *= var22;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.EXPLOSION_NORMAL,
                     (var8 + this.explosionX * 1.0) / 2.0,
                     (var10 + this.explosionY * 1.0) / 2.0,
                     (var12 + this.explosionZ * 1.0) / 2.0,
                     var14,
                     var16,
                     var18
                  );
               this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var8, var10, var12, var14, var16, var18);
            }

            if (var7.getMaterial() != Material.air) {
               if (var7.canDropFromExplosion(this)) {
                  var7.dropBlockAsItemWithChance(this.worldObj, var6, this.worldObj.getBlockState(var6), 1.0F / this.explosionSize, 0);
               }

               this.worldObj.setBlockState(var6, Blocks.air.getDefaultState(), 3);
               var7.onBlockDestroyedByExplosion(this.worldObj, var6, this);
            }
         }
      }

      if (this.isFlaming) {
         for (BlockPos var25 : this.affectedBlockPositions) {
            if (this.worldObj.getBlockState(var25).getBlock().getMaterial() == Material.air
               && this.worldObj.getBlockState(var25.down()).getBlock().isFullBlock()
               && this.explosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlockState(var25, Blocks.fire.getDefaultState());
            }
         }
      }
   }

   public Map<EntityPlayer, Vec3> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   public EntityLivingBase getExplosivePlacedBy() {
      return this.exploder == null
         ? null
         : (
            this.exploder instanceof EntityTNTPrimed
               ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy()
               : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null)
         );
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
