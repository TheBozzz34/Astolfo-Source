package net.minecraft.village;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class VillageSiege {
   private World worldObj;
   private boolean field_75535_b;
   private int field_75536_c = -1;
   private int field_75533_d;
   private int field_75534_e;
   private Village theVillage;
   private int field_75532_g;
   private int field_75538_h;
   private int field_75539_i;
   private static final long a = 4700230799732L;

   public VillageSiege(World worldIn) {
      this.worldObj = worldIn;
   }

   public void tick() {
      if (this.worldObj.isDaytime()) {
         this.field_75536_c = 0;
      } else if (this.field_75536_c != 2) {
         if (this.field_75536_c == 0) {
            float f = this.worldObj.getCelestialAngle(0.0F);
            if (f < 0.5 || f > 0.501) {
               return;
            }

            this.field_75536_c = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
            this.field_75535_b = false;
            if (this.field_75536_c == 2) {
               return;
            }
         }

         if (this.field_75536_c != -1) {
            if (!this.field_75535_b) {
               if (!this.func_75529_b()) {
                  return;
               }

               this.field_75535_b = true;
            }

            if (this.field_75534_e > 0) {
               this.field_75534_e--;
            } else {
               this.field_75534_e = 2;
               if (this.field_75533_d > 0) {
                  this.spawnZombie();
                  this.field_75533_d--;
               } else {
                  this.field_75536_c = 2;
               }
            }
         }
      }
   }

   private boolean func_75529_b() {
      for (EntityPlayer entityPlayer : this.worldObj.playerEntities) {
         if (!entityPlayer.isSpectator()) {
            this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(entityPlayer), 1);
            if (this.theVillage != null
               && this.theVillage.getNumVillageDoors() >= 10
               && this.theVillage.getTicksSinceLastDoorAdding() >= 20
               && this.theVillage.getNumVillagers() >= 20) {
               BlockPos blockPos = this.theVillage.getCenter();
               float f = this.theVillage.getVillageRadius();
               boolean bl = false;

               for (int i = 0; i < 10; i++) {
                  float g = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                  this.field_75532_g = blockPos.getX() + (int)(MathHelper.cos(g) * f * 0.9);
                  this.field_75538_h = blockPos.getY();
                  this.field_75539_i = blockPos.getZ() + (int)(MathHelper.sin(g) * f * 0.9);
                  bl = false;

                  for (Village village : this.worldObj.getVillageCollection().getVillageList()) {
                     if (village != this.theVillage && village.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i))) {
                        bl = true;
                        break;
                     }
                  }

                  if (!bl) {
                     break;
                  }
               }

               if (bl) {
                  return false;
               }

               Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
               if (vec3 != null) {
                  this.field_75534_e = 0;
                  this.field_75533_d = 20;
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean spawnZombie() {
      Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
      if (vec3 == null) {
         return false;
      }

      EntityZombie entityZombie;
      try {
         entityZombie = new EntityZombie(this.worldObj);
         entityZombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityZombie)), (IEntityLivingData)null);
         entityZombie.setVillager(false);
      } catch (Exception exception) {
         exception.printStackTrace();
         return false;
      }

      entityZombie.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
      this.worldObj.spawnEntityInWorld(entityZombie);
      BlockPos blockPos = this.theVillage.getCenter();
      entityZombie.setHomePosAndDistance(blockPos, this.theVillage.getVillageRadius());
      return true;
   }

   private Vec3 func_179867_a(BlockPos blockPos) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos2 = blockPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.theVillage.func_179866_a(blockPos2)
            && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, blockPos2)) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }
}
