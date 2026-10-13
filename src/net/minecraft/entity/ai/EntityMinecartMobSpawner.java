package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
   private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
      @Override
      public void func_98267_a(int var1) {
         EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte)var1);
      }

      @Override
      public World getSpawnerWorld() {
         return EntityMinecartMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World var1) {
      super(var1);
   }

   public EntityMinecartMobSpawner(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.SPAWNER;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.mob_spawner.getDefaultState();
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.mobSpawnerLogic.readFromNBT(var1);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      this.mobSpawnerLogic.writeToNBT(var1);
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      this.mobSpawnerLogic.setDelayToMin(var1);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      this.mobSpawnerLogic.updateSpawner();
   }

   public MobSpawnerBaseLogic func_98039_d() {
      return this.mobSpawnerLogic;
   }
}
