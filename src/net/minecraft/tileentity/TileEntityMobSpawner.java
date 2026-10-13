package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   private final MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
      private static final long c = 28445267118455L;

      @Override
      public void func_98267_a(int var1) {
         TileEntityMobSpawner.this.worldObj.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.mob_spawner, var1, 0);
      }

      @Override
      public World getSpawnerWorld() {
         return TileEntityMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return TileEntityMobSpawner.this.pos;
      }

      @Override
      public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart var1) {
         super.setRandomEntity(var1);
         if (this.getSpawnerWorld() != null) {
            this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.pos);
         }
      }
   };
   private static final long a = 101494246377218L;

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      this.spawnerLogic.readFromNBT(var1);
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      this.spawnerLogic.writeToNBT(var1);
   }

   @Override
   public void update() {
      this.spawnerLogic.updateSpawner();
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound var3 = new NBTTagCompound();
      this.writeToNBT(var3);
      var3.removeTag("SpawnPotentials");
      return new S35PacketUpdateTileEntity(this.pos, 1, var3);
   }

   @Override
   public boolean receiveClientEvent(int var1, int var2) {
      return this.spawnerLogic.setDelayToMin(var1) ? true : super.receiveClientEvent(var1, var2);
   }

   @Override
   public boolean func_183000_F() {
      return true;
   }

   public MobSpawnerBaseLogic getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }
}
