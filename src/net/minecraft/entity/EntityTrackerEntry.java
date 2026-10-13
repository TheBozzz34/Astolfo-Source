package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   private static final Logger logger = LogManager.getLogger();
   public Entity trackedEntity;
   public int trackingDistanceThreshold;
   public int updateFrequency;
   public int encodedPosX;
   public int encodedPosY;
   public int encodedPosZ;
   public int encodedRotationYaw;
   public int encodedRotationPitch;
   public int lastHeadMotion;
   public double lastTrackedEntityMotionX;
   public double lastTrackedEntityMotionY;
   public double motionZ;
   public int updateCounter;
   private double lastTrackedEntityPosX;
   private double lastTrackedEntityPosY;
   private double lastTrackedEntityPosZ;
   private boolean firstUpdateDone;
   private boolean sendVelocityUpdates;
   private int ticksSinceLastForcedTeleport;
   private Entity field_85178_v;
   private boolean ridingEntity;
   private boolean onGround;
   public boolean playerEntitiesUpdated;
   public Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();
   private static final long a = 65831500106796L;

   public EntityTrackerEntry(Entity var1, int var2, int var3, boolean var4) {
      this.trackedEntity = var1;
      this.trackingDistanceThreshold = var2;
      this.updateFrequency = var3;
      this.sendVelocityUpdates = var4;
      this.encodedPosX = MathHelper.floor_double(var1.posX * 32.0);
      this.encodedPosY = MathHelper.floor_double(var1.posY * 32.0);
      this.encodedPosZ = MathHelper.floor_double(var1.posZ * 32.0);
      this.encodedRotationYaw = MathHelper.floor_float(var1.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor_float(var1.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor_float(var1.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = var1.onGround;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)var1).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
   }

   @Override
   public int hashCode() {
      return this.trackedEntity.getEntityId();
   }

   public void updatePlayerList(List<EntityPlayer> var1) {
      this.playerEntitiesUpdated = false;
      if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
         this.lastTrackedEntityPosX = this.trackedEntity.posX;
         this.lastTrackedEntityPosY = this.trackedEntity.posY;
         this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
         this.firstUpdateDone = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayerEntities(var1);
      }

      if (this.field_85178_v != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0) {
         this.field_85178_v = this.trackedEntity.ridingEntity;
         this.sendPacketToTrackedPlayers(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
      }

      if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
         EntityItemFrame var5 = (EntityItemFrame)this.trackedEntity;
         ItemStack var6 = var5.getDisplayedItem();
         if (var6 != null && var6.getItem() instanceof ItemMap) {
            MapData var7 = Items.filled_map.getMapData(var6, this.trackedEntity.worldObj);

            for (EntityPlayer var9 : var1) {
               EntityPlayerMP var10 = (EntityPlayerMP)var9;
               var7.updateVisiblePlayers(var10, var6);
               Packet var11 = Items.filled_map.createMapDataPacket(var6, this.trackedEntity.worldObj, var10);
               if (var11 != null) {
                  var10.playerNetServerHandler.sendPacket(var11);
               }
            }
         }

         this.sendMetadataToAllAssociatedPlayers();
      }

      if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged()) {
         if (this.trackedEntity.ridingEntity == null) {
            this.ticksSinceLastForcedTeleport++;
            int var27 = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int var30 = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int var32 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int var33 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int var34 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int var35 = var27 - this.encodedPosX;
            int var36 = var30 - this.encodedPosY;
            int var12 = var32 - this.encodedPosZ;
            Packet var13 = null;
            boolean var14 = Math.abs(var35) >= 4 || Math.abs(var36) >= 4 || Math.abs(var12) >= 4 || this.updateCounter % 60 == 0;
            boolean var15 = Math.abs(var33 - this.encodedRotationYaw) >= 4 || Math.abs(var34 - this.encodedRotationPitch) >= 4;
            if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
               if (var35 >= -128
                  && var35 < 128
                  && var36 >= -128
                  && var36 < 128
                  && var12 >= -128
                  && var12 < 128
                  && this.ticksSinceLastForcedTeleport <= 400
                  && !this.ridingEntity
                  && this.onGround == this.trackedEntity.onGround) {
                  if ((!var14 || !var15) && !(this.trackedEntity instanceof EntityArrow)) {
                     if (var14) {
                        var13 = new S14PacketEntity.S15PacketEntityRelMove(
                           this.trackedEntity.getEntityId(), (byte)var35, (byte)var36, (byte)var12, this.trackedEntity.onGround
                        );
                     } else if (var15) {
                        var13 = new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)var33, (byte)var34, this.trackedEntity.onGround);
                     }
                  } else {
                     var13 = new S14PacketEntity.S17PacketEntityLookMove(
                        this.trackedEntity.getEntityId(), (byte)var35, (byte)var36, (byte)var12, (byte)var33, (byte)var34, this.trackedEntity.onGround
                     );
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceLastForcedTeleport = 0;
                  var13 = new S18PacketEntityTeleport(
                     this.trackedEntity.getEntityId(), var27, var30, var32, (byte)var33, (byte)var34, this.trackedEntity.onGround
                  );
               }
            }

            if (this.sendVelocityUpdates) {
               double var16 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
               double var18 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
               double var20 = this.trackedEntity.motionZ - this.motionZ;
               double var22 = 0.02;
               double var24 = var16 * var16 + var18 * var18 + var20 * var20;
               if (var24 > var22 * var22
                  || var24 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0) {
                  this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                  this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                  this.motionZ = this.trackedEntity.motionZ;
                  this.sendPacketToTrackedPlayers(
                     new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ)
                  );
               }
            }

            if (var13 != null) {
               this.sendPacketToTrackedPlayers(var13);
            }

            this.sendMetadataToAllAssociatedPlayers();
            if (var14) {
               this.encodedPosX = var27;
               this.encodedPosY = var30;
               this.encodedPosZ = var32;
            }

            if (var15) {
               this.encodedRotationYaw = var33;
               this.encodedRotationPitch = var34;
            }

            this.ridingEntity = false;
         } else {
            int var26 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int var29 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean var31 = Math.abs(var26 - this.encodedRotationYaw) >= 4 || Math.abs(var29 - this.encodedRotationPitch) >= 4;
            if (var31) {
               this.sendPacketToTrackedPlayers(
                  new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)var26, (byte)var29, this.trackedEntity.onGround)
               );
               this.encodedRotationYaw = var26;
               this.encodedRotationPitch = var29;
            }

            this.encodedPosX = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            this.encodedPosZ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            this.sendMetadataToAllAssociatedPlayers();
            this.ridingEntity = true;
         }

         int var28 = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(var28 - this.lastHeadMotion) >= 4) {
            this.sendPacketToTrackedPlayers(new S19PacketEntityHeadLook(this.trackedEntity, (byte)var28));
            this.lastHeadMotion = var28;
         }

         this.trackedEntity.isAirBorne = false;
      }

      this.updateCounter++;
      if (this.trackedEntity.velocityChanged) {
         this.func_151261_b(new S12PacketEntityVelocity(this.trackedEntity));
         this.trackedEntity.velocityChanged = false;
      }
   }

   private void sendMetadataToAllAssociatedPlayers() {
      DataWatcher var4 = this.trackedEntity.getDataWatcher();
      if (var4.hasObjectChanged()) {
         this.func_151261_b(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), var4, false));
      }

      if (this.trackedEntity instanceof EntityLivingBase) {
         ServersideAttributeMap var5 = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
         Set var6 = var5.getAttributeInstanceSet();
         if (!var6.isEmpty()) {
            this.func_151261_b(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), var6));
         }

         var6.clear();
      }
   }

   public void sendPacketToTrackedPlayers(Packet var1) {
      for (EntityPlayerMP var6 : this.trackingPlayers) {
         var6.playerNetServerHandler.sendPacket(var1);
      }
   }

   public void func_151261_b(Packet var1) {
      this.sendPacketToTrackedPlayers(var1);
      if (this.trackedEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(var1);
      }
   }

   public void sendDestroyEntityPacketToTrackedPlayers() {
      for (EntityPlayerMP var5 : this.trackingPlayers) {
         var5.removeEntity(this.trackedEntity);
      }
   }

   public void removeFromTrackedPlayers(EntityPlayerMP var1) {
      if (this.trackingPlayers.contains(var1)) {
         var1.removeEntity(this.trackedEntity);
         this.trackingPlayers.remove(var1);
      }
   }

   public void updatePlayerEntity(EntityPlayerMP var1) {
      if (var1 != this.trackedEntity) {
         if (this.func_180233_c(var1)) {
            if (!this.trackingPlayers.contains(var1) && (this.isPlayerWatchingThisChunk(var1) || this.trackedEntity.forceSpawn)) {
               this.trackingPlayers.add(var1);
               Packet var5 = this.createSpawnPacket();
               var1.playerNetServerHandler.sendPacket(var5);
               if (!this.trackedEntity.getDataWatcher().getIsBlank()) {
                  var1.playerNetServerHandler
                     .sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
               }

               NBTTagCompound var6 = this.trackedEntity.getNBTTagCompound();
               if (var6 != null) {
                  var1.playerNetServerHandler.sendPacket(new S49PacketUpdateEntityNBT(this.trackedEntity.getEntityId(), var6));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  ServersideAttributeMap var7 = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                  Collection var8 = var7.getWatchedAttributes();
                  if (!var8.isEmpty()) {
                     var1.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), var8));
                  }
               }

               this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
               this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
               this.motionZ = this.trackedEntity.motionZ;
               if (this.sendVelocityUpdates && !(var5 instanceof S0FPacketSpawnMob)) {
                  var1.playerNetServerHandler
                     .sendPacket(
                        new S12PacketEntityVelocity(
                           this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                        )
                     );
               }

               if (this.trackedEntity.ridingEntity != null) {
                  var1.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
               }

               if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null) {
                  var1.playerNetServerHandler
                     .sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  for (int var10 = 0; var10 < 5; var10++) {
                     ItemStack var13 = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(var10);
                     if (var13 != null) {
                        var1.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), var10, var13));
                     }
                  }
               }

               if (this.trackedEntity instanceof EntityPlayer) {
                  EntityPlayer var11 = (EntityPlayer)this.trackedEntity;
                  if (var11.isPlayerSleeping()) {
                     var1.playerNetServerHandler.sendPacket(new S0APacketUseBed(var11, new BlockPos(this.trackedEntity)));
                  }
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  EntityLivingBase var12 = (EntityLivingBase)this.trackedEntity;

                  for (PotionEffect var9 : var12.getActivePotionEffects()) {
                     var1.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), var9));
                  }
               }
            }
         } else if (this.trackingPlayers.contains(var1)) {
            this.trackingPlayers.remove(var1);
            var1.removeEntity(this.trackedEntity);
         }
      }
   }

   public boolean func_180233_c(EntityPlayerMP var1) {
      double var5 = var1.posX - this.encodedPosX / 32;
      double var7 = var1.posZ - this.encodedPosZ / 32;
      return var5 >= -this.trackingDistanceThreshold
         && var5 <= this.trackingDistanceThreshold
         && var7 >= -this.trackingDistanceThreshold
         && var7 <= this.trackingDistanceThreshold
         && this.trackedEntity.isSpectatedByPlayer(var1);
   }

   private boolean isPlayerWatchingThisChunk(EntityPlayerMP var1) {
      return var1.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(var1, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
   }

   public void updatePlayerEntities(List<EntityPlayer> var1) {
      for (int var5 = 0; var5 < var1.size(); var5++) {
         this.updatePlayerEntity((EntityPlayerMP)var1.get(var5));
      }
   }

   private Packet createSpawnPacket() {
      long var1 = a ^ 90650346816401L;
      if (this.trackedEntity.isDead) {
         logger.warn("Fetching addPacket for removed entity");
      }

      if (this.trackedEntity instanceof EntityItem) {
         return new S0EPacketSpawnObject(this.trackedEntity, 2, 1);
      }

      if (this.trackedEntity instanceof EntityPlayerMP) {
         return new S0CPacketSpawnPlayer((EntityPlayer)this.trackedEntity);
      }

      if (this.trackedEntity instanceof EntityMinecart) {
         EntityMinecart var12 = (EntityMinecart)this.trackedEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 10, var12.getMinecartType().getNetworkID());
      }

      if (this.trackedEntity instanceof EntityBoat) {
         return new S0EPacketSpawnObject(this.trackedEntity, 1);
      }

      if (this.trackedEntity instanceof IAnimals) {
         this.lastHeadMotion = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         return new S0FPacketSpawnMob((EntityLivingBase)this.trackedEntity);
      }

      if (this.trackedEntity instanceof EntityFishHook) {
         EntityPlayer var11 = ((EntityFishHook)this.trackedEntity).angler;
         return new S0EPacketSpawnObject(this.trackedEntity, 90, var11 != null ? var11.getEntityId() : this.trackedEntity.getEntityId());
      }

      if (this.trackedEntity instanceof EntityArrow) {
         Entity var10 = ((EntityArrow)this.trackedEntity).shootingEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 60, var10 != null ? var10.getEntityId() : this.trackedEntity.getEntityId());
      }

      if (this.trackedEntity instanceof EntitySnowball) {
         return new S0EPacketSpawnObject(this.trackedEntity, 61);
      }

      if (this.trackedEntity instanceof EntityPotion) {
         return new S0EPacketSpawnObject(this.trackedEntity, 73, ((EntityPotion)this.trackedEntity).getPotionDamage());
      }

      if (this.trackedEntity instanceof EntityExpBottle) {
         return new S0EPacketSpawnObject(this.trackedEntity, 75);
      }

      if (this.trackedEntity instanceof EntityEnderPearl) {
         return new S0EPacketSpawnObject(this.trackedEntity, 65);
      }

      if (this.trackedEntity instanceof EntityEnderEye) {
         return new S0EPacketSpawnObject(this.trackedEntity, 72);
      }

      if (this.trackedEntity instanceof EntityFireworkRocket) {
         return new S0EPacketSpawnObject(this.trackedEntity, 76);
      }

      if (this.trackedEntity instanceof EntityFireball) {
         EntityFireball var9 = (EntityFireball)this.trackedEntity;
         S0EPacketSpawnObject var14 = null;
         long var10000 = 1279703370763477468L ^ var1;
         byte var17 = 63;
         if (this.trackedEntity instanceof EntitySmallFireball) {
            var17 = 64;
         } else if (this.trackedEntity instanceof EntityWitherSkull) {
            var17 = 66;
         }

         if (var9.shootingEntity != null) {
            var14 = new S0EPacketSpawnObject(this.trackedEntity, var17, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
         } else {
            var14 = new S0EPacketSpawnObject(this.trackedEntity, var17, 0);
         }

         var14.setSpeedX((int)(var9.accelerationX * 8000.0));
         var14.setSpeedY((int)(var9.accelerationY * 8000.0));
         var14.setSpeedZ((int)(var9.accelerationZ * 8000.0));
         return var14;
      } else if (this.trackedEntity instanceof EntityEgg) {
         return new S0EPacketSpawnObject(this.trackedEntity, 62);
      } else if (this.trackedEntity instanceof EntityTNTPrimed) {
         return new S0EPacketSpawnObject(this.trackedEntity, 50);
      } else if (this.trackedEntity instanceof EntityEnderCrystal) {
         return new S0EPacketSpawnObject(this.trackedEntity, 51);
      } else if (this.trackedEntity instanceof EntityFallingBlock) {
         EntityFallingBlock var8 = (EntityFallingBlock)this.trackedEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(var8.getBlock()));
      } else if (this.trackedEntity instanceof EntityArmorStand) {
         return new S0EPacketSpawnObject(this.trackedEntity, 78);
      } else if (this.trackedEntity instanceof EntityPainting) {
         return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityItemFrame) {
         EntityItemFrame var7 = (EntityItemFrame)this.trackedEntity;
         S0EPacketSpawnObject var13 = new S0EPacketSpawnObject(this.trackedEntity, 71, var7.facingDirection.getHorizontalIndex());
         BlockPos var16 = var7.getHangingPosition();
         var13.setX(MathHelper.floor_float(var16.getX() * 32));
         var13.setY(MathHelper.floor_float(var16.getY() * 32));
         var13.setZ(MathHelper.floor_float(var16.getZ() * 32));
         return var13;
      } else if (this.trackedEntity instanceof EntityLeashKnot) {
         EntityLeashKnot var4 = (EntityLeashKnot)this.trackedEntity;
         S0EPacketSpawnObject var5 = new S0EPacketSpawnObject(this.trackedEntity, 77);
         BlockPos var6 = var4.getHangingPosition();
         var5.setX(MathHelper.floor_float(var6.getX() * 32));
         var5.setY(MathHelper.floor_float(var6.getY() * 32));
         var5.setZ(MathHelper.floor_float(var6.getZ() * 32));
         return var5;
      } else if (this.trackedEntity instanceof EntityXPOrb) {
         return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
      }
   }

   public void removeTrackedPlayerSymmetric(EntityPlayerMP var1) {
      if (this.trackingPlayers.contains(var1)) {
         this.trackingPlayers.remove(var1);
         var1.removeEntity(this.trackedEntity);
      }
   }
}
