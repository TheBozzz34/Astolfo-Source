package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
   private int spawnDelay = 20;
   private String mobID = "Pig";
   private final List<MobSpawnerBaseLogic.WeightedRandomMinecart> minecartToSpawn = Lists.newArrayList();
   private MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;
   private double mobRotation;
   private double prevMobRotation;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   private Entity cachedEntity;
   private int maxNearbyEntities = 6;
   private int activatingRangeFromPlayer = 16;
   private int spawnRange = 4;
   private static final long b = 8642003192993L;

   private String getEntityNameToSpawn() {
      if (this.getRandomEntity() == null) {
         if (this.mobID != null && this.mobID.equals("Minecart")) {
            this.mobID = "MinecartRideable";
         }

         return this.mobID;
      } else {
         return this.getRandomEntity().entityType;
      }
   }

   public void setEntityName(String var1) {
      this.mobID = var1;
   }

   private boolean isActivated() {
      BlockPos var1 = this.getSpawnerPosition();
      return this.getSpawnerWorld().isAnyPlayerWithinRangeAt(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5, this.activatingRangeFromPlayer);
   }

   public void updateSpawner() {
      if (this.isActivated()) {
         BlockPos var4 = this.getSpawnerPosition();
         if (this.getSpawnerWorld().isRemote) {
            double var5 = var4.getX() + this.getSpawnerWorld().rand.nextFloat();
            double var7 = var4.getY() + this.getSpawnerWorld().rand.nextFloat();
            double var9 = var4.getZ() + this.getSpawnerWorld().rand.nextFloat();
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var5, var7, var9, 0.0, 0.0, 0.0);
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, var5, var7, var9, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
               this.spawnDelay--;
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
         } else {
            if (this.spawnDelay == -1) {
               this.resetTimer();
            }

            if (this.spawnDelay > 0) {
               this.spawnDelay--;
               return;
            }

            boolean var16 = false;

            for (int var6 = 0; var6 < this.spawnCount; var6++) {
               Entity var17 = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
               if (var17 == null) {
                  return;
               }

               int var8 = this.getSpawnerWorld()
                  .getEntitiesWithinAABB(
                     var17.getClass(),
                     new AxisAlignedBB(var4.getX(), var4.getY(), var4.getZ(), var4.getX() + 1, var4.getY() + 1, var4.getZ() + 1)
                        .expand(this.spawnRange, this.spawnRange, this.spawnRange)
                  )
                  .size();
               if (var8 >= this.maxNearbyEntities) {
                  this.resetTimer();
                  return;
               }

               double var18 = var4.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               double var11 = var4.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1;
               double var13 = var4.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               EntityLiving var15 = var17 instanceof EntityLiving ? (EntityLiving)var17 : null;
               var17.setLocationAndAngles(var18, var11, var13, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
               if (var15 == null || var15.getCanSpawnHere() && var15.isNotColliding()) {
                  this.spawnNewEntity(var17, true);
                  this.getSpawnerWorld().playAuxSFX(2004, var4, 0);
                  if (var15 != null) {
                     var15.spawnExplosionParticle();
                  }

                  var16 = true;
               }
            }

            if (var16) {
               this.resetTimer();
            }
         }
      }
   }

   private Entity spawnNewEntity(Entity var1, boolean var2) {
      if (this.getRandomEntity() != null) {
         NBTTagCompound var6 = new NBTTagCompound();
         var1.writeToNBTOptional(var6);

         for (String var8 : this.getRandomEntity().nbtData.getKeySet()) {
            NBTBase var9 = this.getRandomEntity().nbtData.getTag(var8);
            var6.setTag(var8, var9.copy());
         }

         var1.readFromNBT(var6);
         if (var1.worldObj != null && var2) {
            var1.worldObj.spawnEntityInWorld(var1);
         }

         Entity var15 = var1;

         while (var6.hasKey("Riding", 10)) {
            NBTTagCompound var14 = var6.getCompoundTag("Riding");
            Entity var16 = EntityList.createEntityByName(var14.getString("id"), var1.worldObj);
            if (var16 != null) {
               NBTTagCompound var10 = new NBTTagCompound();
               var16.writeToNBTOptional(var10);

               for (String var12 : var14.getKeySet()) {
                  NBTBase var13 = var14.getTag(var12);
                  var10.setTag(var12, var13.copy());
               }

               var16.readFromNBT(var10);
               var16.setLocationAndAngles(var15.posX, var15.posY, var15.posZ, var15.rotationYaw, var15.rotationPitch);
               if (var1.worldObj != null && var2) {
                  var1.worldObj.spawnEntityInWorld(var16);
               }

               var15.mountEntity(var16);
            }

            var15 = var16;
            var6 = var14;
         }
      } else if (var1 instanceof EntityLivingBase && var1.worldObj != null && var2) {
         if (var1 instanceof EntityLiving) {
            ((EntityLiving)var1).onInitialSpawn(var1.worldObj.getDifficultyForLocation(new BlockPos(var1)), (IEntityLivingData)null);
         }

         var1.worldObj.spawnEntityInWorld(var1);
      }

      return var1;
   }

   private void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int var4 = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(var4);
      }

      if (this.minecartToSpawn.size() > 0) {
         this.setRandomEntity(WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
      }

      this.func_98267_a(1);
   }

   public void readFromNBT(NBTTagCompound var1) {
      this.mobID = var1.getString("EntityId");
      this.spawnDelay = var1.getShort("Delay");
      this.minecartToSpawn.clear();
      if (var1.hasKey("SpawnPotentials", 9)) {
         NBTTagList var5 = var1.getTagList("SpawnPotentials", 10);

         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(var5.getCompoundTagAt(var6)));
         }
      }

      if (var1.hasKey("SpawnData", 10)) {
         this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(var1.getCompoundTag("SpawnData"), this.mobID));
      } else {
         this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
      }

      if (var1.hasKey("MinSpawnDelay", 99)) {
         this.minSpawnDelay = var1.getShort("MinSpawnDelay");
         this.maxSpawnDelay = var1.getShort("MaxSpawnDelay");
         this.spawnCount = var1.getShort("SpawnCount");
      }

      if (var1.hasKey("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = var1.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = var1.getShort("RequiredPlayerRange");
      }

      if (var1.hasKey("SpawnRange", 99)) {
         this.spawnRange = var1.getShort("SpawnRange");
      }

      if (this.getSpawnerWorld() != null) {
         this.cachedEntity = null;
      }
   }

   public void writeToNBT(NBTTagCompound var1) {
      String var5 = this.getEntityNameToSpawn();
      if (!StringUtils.isNullOrEmpty(var5)) {
         var1.setString("EntityId", var5);
         var1.setShort("Delay", (short)this.spawnDelay);
         var1.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
         var1.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         var1.setShort("SpawnCount", (short)this.spawnCount);
         var1.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         var1.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         var1.setShort("SpawnRange", (short)this.spawnRange);
         if (this.getRandomEntity() != null) {
            var1.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
         }

         if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0) {
            NBTTagList var6 = new NBTTagList();
            if (this.minecartToSpawn.size() > 0) {
               for (MobSpawnerBaseLogic.WeightedRandomMinecart var8 : this.minecartToSpawn) {
                  var6.appendTag(var8.toNBT());
               }
            } else {
               var6.appendTag(this.getRandomEntity().toNBT());
            }

            var1.setTag("SpawnPotentials", var6);
         }
      }
   }

   public Entity func_180612_a(World var1) {
      if (this.cachedEntity == null) {
         Entity var5 = EntityList.createEntityByName(this.getEntityNameToSpawn(), var1);
         if (var5 != null) {
            var5 = this.spawnNewEntity(var5, false);
            this.cachedEntity = var5;
         }
      }

      return this.cachedEntity;
   }

   public boolean setDelayToMin(int var1) {
      if (var1 == 1 && this.getSpawnerWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   private MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity() {
      return this.randomEntity;
   }

   public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart var1) {
      this.randomEntity = var1;
   }

   public abstract void func_98267_a(int var1);

   public abstract World getSpawnerWorld();

   public abstract BlockPos getSpawnerPosition();

   public double getMobRotation() {
      return this.mobRotation;
   }

   public double getPrevMobRotation() {
      return this.prevMobRotation;
   }

   public class WeightedRandomMinecart extends WeightedRandom.Item {
      private final NBTTagCompound nbtData;
      private final String entityType;
      private static final long a = 108210089452650L;

      public WeightedRandomMinecart(NBTTagCompound var2) {
         this(var2.getCompoundTag("Properties"), var2.getString("Type"), var2.getInteger("Weight"));
      }

      public WeightedRandomMinecart(NBTTagCompound var2, String var3) {
         this(var2, var3, 1);
      }

      private WeightedRandomMinecart(NBTTagCompound var2, String var3, int var4) {
         super(var4);
         if (var3.equals("Minecart")) {
            if (var2 != null) {
               var3 = EntityMinecart.EnumMinecartType.byNetworkID(var2.getInteger("Type")).getName();
            } else {
               var3 = "MinecartRideable";
            }
         }

         this.nbtData = var2;
         this.entityType = var3;
      }

      public NBTTagCompound toNBT() {
         NBTTagCompound var3 = new NBTTagCompound();
         var3.setTag("Properties", this.nbtData);
         var3.setString("Type", this.entityType);
         var3.setInteger("Weight", this.itemWeight);
         return var3;
      }
   }
}
