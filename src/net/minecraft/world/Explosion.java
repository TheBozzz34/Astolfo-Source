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

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
   }

   public Explosion(
      World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking, List<BlockPos> affectedPositions
   ) {
      this(worldIn, entityIn, x, y, z, size, flaming, smoking);
      this.affectedBlockPositions.addAll(affectedPositions);
   }

   public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking) {
      this.worldObj = worldIn;
      this.exploder = entityIn;
      this.explosionSize = size;
      this.explosionX = x;
      this.explosionY = y;
      this.explosionZ = z;
      this.isFlaming = flaming;
      this.isSmoking = smoking;
   }

   public void doExplosionA() {
      HashSet hashSet = Sets.newHashSet();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                  double d = i / 15.0F * 2.0F - 1.0F;
                  double e = j / 15.0F * 2.0F - 1.0F;
                  double d2 = k / 15.0F * 2.0F - 1.0F;
                  double d3 = Math.sqrt(d * d + e * e + d2 * d2);
                  d /= d3;
                  e /= d3;
                  d2 /= d3;
                  float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  double d4 = this.explosionX;
                  double d5 = this.explosionY;
                  double d6 = this.explosionZ;

                  while (f > 0.0F) {
                     BlockPos blockPos = new BlockPos(d4, d5, d6);
                     IBlockState iBlockState = this.worldObj.getBlockState(blockPos);
                     if (iBlockState.getBlock().getMaterial() != Material.air) {
                        float g = this.exploder != null
                           ? this.exploder.getExplosionResistance(this, this.worldObj, blockPos, iBlockState)
                           : iBlockState.getBlock().getExplosionResistance((Entity)null);
                        f -= (g + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockPos, iBlockState, f))) {
                        hashSet.add(blockPos);
                     }

                     d4 += d * 0.3F;
                     d5 += e * 0.3F;
                     d6 += d2 * 0.3F;
                     f -= 0.22500001F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(hashSet);
      float h = this.explosionSize * 2.0F;
      int l = MathHelper.floor_double(this.explosionX - h - 1.0);
      int m = MathHelper.floor_double(this.explosionX + h + 1.0);
      int n = MathHelper.floor_double(this.explosionY - h - 1.0);
      int i2 = MathHelper.floor_double(this.explosionY + h + 1.0);
      int i3 = MathHelper.floor_double(this.explosionZ - h - 1.0);
      int i4 = MathHelper.floor_double(this.explosionZ + h + 1.0);
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(l, n, i3, m, i2, i4));
      Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

      for (int i5 = 0; i5 < list.size(); i5++) {
         Entity entity = (Entity)list.get(i5);
         if (!entity.isImmuneToExplosions()) {
            double d7 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / h;
            if (d7 <= 1.0) {
               double d8 = entity.posX - this.explosionX;
               double d9 = entity.posY + entity.getEyeHeight() - this.explosionY;
               double d10 = entity.posZ - this.explosionZ;
               double d11 = MathHelper.sqrt_double(d8 * d8 + d9 * d9 + d10 * d10);
               if (d11 != 0.0) {
                  d8 /= d11;
                  d9 /= d11;
                  d10 /= d11;
                  double d12 = this.worldObj.getBlockDensity(vec3, entity.getEntityBoundingBox());
                  double d13 = (1.0 - d7) * d12;
                  entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d13 * d13 + d13) / 2.0 * 8.0 * h + 1.0));
                  double d14 = EnchantmentProtection.func_92092_a(entity, d13);
                  entity.motionX += d8 * d14;
                  entity.motionY += d9 * d14;
                  entity.motionZ += d10 * d14;
                  if (entity instanceof EntityPlayer && !((EntityPlayer)entity).capabilities.disableDamage) {
                     this.playerKnockbackMap.put((EntityPlayer)entity, new Vec3(d8 * d13, d9 * d13, d10 * d13));
                  }
               }
            }
         }
      }
   }

   public void doExplosionB(boolean spawnParticles) {
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
         for (BlockPos blockPos : this.affectedBlockPositions) {
            Block block = this.worldObj.getBlockState(blockPos).getBlock();
            if (spawnParticles) {
               double d = blockPos.getX() + this.worldObj.rand.nextFloat();
               double e = blockPos.getY() + this.worldObj.rand.nextFloat();
               double d2 = blockPos.getZ() + this.worldObj.rand.nextFloat();
               double d3 = d - this.explosionX;
               double d4 = e - this.explosionY;
               double d5 = d2 - this.explosionZ;
               double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
               d3 /= d6;
               d4 /= d6;
               d5 /= d6;
               double d7 = 0.5 / (d6 / this.explosionSize + 0.1);
               d7 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
               d3 *= d7;
               d4 *= d7;
               d5 *= d7;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.EXPLOSION_NORMAL,
                     (d + this.explosionX * 1.0) / 2.0,
                     (e + this.explosionY * 1.0) / 2.0,
                     (d2 + this.explosionZ * 1.0) / 2.0,
                     d3,
                     d4,
                     d5
                  );
               this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, e, d2, d3, d4, d5);
            }

            if (block.getMaterial() != Material.air) {
               if (block.canDropFromExplosion(this)) {
                  block.dropBlockAsItemWithChance(this.worldObj, blockPos, this.worldObj.getBlockState(blockPos), 1.0F / this.explosionSize, 0);
               }

               this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState(), 3);
               block.onBlockDestroyedByExplosion(this.worldObj, blockPos, this);
            }
         }
      }

      if (this.isFlaming) {
         for (BlockPos blockPos2 : this.affectedBlockPositions) {
            if (this.worldObj.getBlockState(blockPos2).getBlock().getMaterial() == Material.air
               && this.worldObj.getBlockState(blockPos2.down()).getBlock().isFullBlock()
               && this.explosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlockState(blockPos2, Blocks.fire.getDefaultState());
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
