package net.minecraft.village;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
   private World worldObj;
   private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   private BlockPos centerHelper = BlockPos.ORIGIN;
   private BlockPos center = BlockPos.ORIGIN;
   private int villageRadius;
   private int lastAddDoorTimestamp;
   private int tickCounter;
   private int numVillagers;
   private int noBreedTicks;
   private TreeMap<String, Integer> playerReputation = new TreeMap<>();
   private List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
   private int numIronGolems;
   private static final long a = 91174852638668L;

   public Village() {
   }

   public Village(World var1) {
      this.worldObj = var1;
   }

   public void setWorld(World var1) {
      this.worldObj = var1;
   }

   public void tick(int var1) {
      this.tickCounter = var1;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (var1 % 20 == 0) {
         this.updateNumVillagers();
      }

      if (var1 % 30 == 0) {
         this.updateNumIronGolems();
      }

      int var5 = this.numVillagers / 10;
      if (this.numIronGolems < var5 && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0) {
         Vec3 var6 = this.func_179862_a(this.center, 2, 4, 2);
         if (var6 != null) {
            EntityIronGolem var7 = new EntityIronGolem(this.worldObj);
            var7.setPosition(var6.xCoord, var6.yCoord, var6.zCoord);
            this.worldObj.spawnEntityInWorld(var7);
            this.numIronGolems++;
         }
      }
   }

   private Vec3 func_179862_a(BlockPos var1, int var2, int var3, int var4) {
      for (int var8 = 0; var8 < 10; var8++) {
         BlockPos var9 = var1.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.func_179866_a(var9) && this.func_179861_a(new BlockPos(var2, var3, var4), var9)) {
            return new Vec3(var9.getX(), var9.getY(), var9.getZ());
         }
      }

      return null;
   }

   private boolean func_179861_a(BlockPos var1, BlockPos var2) {
      if (!World.doesBlockHaveSolidTopSurface(this.worldObj, var2.down())) {
         return false;
      }

      int var6 = var2.getX() - var1.getX() / 2;
      int var7 = var2.getZ() - var1.getZ() / 2;

      for (int var8 = var6; var8 < var6 + var1.getX(); var8++) {
         for (int var9 = var2.getY(); var9 < var2.getY() + var1.getY(); var9++) {
            for (int var10 = var7; var10 < var7 + var1.getZ(); var10++) {
               if (this.worldObj.getBlockState(new BlockPos(var8, var9, var10)).getBlock().isNormalCube()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void updateNumIronGolems() {
      List var1 = this.worldObj
         .getEntitiesWithinAABB(
            EntityIronGolem.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numIronGolems = var1.size();
   }

   private void updateNumVillagers() {
      List var4 = this.worldObj
         .getEntitiesWithinAABB(
            EntityVillager.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numVillagers = var4.size();
      if (this.numVillagers == 0) {
         this.playerReputation.clear();
      }
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getNumVillagers() {
      return this.numVillagers;
   }

   public boolean func_179866_a(BlockPos var1) {
      return this.center.distanceSq(var1) < this.villageRadius * this.villageRadius;
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos var1) {
      VillageDoorInfo var5 = null;
      int var6 = Integer.MAX_VALUE;

      for (VillageDoorInfo var8 : this.villageDoorInfoList) {
         int var9 = var8.getDistanceToDoorBlockSq(var1);
         if (var9 < var6) {
            var5 = var8;
            var6 = var9;
         }
      }

      return var5;
   }

   public VillageDoorInfo getDoorInfo(BlockPos var1) {
      VillageDoorInfo var5 = null;
      int var6 = Integer.MAX_VALUE;

      for (VillageDoorInfo var8 : this.villageDoorInfoList) {
         int var9 = var8.getDistanceToDoorBlockSq(var1);
         if (var9 > 256) {
            var9 *= 1000;
         } else {
            var9 = var8.getDoorOpeningRestrictionCounter();
         }

         if (var9 < var6) {
            var5 = var8;
            var6 = var9;
         }
      }

      return var5;
   }

   public VillageDoorInfo getExistedDoor(BlockPos var1) {
      if (this.center.distanceSq(var1) > this.villageRadius * this.villageRadius) {
         return null;
      }

      for (VillageDoorInfo var6 : this.villageDoorInfoList) {
         if (var6.getDoorBlockPos().getX() == var1.getX()
            && var6.getDoorBlockPos().getZ() == var1.getZ()
            && Math.abs(var6.getDoorBlockPos().getY() - var1.getY()) <= 1) {
            return var6;
         }
      }

      return null;
   }

   public void addVillageDoorInfo(VillageDoorInfo var1) {
      this.villageDoorInfoList.add(var1);
      this.centerHelper = this.centerHelper.add(var1.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = var1.getInsidePosY();
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase var1) {
      for (Village.VillageAggressor var6 : this.villageAgressors) {
         if (var6.agressor == var1) {
            var6.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(var1, this.tickCounter));
   }

   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase var1) {
      double var5 = Double.MAX_VALUE;
      Village.VillageAggressor var7 = null;

      for (int var8 = 0; var8 < this.villageAgressors.size(); var8++) {
         Village.VillageAggressor var9 = this.villageAgressors.get(var8);
         double var10 = var9.agressor.getDistanceSqToEntity(var1);
         if (var10 <= var5) {
            var7 = var9;
            var5 = var10;
         }
      }

      return var7 != null ? var7.agressor : null;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase var1) {
      double var5 = Double.MAX_VALUE;
      EntityPlayer var7 = null;

      for (String var9 : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(var9)) {
            EntityPlayer var10 = this.worldObj.getPlayerEntityByName(var9);
            if (var10 != null) {
               double var11 = var10.getDistanceSqToEntity(var1);
               if (var11 <= var5) {
                  var7 = var10;
                  var5 = var11;
               }
            }
         }
      }

      return var7;
   }

   private void removeDeadAndOldAgressors() {
      Iterator var4 = this.villageAgressors.iterator();

      while (var4.hasNext()) {
         Village.VillageAggressor var5 = (Village.VillageAggressor)var4.next();
         if (!var5.agressor.isEntityAlive() || Math.abs(this.tickCounter - var5.agressionTime) > 300) {
            var4.remove();
         }
      }
   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean var4 = false;
      boolean var5 = this.worldObj.rand.nextInt(50) == 0;
      Iterator var6 = this.villageDoorInfoList.iterator();

      while (var6.hasNext()) {
         VillageDoorInfo var7 = (VillageDoorInfo)var6.next();
         if (var5) {
            var7.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(var7.getDoorBlockPos()) || Math.abs(this.tickCounter - var7.getInsidePosY()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(var7.getDoorBlockPos());
            var4 = true;
            var7.setIsDetachedFromVillageFlag(true);
            var6.remove();
         }
      }

      if (var4) {
         this.updateVillageRadiusAndCenter();
      }
   }

   private boolean isWoodDoor(BlockPos var1) {
      Block var5 = this.worldObj.getBlockState(var1).getBlock();
      return var5 instanceof BlockDoor ? var5.getMaterial() == Material.wood : false;
   }

   private void updateVillageRadiusAndCenter() {
      int var4 = this.villageDoorInfoList.size();
      if (var4 == 0) {
         this.center = new BlockPos(0, 0, 0);
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / var4, this.centerHelper.getY() / var4, this.centerHelper.getZ() / var4);
         int var5 = 0;

         for (VillageDoorInfo var7 : this.villageDoorInfoList) {
            var5 = Math.max(var7.getDistanceToDoorBlockSq(this.center), var5);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt(var5) + 1);
      }
   }

   public int getReputationForPlayer(String var1) {
      Integer var5 = this.playerReputation.get(var1);
      return var5 != null ? var5 : 0;
   }

   public int setReputationForPlayer(String var1, int var2) {
      int var5 = this.getReputationForPlayer(var1);
      int var6 = MathHelper.clamp_int(var5 + var2, -30, 10);
      this.playerReputation.put(var1, var6);
      return var6;
   }

   public boolean isPlayerReputationTooLow(String var1) {
      return this.getReputationForPlayer(var1) <= -15;
   }

   public void readVillageDataFromNBT(NBTTagCompound var1) {
      this.numVillagers = var1.getInteger("PopSize");
      this.villageRadius = var1.getInteger("Radius");
      this.numIronGolems = var1.getInteger("Golems");
      this.lastAddDoorTimestamp = var1.getInteger("Stable");
      this.tickCounter = var1.getInteger("Tick");
      this.noBreedTicks = var1.getInteger("MTick");
      this.center = new BlockPos(var1.getInteger("CX"), var1.getInteger("CY"), var1.getInteger("CZ"));
      this.centerHelper = new BlockPos(var1.getInteger("ACX"), var1.getInteger("ACY"), var1.getInteger("ACZ"));
      NBTTagList var5 = var1.getTagList("Doors", 10);

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         VillageDoorInfo var8 = new VillageDoorInfo(
            new BlockPos(var7.getInteger("X"), var7.getInteger("Y"), var7.getInteger("Z")),
            var7.getInteger("IDX"),
            var7.getInteger("IDZ"),
            var7.getInteger("TS")
         );
         this.villageDoorInfoList.add(var8);
      }

      NBTTagList var11 = var1.getTagList("Players", 10);

      for (int var12 = 0; var12 < var11.tagCount(); var12++) {
         NBTTagCompound var13 = var11.getCompoundTagAt(var12);
         if (var13.hasKey("UUID")) {
            PlayerProfileCache var9 = MinecraftServer.getServer().getPlayerProfileCache();
            GameProfile var10 = var9.getProfileByUUID(UUID.fromString(var13.getString("UUID")));
            if (var10 != null) {
               this.playerReputation.put(var10.getName(), var13.getInteger("S"));
            }
         } else {
            this.playerReputation.put(var13.getString("Name"), var13.getInteger("S"));
         }
      }
   }

   public void writeVillageDataToNBT(NBTTagCompound var1) {
      var1.setInteger("PopSize", this.numVillagers);
      var1.setInteger("Radius", this.villageRadius);
      var1.setInteger("Golems", this.numIronGolems);
      var1.setInteger("Stable", this.lastAddDoorTimestamp);
      var1.setInteger("Tick", this.tickCounter);
      var1.setInteger("MTick", this.noBreedTicks);
      var1.setInteger("CX", this.center.getX());
      var1.setInteger("CY", this.center.getY());
      var1.setInteger("CZ", this.center.getZ());
      var1.setInteger("ACX", this.centerHelper.getX());
      var1.setInteger("ACY", this.centerHelper.getY());
      var1.setInteger("ACZ", this.centerHelper.getZ());
      NBTTagList var6 = new NBTTagList();

      for (VillageDoorInfo var8 : this.villageDoorInfoList) {
         NBTTagCompound var9 = new NBTTagCompound();
         var9.setInteger("X", var8.getDoorBlockPos().getX());
         var9.setInteger("Y", var8.getDoorBlockPos().getY());
         var9.setInteger("Z", var8.getDoorBlockPos().getZ());
         var9.setInteger("IDX", var8.getInsideOffsetX());
         var9.setInteger("IDZ", var8.getInsideOffsetZ());
         var9.setInteger("TS", var8.getInsidePosY());
         var6.appendTag(var9);
      }

      var1.setTag("Doors", var6);
      NBTTagList var13 = new NBTTagList();

      for (String var15 : this.playerReputation.keySet()) {
         NBTTagCompound var10 = new NBTTagCompound();
         PlayerProfileCache var11 = MinecraftServer.getServer().getPlayerProfileCache();
         GameProfile var12 = var11.getGameProfileForUsername(var15);
         if (var12 != null) {
            var10.setString("UUID", var12.getId().toString());
            var10.setInteger("S", this.playerReputation.get(var15));
            var13.appendTag(var10);
         }
      }

      var1.setTag("Players", var13);
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int var1) {
      for (String var6 : this.playerReputation.keySet()) {
         this.setReputationForPlayer(var6, var1);
      }
   }

   public class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      public VillageAggressor(EntityLivingBase var2, int var3) {
         this.agressor = var2;
         this.agressionTime = var3;
      }
   }
}
