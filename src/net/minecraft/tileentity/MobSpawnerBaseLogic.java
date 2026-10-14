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

   public void setEntityName(String name) {
      this.mobID = name;
   }

   private boolean isActivated() {
      BlockPos blockPos = this.getSpawnerPosition();
      return this.getSpawnerWorld().isAnyPlayerWithinRangeAt(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, this.activatingRangeFromPlayer);
   }

   public void updateSpawner() {
      if (this.isActivated()) {
         BlockPos blockPos = this.getSpawnerPosition();
         if (this.getSpawnerWorld().isRemote) {
            double d = blockPos.getX() + this.getSpawnerWorld().rand.nextFloat();
            double e = blockPos.getY() + this.getSpawnerWorld().rand.nextFloat();
            double d2 = blockPos.getZ() + this.getSpawnerWorld().rand.nextFloat();
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, e, d2, 0.0, 0.0, 0.0);
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d, e, d2, 0.0, 0.0, 0.0);
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

            boolean bl = false;

            for (int i = 0; i < this.spawnCount; i++) {
               Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
               if (entity == null) {
                  return;
               }

               int j = this.getSpawnerWorld()
                  .getEntitiesWithinAABB(
                     entity.getClass(),
                     new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1)
                        .expand(this.spawnRange, this.spawnRange, this.spawnRange)
                  )
                  .size();
               if (j >= this.maxNearbyEntities) {
                  this.resetTimer();
                  return;
               }

               double d3 = blockPos.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               double d4 = blockPos.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1;
               double d5 = blockPos.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               EntityLiving entityLiving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
               entity.setLocationAndAngles(d3, d4, d5, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
               if (entityLiving == null || entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {
                  this.spawnNewEntity(entity, true);
                  this.getSpawnerWorld().playAuxSFX(2004, blockPos, 0);
                  if (entityLiving != null) {
                     entityLiving.spawnExplosionParticle();
                  }

                  bl = true;
               }
            }

            if (bl) {
               this.resetTimer();
            }
         }
      }
   }

   private Entity spawnNewEntity(Entity entityIn, boolean spawn) {
      if (this.getRandomEntity() != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         entityIn.writeToNBTOptional(nbtTagCompound);

         for (String string : this.getRandomEntity().nbtData.getKeySet()) {
            NBTBase nbtBase = this.getRandomEntity().nbtData.getTag(string);
            nbtTagCompound.setTag(string, nbtBase.copy());
         }

         entityIn.readFromNBT(nbtTagCompound);
         if (entityIn.worldObj != null && spawn) {
            entityIn.worldObj.spawnEntityInWorld(entityIn);
         }

         Entity entity = entityIn;

         while (nbtTagCompound.hasKey("Riding", 10)) {
            NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Riding");
            Entity entity2 = EntityList.createEntityByName(nbtTagCompound2.getString("id"), entityIn.worldObj);
            if (entity2 != null) {
               NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
               entity2.writeToNBTOptional(nbtTagCompound3);

               for (String string2 : nbtTagCompound2.getKeySet()) {
                  NBTBase nbtBase2 = nbtTagCompound2.getTag(string2);
                  nbtTagCompound3.setTag(string2, nbtBase2.copy());
               }

               entity2.readFromNBT(nbtTagCompound3);
               entity2.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
               if (entityIn.worldObj != null && spawn) {
                  entityIn.worldObj.spawnEntityInWorld(entity2);
               }

               entity.mountEntity(entity2);
            }

            entity = entity2;
            nbtTagCompound = nbtTagCompound2;
         }
      } else if (entityIn instanceof EntityLivingBase && entityIn.worldObj != null && spawn) {
         if (entityIn instanceof EntityLiving) {
            ((EntityLiving)entityIn).onInitialSpawn(entityIn.worldObj.getDifficultyForLocation(new BlockPos(entityIn)), (IEntityLivingData)null);
         }

         entityIn.worldObj.spawnEntityInWorld(entityIn);
      }

      return entityIn;
   }

   private void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int i = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
      }

      if (this.minecartToSpawn.size() > 0) {
         this.setRandomEntity(WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
      }

      this.func_98267_a(1);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.mobID = nbt.getString("EntityId");
      this.spawnDelay = nbt.getShort("Delay");
      this.minecartToSpawn.clear();
      if (nbt.hasKey("SpawnPotentials", 9)) {
         NBTTagList nbtTagList = nbt.getTagList("SpawnPotentials", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbtTagList.getCompoundTagAt(i)));
         }
      }

      if (nbt.hasKey("SpawnData", 10)) {
         this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbt.getCompoundTag("SpawnData"), this.mobID));
      } else {
         this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
      }

      if (nbt.hasKey("MinSpawnDelay", 99)) {
         this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
         this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
         this.spawnCount = nbt.getShort("SpawnCount");
      }

      if (nbt.hasKey("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
      }

      if (nbt.hasKey("SpawnRange", 99)) {
         this.spawnRange = nbt.getShort("SpawnRange");
      }

      if (this.getSpawnerWorld() != null) {
         this.cachedEntity = null;
      }
   }

   public void writeToNBT(NBTTagCompound nbt) {
      String string = this.getEntityNameToSpawn();
      if (!StringUtils.isNullOrEmpty(string)) {
         nbt.setString("EntityId", string);
         nbt.setShort("Delay", (short)this.spawnDelay);
         nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
         nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         nbt.setShort("SpawnCount", (short)this.spawnCount);
         nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         nbt.setShort("SpawnRange", (short)this.spawnRange);
         if (this.getRandomEntity() != null) {
            nbt.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
         }

         if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0) {
            NBTTagList nbtTagList = new NBTTagList();
            if (this.minecartToSpawn.size() > 0) {
               for (MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart : this.minecartToSpawn) {
                  nbtTagList.appendTag(weightedRandomMinecart.toNBT());
               }
            } else {
               nbtTagList.appendTag(this.getRandomEntity().toNBT());
            }

            nbt.setTag("SpawnPotentials", nbtTagList);
         }
      }
   }

   public Entity func_180612_a(World worldIn) {
      if (this.cachedEntity == null) {
         Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), worldIn);
         if (entity != null) {
            entity = this.spawnNewEntity(entity, false);
            this.cachedEntity = entity;
         }
      }

      return this.cachedEntity;
   }

   public boolean setDelayToMin(int delay) {
      if (delay == 1 && this.getSpawnerWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   private MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity() {
      return this.randomEntity;
   }

   public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart) {
      this.randomEntity = weightedRandomMinecart;
   }

   public abstract void func_98267_a(int id);

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

      public WeightedRandomMinecart(NBTTagCompound tagCompound) {
         this(tagCompound.getCompoundTag("Properties"), tagCompound.getString("Type"), tagCompound.getInteger("Weight"));
      }

      public WeightedRandomMinecart(NBTTagCompound tagCompound, String typeIn) {
         this(tagCompound, typeIn, 1);
      }

      private WeightedRandomMinecart(NBTTagCompound tagCompound, String typeIn, int weight) {
         super(weight);
         if (typeIn.equals("Minecart")) {
            if (tagCompound != null) {
               typeIn = EntityMinecart.EnumMinecartType.byNetworkID(tagCompound.getInteger("Type")).getName();
            } else {
               typeIn = "MinecartRideable";
            }
         }

         this.nbtData = tagCompound;
         this.entityType = typeIn;
      }

      public NBTTagCompound toNBT() {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setTag("Properties", this.nbtData);
         nbtTagCompound.setString("Type", this.entityType);
         nbtTagCompound.setInteger("Weight", this.itemWeight);
         return nbtTagCompound;
      }
   }
}
