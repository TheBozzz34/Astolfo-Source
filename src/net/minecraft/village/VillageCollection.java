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

   public VillageCollection(String var1) {
      super(var1);
   }

   public VillageCollection(World var1) {
      super(fileNameForProvider(var1.provider));
      this.worldObj = var1;
      this.markDirty();
   }

   public void setWorldsForAll(World var1) {
      this.worldObj = var1;

      for (Village var6 : this.villageList) {
         var6.setWorld(var1);
      }
   }

   public void addToVillagerPositionList(BlockPos var1) {
      if (this.villagerPositionsList.size() <= 64 && !this.positionInList(var1)) {
         this.villagerPositionsList.add(var1);
      }
   }

   public void tick() {
      this.tickCounter++;

      for (Village var5 : this.villageList) {
         var5.tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }
   }

   private void removeAnnihilatedVillages() {
      Iterator var4 = this.villageList.iterator();

      while (var4.hasNext()) {
         Village var5 = (Village)var4.next();
         if (var5.isAnnihilated()) {
            var4.remove();
            this.markDirty();
         }
      }
   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos var1, int var2) {
      Village var6 = null;
      double var7 = Float.MAX_VALUE;

      for (Village var10 : this.villageList) {
         double var11 = var10.getCenter().distanceSq(var1);
         if (var11 < var7) {
            float var13 = var2 + var10.getVillageRadius();
            if (var11 <= var13 * var13) {
               var6 = var10;
               var7 = var11;
            }
         }
      }

      return var6;
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for (int var4 = 0; var4 < this.newDoors.size(); var4++) {
         VillageDoorInfo var5 = this.newDoors.get(var4);
         Village var6 = this.getNearestVillage(var5.getDoorBlockPos(), 32);
         if (var6 == null) {
            var6 = new Village(this.worldObj);
            this.villageList.add(var6);
            this.markDirty();
         }

         var6.addVillageDoorInfo(var5);
      }

      this.newDoors.clear();
   }

   private void addDoorsAround(BlockPos var1) {
      byte var5 = 16;
      byte var6 = 4;
      byte var7 = 16;

      for (int var8 = -var5; var8 < var5; var8++) {
         for (int var9 = -var6; var9 < var6; var9++) {
            for (int var10 = -var7; var10 < var7; var10++) {
               BlockPos var11 = var1.add(var8, var9, var10);
               if (this.isWoodDoor(var11)) {
                  VillageDoorInfo var12 = this.checkDoorExistence(var11);
                  if (var12 == null) {
                     this.addToNewDoorsList(var11);
                  } else {
                     var12.func_179849_a(this.tickCounter);
                  }
               }
            }
         }
      }
   }

   private VillageDoorInfo checkDoorExistence(BlockPos var1) {
      for (VillageDoorInfo var6 : this.newDoors) {
         if (var6.getDoorBlockPos().getX() == var1.getX()
            && var6.getDoorBlockPos().getZ() == var1.getZ()
            && Math.abs(var6.getDoorBlockPos().getY() - var1.getY()) <= 1) {
            return var6;
         }
      }

      for (Village var9 : this.villageList) {
         VillageDoorInfo var7 = var9.getExistedDoor(var1);
         if (var7 != null) {
            return var7;
         }
      }

      return null;
   }

   private void addToNewDoorsList(BlockPos var1) {
      EnumFacing var4 = BlockDoor.getFacing(this.worldObj, var1);
      EnumFacing var5 = var4.getOpposite();
      int var6 = this.countBlocksCanSeeSky(var1, var4, 5);
      int var7 = this.countBlocksCanSeeSky(var1, var5, var6 + 1);
      if (var6 != var7) {
         this.newDoors.add(new VillageDoorInfo(var1, var6 < var7 ? var4 : var5, this.tickCounter));
      }
   }

   private int countBlocksCanSeeSky(BlockPos var1, EnumFacing var2, int var3) {
      int var7 = 0;

      for (int var8 = 1; var8 <= 5; var8++) {
         if (this.worldObj.canSeeSky(var1.offset(var2, var8))) {
            if (++var7 >= var3) {
               return var7;
            }
         }
      }

      return var7;
   }

   private boolean positionInList(BlockPos var1) {
      for (BlockPos var6 : this.villagerPositionsList) {
         if (var6.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private boolean isWoodDoor(BlockPos var1) {
      Block var5 = this.worldObj.getBlockState(var1).getBlock();
      return var5 instanceof BlockDoor ? var5.getMaterial() == Material.wood : false;
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      this.tickCounter = var1.getInteger("Tick");
      NBTTagList var5 = var1.getTagList("Villages", 10);

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         Village var8 = new Village();
         var8.readVillageDataFromNBT(var7);
         this.villageList.add(var8);
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      var1.setInteger("Tick", this.tickCounter);
      NBTTagList var5 = new NBTTagList();

      for (Village var7 : this.villageList) {
         NBTTagCompound var8 = new NBTTagCompound();
         var7.writeVillageDataToNBT(var8);
         var5.appendTag(var8);
      }

      var1.setTag("Villages", var5);
   }

   public static String fileNameForProvider(WorldProvider var0) {
      return "villages" + var0.getInternalNameSuffix();
   }
}
