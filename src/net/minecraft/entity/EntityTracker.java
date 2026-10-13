package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
   private static final Logger logger = LogManager.getLogger();
   private final WorldServer theWorld;
   private Set<EntityTrackerEntry> trackedEntities = Sets.newHashSet();
   private IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap<>();
   private int maxTrackingDistanceThreshold;
   private static final long a = 101873760328026L;

   public EntityTracker(WorldServer var1) {
      this.theWorld = var1;
      this.maxTrackingDistanceThreshold = var1.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
   }

   public void trackEntity(Entity var1) {
      if (var1 instanceof EntityPlayerMP) {
         this.trackEntity(var1, 512, 2);
         EntityPlayerMP var5 = (EntityPlayerMP)var1;

         for (EntityTrackerEntry var7 : this.trackedEntities) {
            if (var7.trackedEntity != var5) {
               var7.updatePlayerEntity(var5);
            }
         }
      } else if (var1 instanceof EntityFishHook) {
         this.addEntityToTracker(var1, 64, 5, true);
      } else if (var1 instanceof EntityArrow) {
         this.addEntityToTracker(var1, 64, 20, false);
      } else if (var1 instanceof EntitySmallFireball) {
         this.addEntityToTracker(var1, 64, 10, false);
      } else if (var1 instanceof EntityFireball) {
         this.addEntityToTracker(var1, 64, 10, false);
      } else if (var1 instanceof EntitySnowball) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityEnderPearl) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityEnderEye) {
         this.addEntityToTracker(var1, 64, 4, true);
      } else if (var1 instanceof EntityEgg) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityPotion) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityExpBottle) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityFireworkRocket) {
         this.addEntityToTracker(var1, 64, 10, true);
      } else if (var1 instanceof EntityItem) {
         this.addEntityToTracker(var1, 64, 20, true);
      } else if (var1 instanceof EntityMinecart) {
         this.addEntityToTracker(var1, 80, 3, true);
      } else if (var1 instanceof EntityBoat) {
         this.addEntityToTracker(var1, 80, 3, true);
      } else if (var1 instanceof EntitySquid) {
         this.addEntityToTracker(var1, 64, 3, true);
      } else if (var1 instanceof EntityWither) {
         this.addEntityToTracker(var1, 80, 3, false);
      } else if (var1 instanceof EntityBat) {
         this.addEntityToTracker(var1, 80, 3, false);
      } else if (var1 instanceof EntityDragon) {
         this.addEntityToTracker(var1, 160, 3, true);
      } else if (var1 instanceof IAnimals) {
         this.addEntityToTracker(var1, 80, 3, true);
      } else if (var1 instanceof EntityTNTPrimed) {
         this.addEntityToTracker(var1, 160, 10, true);
      } else if (var1 instanceof EntityFallingBlock) {
         this.addEntityToTracker(var1, 160, 20, true);
      } else if (var1 instanceof EntityHanging) {
         this.addEntityToTracker(var1, 160, Integer.MAX_VALUE, false);
      } else if (var1 instanceof EntityArmorStand) {
         this.addEntityToTracker(var1, 160, 3, true);
      } else if (var1 instanceof EntityXPOrb) {
         this.addEntityToTracker(var1, 160, 20, true);
      } else if (var1 instanceof EntityEnderCrystal) {
         this.addEntityToTracker(var1, 256, Integer.MAX_VALUE, false);
      }
   }

   public void trackEntity(Entity var1, int var2, int var3) {
      this.addEntityToTracker(var1, var2, var3, false);
   }

   public void addEntityToTracker(Entity var1, int var2, final int var3, boolean var4) {
      if (var2 > this.maxTrackingDistanceThreshold) {
         var2 = this.maxTrackingDistanceThreshold;
      }

      try {
         if (this.trackedEntityHashTable.containsItem(var1.getEntityId())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry var8 = new EntityTrackerEntry(var1, var2, var3, var4);
         this.trackedEntities.add(var8);
         this.trackedEntityHashTable.addKey(var1.getEntityId(), var8);
         var8.updatePlayerEntities(this.theWorld.playerEntities);
      } catch (Throwable var14) {
         CrashReport var9 = CrashReport.makeCrashReport(var14, "Adding entity to track");
         CrashReportCategory var10 = var9.makeCategory("Entity To Track");
         var10.addCrashSection("Tracking range", var2 + " blocks");
         var10.addCrashSectionCallable("Update interval", new Callable<String>() {
            private static final long a = 97084932681086L;
            private static final long e = -7470733788314599425L;

            public String call() throws Exception {
               String var3x = "Once per " + var3 + " ticks";
               if (var3 == (int)e) {
                  var3x = "Maximum (" + var3x + ")";
               }

               return var3x;
            }
         });
         var1.addEntityCrashInfo(var10);
         CrashReportCategory var11 = var9.makeCategory("Entity That Is Already Tracked");
         this.trackedEntityHashTable.lookup(var1.getEntityId()).trackedEntity.addEntityCrashInfo(var11);

         try {
            throw new ReportedException(var9);
         } catch (ReportedException var13) {
            logger.error("\"Silently\" catching entity tracking error.", var13);
         }
      }
   }

   public void untrackEntity(Entity var1) {
      if (var1 instanceof EntityPlayerMP) {
         EntityPlayerMP var5 = (EntityPlayerMP)var1;

         for (EntityTrackerEntry var7 : this.trackedEntities) {
            var7.removeFromTrackedPlayers(var5);
         }
      }

      EntityTrackerEntry var8 = this.trackedEntityHashTable.removeObject(var1.getEntityId());
      if (var8 != null) {
         this.trackedEntities.remove(var8);
         var8.sendDestroyEntityPacketToTrackedPlayers();
      }
   }

   public void updateTrackedEntities() {
      ArrayList var4 = Lists.newArrayList();

      for (EntityTrackerEntry var6 : this.trackedEntities) {
         var6.updatePlayerList(this.theWorld.playerEntities);
         if (var6.playerEntitiesUpdated && var6.trackedEntity instanceof EntityPlayerMP) {
            var4.add((EntityPlayerMP)var6.trackedEntity);
         }
      }

      for (int var9 = 0; var9 < var4.size(); var9++) {
         EntityPlayerMP var10 = (EntityPlayerMP)var4.get(var9);

         for (EntityTrackerEntry var8 : this.trackedEntities) {
            if (var8.trackedEntity != var10) {
               var8.updatePlayerEntity(var10);
            }
         }
      }
   }

   public void func_180245_a(EntityPlayerMP var1) {
      for (EntityTrackerEntry var6 : this.trackedEntities) {
         if (var6.trackedEntity == var1) {
            var6.updatePlayerEntities(this.theWorld.playerEntities);
         } else {
            var6.updatePlayerEntity(var1);
         }
      }
   }

   public void sendToAllTrackingEntity(Entity var1, Packet var2) {
      EntityTrackerEntry var6 = this.trackedEntityHashTable.lookup(var1.getEntityId());
      if (var6 != null) {
         var6.sendPacketToTrackedPlayers(var2);
      }
   }

   public void func_151248_b(Entity var1, Packet var2) {
      EntityTrackerEntry var6 = this.trackedEntityHashTable.lookup(var1.getEntityId());
      if (var6 != null) {
         var6.func_151261_b(var2);
      }
   }

   public void removePlayerFromTrackers(EntityPlayerMP var1) {
      for (EntityTrackerEntry var6 : this.trackedEntities) {
         var6.removeTrackedPlayerSymmetric(var1);
      }
   }

   public void func_85172_a(EntityPlayerMP var1, Chunk var2) {
      for (EntityTrackerEntry var7 : this.trackedEntities) {
         if (var7.trackedEntity != var1 && var7.trackedEntity.chunkCoordX == var2.xPosition && var7.trackedEntity.chunkCoordZ == var2.zPosition) {
            var7.updatePlayerEntity(var1);
         }
      }
   }
}
