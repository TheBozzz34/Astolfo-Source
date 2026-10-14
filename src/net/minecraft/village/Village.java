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

   public Village() {
   }

   public Village(World worldIn) {
      this.worldObj = worldIn;
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   public void tick(int i) {
      this.tickCounter = i;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (i % 20 == 0) {
         this.updateNumVillagers();
      }

      if (i % 30 == 0) {
         this.updateNumIronGolems();
      }

      int j = this.numVillagers / 10;
      if (this.numIronGolems < j && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0) {
         Vec3 vec3 = this.func_179862_a(this.center, 2, 4, 2);
         if (vec3 != null) {
            EntityIronGolem entityIronGolem = new EntityIronGolem(this.worldObj);
            entityIronGolem.setPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            this.worldObj.spawnEntityInWorld(entityIronGolem);
            this.numIronGolems++;
         }
      }
   }

   private Vec3 func_179862_a(BlockPos blockPos, int i, int j, int k) {
      for (int l = 0; l < 10; l++) {
         BlockPos blockPos2 = blockPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.func_179866_a(blockPos2) && this.func_179861_a(new BlockPos(i, j, k), blockPos2)) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }

   private boolean func_179861_a(BlockPos blockPos, BlockPos blockPos2) {
      if (!World.doesBlockHaveSolidTopSurface(this.worldObj, blockPos2.down())) {
         return false;
      }

      int i = blockPos2.getX() - blockPos.getX() / 2;
      int j = blockPos2.getZ() - blockPos.getZ() / 2;

      for (int k = i; k < i + blockPos.getX(); k++) {
         for (int l = blockPos2.getY(); l < blockPos2.getY() + blockPos.getY(); l++) {
            for (int m = j; m < j + blockPos.getZ(); m++) {
               if (this.worldObj.getBlockState(new BlockPos(k, l, m)).getBlock().isNormalCube()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void updateNumIronGolems() {
      List list = this.worldObj
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
      this.numIronGolems = list.size();
   }

   private void updateNumVillagers() {
      List list = this.worldObj
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
      this.numVillagers = list.size();
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

   public boolean func_179866_a(BlockPos pos) {
      return this.center.distanceSq(pos) < this.villageRadius * this.villageRadius;
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos pos) {
      VillageDoorInfo villageDoorInfo = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         int j = villageDoorInfo2.getDistanceToDoorBlockSq(pos);
         if (j < i) {
            villageDoorInfo = villageDoorInfo2;
            i = j;
         }
      }

      return villageDoorInfo;
   }

   public VillageDoorInfo getDoorInfo(BlockPos pos) {
      VillageDoorInfo villageDoorInfo = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         int j = villageDoorInfo2.getDistanceToDoorBlockSq(pos);
         if (j > 256) {
            j *= 1000;
         } else {
            j = villageDoorInfo2.getDoorOpeningRestrictionCounter();
         }

         if (j < i) {
            villageDoorInfo = villageDoorInfo2;
            i = j;
         }
      }

      return villageDoorInfo;
   }

   public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
      if (this.center.distanceSq(doorBlock) > this.villageRadius * this.villageRadius) {
         return null;
      }

      for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
         if (villageDoorInfo.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo;
         }
      }

      return null;
   }

   public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
      this.villageDoorInfoList.add(doorInfo);
      this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = doorInfo.getInsidePosY();
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
      for (Village.VillageAggressor villageAggressor : this.villageAgressors) {
         if (villageAggressor.agressor == entitylivingbaseIn) {
            villageAggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
   }

   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
      double d = Double.MAX_VALUE;
      Village.VillageAggressor villageAggressor = null;

      for (int i = 0; i < this.villageAgressors.size(); i++) {
         Village.VillageAggressor villageAggressor2 = this.villageAgressors.get(i);
         double e = villageAggressor2.agressor.getDistanceSqToEntity(entitylivingbaseIn);
         if (e <= d) {
            villageAggressor = villageAggressor2;
            d = e;
         }
      }

      return villageAggressor != null ? villageAggressor.agressor : null;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
      double d = Double.MAX_VALUE;
      EntityPlayer entityPlayer = null;

      for (String string : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(string)) {
            EntityPlayer entityPlayer2 = this.worldObj.getPlayerEntityByName(string);
            if (entityPlayer2 != null) {
               double e = entityPlayer2.getDistanceSqToEntity(villageDefender);
               if (e <= d) {
                  entityPlayer = entityPlayer2;
                  d = e;
               }
            }
         }
      }

      return entityPlayer;
   }

   private void removeDeadAndOldAgressors() {
      Iterator iterator2 = this.villageAgressors.iterator();

      while (iterator2.hasNext()) {
         Village.VillageAggressor villageAggressor = (Village.VillageAggressor)iterator2.next();
         if (!villageAggressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - villageAggressor.agressionTime) > 300) {
            iterator2.remove();
         }
      }
   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean bl = false;
      boolean bl2 = this.worldObj.rand.nextInt(50) == 0;
      Iterator iterator2 = this.villageDoorInfoList.iterator();

      while (iterator2.hasNext()) {
         VillageDoorInfo villageDoorInfo = (VillageDoorInfo)iterator2.next();
         if (bl2) {
            villageDoorInfo.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(villageDoorInfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villageDoorInfo.getInsidePosY()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villageDoorInfo.getDoorBlockPos());
            bl = true;
            villageDoorInfo.setIsDetachedFromVillageFlag(true);
            iterator2.remove();
         }
      }

      if (bl) {
         this.updateVillageRadiusAndCenter();
      }
   }

   private boolean isWoodDoor(BlockPos pos) {
      Block block = this.worldObj.getBlockState(pos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   private void updateVillageRadiusAndCenter() {
      int i = this.villageDoorInfoList.size();
      if (i == 0) {
         this.center = new BlockPos(0, 0, 0);
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / i, this.centerHelper.getY() / i, this.centerHelper.getZ() / i);
         int j = 0;

         for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
            j = Math.max(villageDoorInfo.getDistanceToDoorBlockSq(this.center), j);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt(j) + 1);
      }
   }

   public int getReputationForPlayer(String string) {
      Integer integer = this.playerReputation.get(string);
      return integer != null ? integer : 0;
   }

   public int setReputationForPlayer(String string, int i) {
      int j = this.getReputationForPlayer(string);
      int k = MathHelper.clamp_int(j + i, -30, 10);
      this.playerReputation.put(string, k);
      return k;
   }

   public boolean isPlayerReputationTooLow(String string) {
      return this.getReputationForPlayer(string) <= -15;
   }

   public void readVillageDataFromNBT(NBTTagCompound compound) {
      this.numVillagers = compound.getInteger("PopSize");
      this.villageRadius = compound.getInteger("Radius");
      this.numIronGolems = compound.getInteger("Golems");
      this.lastAddDoorTimestamp = compound.getInteger("Stable");
      this.tickCounter = compound.getInteger("Tick");
      this.noBreedTicks = compound.getInteger("MTick");
      this.center = new BlockPos(compound.getInteger("CX"), compound.getInteger("CY"), compound.getInteger("CZ"));
      this.centerHelper = new BlockPos(compound.getInteger("ACX"), compound.getInteger("ACY"), compound.getInteger("ACZ"));
      NBTTagList nbtTagList = compound.getTagList("Doors", 10);

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         VillageDoorInfo villageDoorInfo = new VillageDoorInfo(
            new BlockPos(nbtTagCompound.getInteger("X"), nbtTagCompound.getInteger("Y"), nbtTagCompound.getInteger("Z")),
            nbtTagCompound.getInteger("IDX"),
            nbtTagCompound.getInteger("IDZ"),
            nbtTagCompound.getInteger("TS")
         );
         this.villageDoorInfoList.add(villageDoorInfo);
      }

      NBTTagList nbtTagList2 = compound.getTagList("Players", 10);

      for (int j = 0; j < nbtTagList2.tagCount(); j++) {
         NBTTagCompound nbtTagCompound2 = nbtTagList2.getCompoundTagAt(j);
         if (nbtTagCompound2.hasKey("UUID")) {
            PlayerProfileCache playerProfileCache = MinecraftServer.getServer().getPlayerProfileCache();
            GameProfile gameProfile = playerProfileCache.getProfileByUUID(UUID.fromString(nbtTagCompound2.getString("UUID")));
            if (gameProfile != null) {
               this.playerReputation.put(gameProfile.getName(), nbtTagCompound2.getInteger("S"));
            }
         } else {
            this.playerReputation.put(nbtTagCompound2.getString("Name"), nbtTagCompound2.getInteger("S"));
         }
      }
   }

   public void writeVillageDataToNBT(NBTTagCompound compound) {
      compound.setInteger("PopSize", this.numVillagers);
      compound.setInteger("Radius", this.villageRadius);
      compound.setInteger("Golems", this.numIronGolems);
      compound.setInteger("Stable", this.lastAddDoorTimestamp);
      compound.setInteger("Tick", this.tickCounter);
      compound.setInteger("MTick", this.noBreedTicks);
      compound.setInteger("CX", this.center.getX());
      compound.setInteger("CY", this.center.getY());
      compound.setInteger("CZ", this.center.getZ());
      compound.setInteger("ACX", this.centerHelper.getX());
      compound.setInteger("ACY", this.centerHelper.getY());
      compound.setInteger("ACZ", this.centerHelper.getZ());
      NBTTagList nbtTagList = new NBTTagList();

      for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setInteger("X", villageDoorInfo.getDoorBlockPos().getX());
         nbtTagCompound.setInteger("Y", villageDoorInfo.getDoorBlockPos().getY());
         nbtTagCompound.setInteger("Z", villageDoorInfo.getDoorBlockPos().getZ());
         nbtTagCompound.setInteger("IDX", villageDoorInfo.getInsideOffsetX());
         nbtTagCompound.setInteger("IDZ", villageDoorInfo.getInsideOffsetZ());
         nbtTagCompound.setInteger("TS", villageDoorInfo.getInsidePosY());
         nbtTagList.appendTag(nbtTagCompound);
      }

      compound.setTag("Doors", nbtTagList);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (String string : this.playerReputation.keySet()) {
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         PlayerProfileCache playerProfileCache = MinecraftServer.getServer().getPlayerProfileCache();
         GameProfile gameProfile = playerProfileCache.getGameProfileForUsername(string);
         if (gameProfile != null) {
            nbtTagCompound2.setString("UUID", gameProfile.getId().toString());
            nbtTagCompound2.setInteger("S", this.playerReputation.get(string));
            nbtTagList2.appendTag(nbtTagCompound2);
         }
      }

      compound.setTag("Players", nbtTagList2);
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int i) {
      for (String string : this.playerReputation.keySet()) {
         this.setReputationForPlayer(string, i);
      }
   }

   public class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      public VillageAggressor(EntityLivingBase entityLivingBase, int i) {
         this.agressor = entityLivingBase;
         this.agressionTime = i;
      }
   }
}
