package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {
   private int lightningState;
   public long boltVertex;
   private int boltLivingTime;
   private static final long ab = 87380344513601L;

   public EntityLightningBolt(World var1, double var2, double var4, double var6) {
      super(var1);
      this.setLocationAndAngles(var2, var4, var6, 0.0F, 0.0F);
      this.lightningState = 2;
      this.boltVertex = this.rand.nextLong();
      this.boltLivingTime = this.rand.nextInt(3) + 1;
      BlockPos var12 = new BlockPos(this);
      if (!var1.isRemote
         && var1.getGameRules().getBoolean("doFireTick")
         && (var1.getDifficulty() == EnumDifficulty.NORMAL || var1.getDifficulty() == EnumDifficulty.HARD)
         && var1.isAreaLoaded(var12, 10)) {
         if (var1.getBlockState(var12).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(var1, var12)) {
            var1.setBlockState(var12, Blocks.fire.getDefaultState());
         }

         for (int var13 = 0; var13 < 4; var13++) {
            BlockPos var14 = var12.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
            if (var1.getBlockState(var14).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(var1, var14)) {
               var1.setBlockState(var14, Blocks.fire.getDefaultState());
            }
         }
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.lightningState == 2) {
         this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
         this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
      }

      this.lightningState--;
      if (this.lightningState < 0) {
         if (this.boltLivingTime == 0) {
            this.setDead();
         } else if (this.lightningState < -this.rand.nextInt(10)) {
            this.boltLivingTime--;
            this.lightningState = 1;
            this.boltVertex = this.rand.nextLong();
            BlockPos var4 = new BlockPos(this);
            if (!this.worldObj.isRemote
               && this.worldObj.getGameRules().getBoolean("doFireTick")
               && this.worldObj.isAreaLoaded(var4, 10)
               && this.worldObj.getBlockState(var4).getBlock().getMaterial() == Material.air
               && Blocks.fire.canPlaceBlockAt(this.worldObj, var4)) {
               this.worldObj.setBlockState(var4, Blocks.fire.getDefaultState());
            }
         }
      }

      if (this.lightningState >= 0) {
         if (this.worldObj.isRemote) {
            this.worldObj.setLastLightningBolt(2);
         } else {
            double var9 = 3.0;
            List var6 = this.worldObj
               .getEntitiesWithinAABBExcludingEntity(
                  this, new AxisAlignedBB(this.posX - var9, this.posY - var9, this.posZ - var9, this.posX + var9, this.posY + 6.0 + var9, this.posZ + var9)
               );

            for (int var7 = 0; var7 < var6.size(); var7++) {
               Entity var8 = (Entity)var6.get(var7);
               var8.onStruckByLightning(this);
            }
         }
      }
   }

   @Override
   public void entityInit() {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }
}
