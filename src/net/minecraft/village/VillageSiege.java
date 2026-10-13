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

   public VillageSiege(World var1) {
      this.worldObj = var1;
   }

   public void tick() {
      if (this.worldObj.isDaytime()) {
         this.field_75536_c = 0;
      } else if (this.field_75536_c != 2) {
         if (this.field_75536_c == 0) {
            float var4 = this.worldObj.getCelestialAngle(0.0F);
            if (var4 < 0.5 || var4 > 0.501) {
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
      for (EntityPlayer var6 : this.worldObj.playerEntities) {
         if (!var6.isSpectator()) {
            this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(var6), 1);
            if (this.theVillage != null
               && this.theVillage.getNumVillageDoors() >= 10
               && this.theVillage.getTicksSinceLastDoorAdding() >= 20
               && this.theVillage.getNumVillagers() >= 20) {
               BlockPos var7 = this.theVillage.getCenter();
               float var8 = this.theVillage.getVillageRadius();
               boolean var9 = false;

               for (int var10 = 0; var10 < 10; var10++) {
                  float var11 = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                  this.field_75532_g = var7.getX() + (int)(MathHelper.cos(var11) * var8 * 0.9);
                  this.field_75538_h = var7.getY();
                  this.field_75539_i = var7.getZ() + (int)(MathHelper.sin(var11) * var8 * 0.9);
                  var9 = false;

                  for (Village var13 : this.worldObj.getVillageCollection().getVillageList()) {
                     if (var13 != this.theVillage && var13.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i))) {
                        var9 = true;
                        break;
                     }
                  }

                  if (!var9) {
                     break;
                  }
               }

               if (var9) {
                  return false;
               }

               Vec3 var14 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
               if (var14 != null) {
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
      Vec3 var3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
      if (var3 == null) {
         return false;
      }

      EntityZombie var4;
      try {
         var4 = new EntityZombie(this.worldObj);
         var4.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var4)), (IEntityLivingData)null);
         var4.setVillager(false);
      } catch (Exception var6) {
         var6.printStackTrace();
         return false;
      }

      var4.setLocationAndAngles(var3.xCoord, var3.yCoord, var3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
      this.worldObj.spawnEntityInWorld(var4);
      BlockPos var5 = this.theVillage.getCenter();
      var4.setHomePosAndDistance(var5, this.theVillage.getVillageRadius());
      return true;
   }

   private Vec3 func_179867_a(BlockPos var1) {
      for (int var5 = 0; var5 < 10; var5++) {
         BlockPos var6 = var1.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.theVillage.func_179866_a(var6)
            && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, var6)) {
            return new Vec3(var6.getX(), var6.getY(), var6.getZ());
         }
      }

      return null;
   }
}
