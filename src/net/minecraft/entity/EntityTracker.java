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

   public EntityTracker(WorldServer theWorldIn) {
      this.theWorld = theWorldIn;
      this.maxTrackingDistanceThreshold = theWorldIn.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
   }

   public void trackEntity(Entity entityIn) {
      if (entityIn instanceof EntityPlayerMP) {
         this.trackEntity(entityIn, 512, 2);
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityIn;

         for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
            if (entityTrackerEntry.trackedEntity != entityPlayerMP) {
               entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
            }
         }
      } else if (entityIn instanceof EntityFishHook) {
         this.addEntityToTracker(entityIn, 64, 5, true);
      } else if (entityIn instanceof EntityArrow) {
         this.addEntityToTracker(entityIn, 64, 20, false);
      } else if (entityIn instanceof EntitySmallFireball) {
         this.addEntityToTracker(entityIn, 64, 10, false);
      } else if (entityIn instanceof EntityFireball) {
         this.addEntityToTracker(entityIn, 64, 10, false);
      } else if (entityIn instanceof EntitySnowball) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityEnderPearl) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityEnderEye) {
         this.addEntityToTracker(entityIn, 64, 4, true);
      } else if (entityIn instanceof EntityEgg) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityPotion) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityExpBottle) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityFireworkRocket) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityItem) {
         this.addEntityToTracker(entityIn, 64, 20, true);
      } else if (entityIn instanceof EntityMinecart) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntityBoat) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntitySquid) {
         this.addEntityToTracker(entityIn, 64, 3, true);
      } else if (entityIn instanceof EntityWither) {
         this.addEntityToTracker(entityIn, 80, 3, false);
      } else if (entityIn instanceof EntityBat) {
         this.addEntityToTracker(entityIn, 80, 3, false);
      } else if (entityIn instanceof EntityDragon) {
         this.addEntityToTracker(entityIn, 160, 3, true);
      } else if (entityIn instanceof IAnimals) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntityTNTPrimed) {
         this.addEntityToTracker(entityIn, 160, 10, true);
      } else if (entityIn instanceof EntityFallingBlock) {
         this.addEntityToTracker(entityIn, 160, 20, true);
      } else if (entityIn instanceof EntityHanging) {
         this.addEntityToTracker(entityIn, 160, Integer.MAX_VALUE, false);
      } else if (entityIn instanceof EntityArmorStand) {
         this.addEntityToTracker(entityIn, 160, 3, true);
      } else if (entityIn instanceof EntityXPOrb) {
         this.addEntityToTracker(entityIn, 160, 20, true);
      } else if (entityIn instanceof EntityEnderCrystal) {
         this.addEntityToTracker(entityIn, 256, Integer.MAX_VALUE, false);
      }
   }

   public void trackEntity(Entity entityIn, int trackingRange, int updateFrequency) {
      this.addEntityToTracker(entityIn, trackingRange, updateFrequency, false);
   }

   public void addEntityToTracker(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates) {
      if (trackingRange > this.maxTrackingDistanceThreshold) {
         trackingRange = this.maxTrackingDistanceThreshold;
      }

      try {
         if (this.trackedEntityHashTable.containsItem(entityIn.getEntityId())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry entityTrackerEntry = new EntityTrackerEntry(entityIn, trackingRange, updateFrequency, sendVelocityUpdates);
         this.trackedEntities.add(entityTrackerEntry);
         this.trackedEntityHashTable.addKey(entityIn.getEntityId(), entityTrackerEntry);
         entityTrackerEntry.updatePlayerEntities(this.theWorld.playerEntities);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Adding entity to track");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity To Track");
         crashReportCategory.addCrashSection("Tracking range", trackingRange + " blocks");
         crashReportCategory.addCrashSectionCallable("Update interval", new Callable<String>() {
            private static final long a = 97084932681086L;
            private static final long e = -7470733788314599425L;

            public String call() throws Exception {
               String string = "Once per " + updateFrequency + " ticks";
               if (updateFrequency == (int)e) {
                  string = "Maximum (" + string + ")";
               }

               return string;
            }
         });
         entityIn.addEntityCrashInfo(crashReportCategory);
         CrashReportCategory crashReportCategory2 = crashReport.makeCategory("Entity That Is Already Tracked");
         this.trackedEntityHashTable.lookup(entityIn.getEntityId()).trackedEntity.addEntityCrashInfo(crashReportCategory2);

         try {
            throw new ReportedException(crashReport);
         } catch (ReportedException reportedException) {
            logger.error("\"Silently\" catching entity tracking error.", reportedException);
         }
      }
   }

   public void untrackEntity(Entity entityIn) {
      if (entityIn instanceof EntityPlayerMP) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityIn;

         for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
            entityTrackerEntry.removeFromTrackedPlayers(entityPlayerMP);
         }
      }

      EntityTrackerEntry entityTrackerEntry2 = this.trackedEntityHashTable.removeObject(entityIn.getEntityId());
      if (entityTrackerEntry2 != null) {
         this.trackedEntities.remove(entityTrackerEntry2);
         entityTrackerEntry2.sendDestroyEntityPacketToTrackedPlayers();
      }
   }

   public void updateTrackedEntities() {
      ArrayList arrayList = Lists.newArrayList();

      for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
         entityTrackerEntry.updatePlayerList(this.theWorld.playerEntities);
         if (entityTrackerEntry.playerEntitiesUpdated && entityTrackerEntry.trackedEntity instanceof EntityPlayerMP) {
            arrayList.add((EntityPlayerMP)entityTrackerEntry.trackedEntity);
         }
      }

      for (int i = 0; i < arrayList.size(); i++) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)arrayList.get(i);

         for (EntityTrackerEntry entityTrackerEntry2 : this.trackedEntities) {
            if (entityTrackerEntry2.trackedEntity != entityPlayerMP) {
               entityTrackerEntry2.updatePlayerEntity(entityPlayerMP);
            }
         }
      }
   }

   public void func_180245_a(EntityPlayerMP entityPlayerMP) {
      for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
         if (entityTrackerEntry.trackedEntity == entityPlayerMP) {
            entityTrackerEntry.updatePlayerEntities(this.theWorld.playerEntities);
         } else {
            entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
         }
      }
   }

   public void sendToAllTrackingEntity(Entity entityIn, Packet packet) {
      EntityTrackerEntry entityTrackerEntry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());
      if (entityTrackerEntry != null) {
         entityTrackerEntry.sendPacketToTrackedPlayers(packet);
      }
   }

   public void func_151248_b(Entity entityIn, Packet packet) {
      EntityTrackerEntry entityTrackerEntry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());
      if (entityTrackerEntry != null) {
         entityTrackerEntry.func_151261_b(packet);
      }
   }

   public void removePlayerFromTrackers(EntityPlayerMP entityPlayerMP) {
      for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
         entityTrackerEntry.removeTrackedPlayerSymmetric(entityPlayerMP);
      }
   }

   public void func_85172_a(EntityPlayerMP entityPlayerMP, Chunk chunk) {
      for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
         if (entityTrackerEntry.trackedEntity != entityPlayerMP && entityTrackerEntry.trackedEntity.chunkCoordX == chunk.xPosition && entityTrackerEntry.trackedEntity.chunkCoordZ == chunk.zPosition) {
            entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
         }
      }
   }
}
