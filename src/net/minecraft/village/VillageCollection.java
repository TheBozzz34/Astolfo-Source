package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World worldObj;
   private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
   private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
   private final List<Village> villageList = Lists.newArrayList();
   private int tickCounter;
   private static final long a = 94111019860747L;

   public VillageCollection(String name) {
      super(name);
   }

   public VillageCollection(World worldIn) {
      super(fileNameForProvider(worldIn.provider));
      this.worldObj = worldIn;
      this.markDirty();
   }

   public void setWorldsForAll(World worldIn) {
      this.worldObj = worldIn;

      for (Village village : this.villageList) {
         village.setWorld(worldIn);
      }
   }

   public void addToVillagerPositionList(BlockPos pos) {
      if (this.villagerPositionsList.size() <= 64 && !this.positionInList(pos)) {
         this.villagerPositionsList.add(pos);
      }
   }

   public void tick() {
      this.tickCounter++;

      for (Village village : this.villageList) {
         village.tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }
   }

   private void removeAnnihilatedVillages() {
      Iterator iterator2 = this.villageList.iterator();

      while (iterator2.hasNext()) {
         Village village = (Village)iterator2.next();
         if (village.isAnnihilated()) {
            iterator2.remove();
            this.markDirty();
         }
      }
   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos doorBlock, int radius) {
      Village village = null;
      double d = Float.MAX_VALUE;

      for (Village village2 : this.villageList) {
         double e = village2.getCenter().distanceSq(doorBlock);
         if (e < d) {
            float f = radius + village2.getVillageRadius();
            if (e <= f * f) {
               village = village2;
               d = e;
            }
         }
      }

      return village;
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for (int i = 0; i < this.newDoors.size(); i++) {
         VillageDoorInfo villageDoorInfo = this.newDoors.get(i);
         Village village = this.getNearestVillage(villageDoorInfo.getDoorBlockPos(), 32);
         if (village == null) {
            village = new Village(this.worldObj);
            this.villageList.add(village);
            this.markDirty();
         }

         village.addVillageDoorInfo(villageDoorInfo);
      }

      this.newDoors.clear();
   }

   private void addDoorsAround(BlockPos central) {
      byte b = 16;
      byte b2 = 4;
      byte b3 = 16;

      for (int i = -b; i < b; i++) {
         for (int j = -b2; j < b2; j++) {
            for (int k = -b3; k < b3; k++) {
               BlockPos blockPos = central.add(i, j, k);
               if (this.isWoodDoor(blockPos)) {
                  VillageDoorInfo villageDoorInfo = this.checkDoorExistence(blockPos);
                  if (villageDoorInfo == null) {
                     this.addToNewDoorsList(blockPos);
                  } else {
                     villageDoorInfo.func_179849_a(this.tickCounter);
                  }
               }
            }
         }
      }
   }

   private VillageDoorInfo checkDoorExistence(BlockPos doorBlock) {
      for (VillageDoorInfo villageDoorInfo : this.newDoors) {
         if (villageDoorInfo.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo;
         }
      }

      for (Village village : this.villageList) {
         VillageDoorInfo villageDoorInfo2 = village.getExistedDoor(doorBlock);
         if (villageDoorInfo2 != null) {
            return villageDoorInfo2;
         }
      }

      return null;
   }

   private void addToNewDoorsList(BlockPos doorBlock) {
      EnumFacing enumFacing = BlockDoor.getFacing(this.worldObj, doorBlock);
      EnumFacing enumFacing2 = enumFacing.getOpposite();
      int i = this.countBlocksCanSeeSky(doorBlock, enumFacing, 5);
      int j = this.countBlocksCanSeeSky(doorBlock, enumFacing2, i + 1);
      if (i != j) {
         this.newDoors.add(new VillageDoorInfo(doorBlock, i < j ? enumFacing : enumFacing2, this.tickCounter));
      }
   }

   private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation) {
      int i = 0;

      for (int j = 1; j <= 5; j++) {
         if (this.worldObj.canSeeSky(centerPos.offset(direction, j))) {
            if (++i >= limitation) {
               return i;
            }
         }
      }

      return i;
   }

   private boolean positionInList(BlockPos pos) {
      for (BlockPos blockPos : this.villagerPositionsList) {
         if (blockPos.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private boolean isWoodDoor(BlockPos doorPos) {
      Block block = this.worldObj.getBlockState(doorPos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.tickCounter = nbt.getInteger("Tick");
      NBTTagList nbtTagList = nbt.getTagList("Villages", 10);

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         Village village = new Village();
         village.readVillageDataFromNBT(nbtTagCompound);
         this.villageList.add(village);
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("Tick", this.tickCounter);
      NBTTagList nbtTagList = new NBTTagList();

      for (Village village : this.villageList) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         village.writeVillageDataToNBT(nbtTagCompound);
         nbtTagList.appendTag(nbtTagCompound);
      }

      nbt.setTag("Villages", nbtTagList);
   }

   public static String fileNameForProvider(WorldProvider provider) {
      return "villages" + provider.getInternalNameSuffix();
   }
}
