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

   public EntityTrackerEntry(Entity trackedEntityIn, int trackingDistanceThresholdIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn) {
      this.trackedEntity = trackedEntityIn;
      this.trackingDistanceThreshold = trackingDistanceThresholdIn;
      this.updateFrequency = updateFrequencyIn;
      this.sendVelocityUpdates = sendVelocityUpdatesIn;
      this.encodedPosX = MathHelper.floor_double(trackedEntityIn.posX * 32.0);
      this.encodedPosY = MathHelper.floor_double(trackedEntityIn.posY * 32.0);
      this.encodedPosZ = MathHelper.floor_double(trackedEntityIn.posZ * 32.0);
      this.encodedRotationYaw = MathHelper.floor_float(trackedEntityIn.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor_float(trackedEntityIn.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor_float(trackedEntityIn.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = trackedEntityIn.onGround;
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry)object).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
   }

   @Override
   public int hashCode() {
      return this.trackedEntity.getEntityId();
   }

   public void updatePlayerList(List<EntityPlayer> players) {
      this.playerEntitiesUpdated = false;
      if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
         this.lastTrackedEntityPosX = this.trackedEntity.posX;
         this.lastTrackedEntityPosY = this.trackedEntity.posY;
         this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
         this.firstUpdateDone = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayerEntities(players);
      }

      if (this.field_85178_v != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0) {
         this.field_85178_v = this.trackedEntity.ridingEntity;
         this.sendPacketToTrackedPlayers(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
      }

      if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
         EntityItemFrame entityItemFrame = (EntityItemFrame)this.trackedEntity;
         ItemStack itemStack = entityItemFrame.getDisplayedItem();
         if (itemStack != null && itemStack.getItem() instanceof ItemMap) {
            MapData mapData = Items.filled_map.getMapData(itemStack, this.trackedEntity.worldObj);

            for (EntityPlayer entityPlayer : players) {
               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityPlayer;
               mapData.updateVisiblePlayers(entityPlayerMP, itemStack);
               Packet packet = Items.filled_map.createMapDataPacket(itemStack, this.trackedEntity.worldObj, entityPlayerMP);
               if (packet != null) {
                  entityPlayerMP.playerNetServerHandler.sendPacket(packet);
               }
            }
         }

         this.sendMetadataToAllAssociatedPlayers();
      }

      if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged()) {
         if (this.trackedEntity.ridingEntity == null) {
            this.ticksSinceLastForcedTeleport++;
            int i = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int j = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int k = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int l = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int m = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int n = i - this.encodedPosX;
            int i2 = j - this.encodedPosY;
            int i3 = k - this.encodedPosZ;
            Packet packet2 = null;
            boolean bl = Math.abs(n) >= 4 || Math.abs(i2) >= 4 || Math.abs(i3) >= 4 || this.updateCounter % 60 == 0;
            boolean bl2 = Math.abs(l - this.encodedRotationYaw) >= 4 || Math.abs(m - this.encodedRotationPitch) >= 4;
            if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
               if (n >= -128
                  && n < 128
                  && i2 >= -128
                  && i2 < 128
                  && i3 >= -128
                  && i3 < 128
                  && this.ticksSinceLastForcedTeleport <= 400
                  && !this.ridingEntity
                  && this.onGround == this.trackedEntity.onGround) {
                  if ((!bl || !bl2) && !(this.trackedEntity instanceof EntityArrow)) {
                     if (bl) {
                        packet2 = new S14PacketEntity.S15PacketEntityRelMove(
                           this.trackedEntity.getEntityId(), (byte)n, (byte)i2, (byte)i3, this.trackedEntity.onGround
                        );
                     } else if (bl2) {
                        packet2 = new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)l, (byte)m, this.trackedEntity.onGround);
                     }
                  } else {
                     packet2 = new S14PacketEntity.S17PacketEntityLookMove(
                        this.trackedEntity.getEntityId(), (byte)n, (byte)i2, (byte)i3, (byte)l, (byte)m, this.trackedEntity.onGround
                     );
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceLastForcedTeleport = 0;
                  packet2 = new S18PacketEntityTeleport(
                     this.trackedEntity.getEntityId(), i, j, k, (byte)l, (byte)m, this.trackedEntity.onGround
                  );
               }
            }

            if (this.sendVelocityUpdates) {
               double d = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
               double e = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
               double d2 = this.trackedEntity.motionZ - this.motionZ;
               double d3 = 0.02;
               double d4 = d * d + e * e + d2 * d2;
               if (d4 > d3 * d3
                  || d4 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0) {
                  this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                  this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                  this.motionZ = this.trackedEntity.motionZ;
                  this.sendPacketToTrackedPlayers(
                     new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ)
                  );
               }
            }

            if (packet2 != null) {
               this.sendPacketToTrackedPlayers(packet2);
            }

            this.sendMetadataToAllAssociatedPlayers();
            if (bl) {
               this.encodedPosX = i;
               this.encodedPosY = j;
               this.encodedPosZ = k;
            }

            if (bl2) {
               this.encodedRotationYaw = l;
               this.encodedRotationPitch = m;
            }

            this.ridingEntity = false;
         } else {
            int i4 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int i5 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean bl3 = Math.abs(i4 - this.encodedRotationYaw) >= 4 || Math.abs(i5 - this.encodedRotationPitch) >= 4;
            if (bl3) {
               this.sendPacketToTrackedPlayers(
                  new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)i4, (byte)i5, this.trackedEntity.onGround)
               );
               this.encodedRotationYaw = i4;
               this.encodedRotationPitch = i5;
            }

            this.encodedPosX = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            this.encodedPosZ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            this.sendMetadataToAllAssociatedPlayers();
            this.ridingEntity = true;
         }

         int i6 = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(i6 - this.lastHeadMotion) >= 4) {
            this.sendPacketToTrackedPlayers(new S19PacketEntityHeadLook(this.trackedEntity, (byte)i6));
            this.lastHeadMotion = i6;
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
      DataWatcher dataWatcher = this.trackedEntity.getDataWatcher();
      if (dataWatcher.hasObjectChanged()) {
         this.func_151261_b(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), dataWatcher, false));
      }

      if (this.trackedEntity instanceof EntityLivingBase) {
         ServersideAttributeMap serversideAttributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
         Set set = serversideAttributeMap.getAttributeInstanceSet();
         if (!set.isEmpty()) {
            this.func_151261_b(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), set));
         }

         set.clear();
      }
   }

   public void sendPacketToTrackedPlayers(Packet packetIn) {
      for (EntityPlayerMP entityPlayerMP : this.trackingPlayers) {
         entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void func_151261_b(Packet packetIn) {
      this.sendPacketToTrackedPlayers(packetIn);
      if (this.trackedEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void sendDestroyEntityPacketToTrackedPlayers() {
      for (EntityPlayerMP entityPlayerMP : this.trackingPlayers) {
         entityPlayerMP.removeEntity(this.trackedEntity);
      }
   }

   public void removeFromTrackedPlayers(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         playerMP.removeEntity(this.trackedEntity);
         this.trackingPlayers.remove(playerMP);
      }
   }

   public void updatePlayerEntity(EntityPlayerMP playerMP) {
      if (playerMP != this.trackedEntity) {
         if (this.func_180233_c(playerMP)) {
            if (!this.trackingPlayers.contains(playerMP) && (this.isPlayerWatchingThisChunk(playerMP) || this.trackedEntity.forceSpawn)) {
               this.trackingPlayers.add(playerMP);
               Packet packet = this.createSpawnPacket();
               playerMP.playerNetServerHandler.sendPacket(packet);
               if (!this.trackedEntity.getDataWatcher().getIsBlank()) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
               }

               NBTTagCompound nbtTagCompound = this.trackedEntity.getNBTTagCompound();
               if (nbtTagCompound != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S49PacketUpdateEntityNBT(this.trackedEntity.getEntityId(), nbtTagCompound));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  ServersideAttributeMap serversideAttributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                  Collection collection = serversideAttributeMap.getWatchedAttributes();
                  if (!collection.isEmpty()) {
                     playerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), collection));
                  }
               }

               this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
               this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
               this.motionZ = this.trackedEntity.motionZ;
               if (this.sendVelocityUpdates && !(packet instanceof S0FPacketSpawnMob)) {
                  playerMP.playerNetServerHandler
                     .sendPacket(
                        new S12PacketEntityVelocity(
                           this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                        )
                     );
               }

               if (this.trackedEntity.ridingEntity != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
               }

               if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  for (int i = 0; i < 5; i++) {
                     ItemStack itemStack = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(i);
                     if (itemStack != null) {
                        playerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), i, itemStack));
                     }
                  }
               }

               if (this.trackedEntity instanceof EntityPlayer) {
                  EntityPlayer entityPlayer = (EntityPlayer)this.trackedEntity;
                  if (entityPlayer.isPlayerSleeping()) {
                     playerMP.playerNetServerHandler.sendPacket(new S0APacketUseBed(entityPlayer, new BlockPos(this.trackedEntity)));
                  }
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  EntityLivingBase entityLivingBase = (EntityLivingBase)this.trackedEntity;

                  for (PotionEffect potionEffect : entityLivingBase.getActivePotionEffects()) {
                     playerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), potionEffect));
                  }
               }
            }
         } else if (this.trackingPlayers.contains(playerMP)) {
            this.trackingPlayers.remove(playerMP);
            playerMP.removeEntity(this.trackedEntity);
         }
      }
   }

   public boolean func_180233_c(EntityPlayerMP playerMP) {
      double d = playerMP.posX - this.encodedPosX / 32;
      double e = playerMP.posZ - this.encodedPosZ / 32;
      return d >= -this.trackingDistanceThreshold
         && d <= this.trackingDistanceThreshold
         && e >= -this.trackingDistanceThreshold
         && e <= this.trackingDistanceThreshold
         && this.trackedEntity.isSpectatedByPlayer(playerMP);
   }

   private boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP) {
      return playerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
   }

   public void updatePlayerEntities(List<EntityPlayer> players) {
      for (int i = 0; i < players.size(); i++) {
         this.updatePlayerEntity((EntityPlayerMP)players.get(i));
      }
   }

   private Packet createSpawnPacket() {
      long l = a ^ 90650346816401L;
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
         EntityMinecart entityMinecart = (EntityMinecart)this.trackedEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 10, entityMinecart.getMinecartType().getNetworkID());
      }

      if (this.trackedEntity instanceof EntityBoat) {
         return new S0EPacketSpawnObject(this.trackedEntity, 1);
      }

      if (this.trackedEntity instanceof IAnimals) {
         this.lastHeadMotion = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         return new S0FPacketSpawnMob((EntityLivingBase)this.trackedEntity);
      }

      if (this.trackedEntity instanceof EntityFishHook) {
         EntityPlayer entityPlayer = ((EntityFishHook)this.trackedEntity).angler;
         return new S0EPacketSpawnObject(this.trackedEntity, 90, entityPlayer != null ? entityPlayer.getEntityId() : this.trackedEntity.getEntityId());
      }

      if (this.trackedEntity instanceof EntityArrow) {
         Entity entity2 = ((EntityArrow)this.trackedEntity).shootingEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 60, entity2 != null ? entity2.getEntityId() : this.trackedEntity.getEntityId());
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
         EntityFireball entityFireball = (EntityFireball)this.trackedEntity;
         S0EPacketSpawnObject s0EPacketSpawnObject = null;
         long m = 1279703370763477468L ^ l;
         byte b = 63;
         if (this.trackedEntity instanceof EntitySmallFireball) {
            b = 64;
         } else if (this.trackedEntity instanceof EntityWitherSkull) {
            b = 66;
         }

         if (entityFireball.shootingEntity != null) {
            s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
         } else {
            s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, 0);
         }

         s0EPacketSpawnObject.setSpeedX((int)(entityFireball.accelerationX * 8000.0));
         s0EPacketSpawnObject.setSpeedY((int)(entityFireball.accelerationY * 8000.0));
         s0EPacketSpawnObject.setSpeedZ((int)(entityFireball.accelerationZ * 8000.0));
         return s0EPacketSpawnObject;
      } else if (this.trackedEntity instanceof EntityEgg) {
         return new S0EPacketSpawnObject(this.trackedEntity, 62);
      } else if (this.trackedEntity instanceof EntityTNTPrimed) {
         return new S0EPacketSpawnObject(this.trackedEntity, 50);
      } else if (this.trackedEntity instanceof EntityEnderCrystal) {
         return new S0EPacketSpawnObject(this.trackedEntity, 51);
      } else if (this.trackedEntity instanceof EntityFallingBlock) {
         EntityFallingBlock entityFallingBlock = (EntityFallingBlock)this.trackedEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(entityFallingBlock.getBlock()));
      } else if (this.trackedEntity instanceof EntityArmorStand) {
         return new S0EPacketSpawnObject(this.trackedEntity, 78);
      } else if (this.trackedEntity instanceof EntityPainting) {
         return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityItemFrame) {
         EntityItemFrame entityItemFrame = (EntityItemFrame)this.trackedEntity;
         S0EPacketSpawnObject s0EPacketSpawnObject2 = new S0EPacketSpawnObject(this.trackedEntity, 71, entityItemFrame.facingDirection.getHorizontalIndex());
         BlockPos blockPos = entityItemFrame.getHangingPosition();
         s0EPacketSpawnObject2.setX(MathHelper.floor_float(blockPos.getX() * 32));
         s0EPacketSpawnObject2.setY(MathHelper.floor_float(blockPos.getY() * 32));
         s0EPacketSpawnObject2.setZ(MathHelper.floor_float(blockPos.getZ() * 32));
         return s0EPacketSpawnObject2;
      } else if (this.trackedEntity instanceof EntityLeashKnot) {
         EntityLeashKnot entityLeashKnot = (EntityLeashKnot)this.trackedEntity;
         S0EPacketSpawnObject s0EPacketSpawnObject3 = new S0EPacketSpawnObject(this.trackedEntity, 77);
         BlockPos blockPos2 = entityLeashKnot.getHangingPosition();
         s0EPacketSpawnObject3.setX(MathHelper.floor_float(blockPos2.getX() * 32));
         s0EPacketSpawnObject3.setY(MathHelper.floor_float(blockPos2.getY() * 32));
         s0EPacketSpawnObject3.setZ(MathHelper.floor_float(blockPos2.getZ() * 32));
         return s0EPacketSpawnObject3;
      } else if (this.trackedEntity instanceof EntityXPOrb) {
         return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
      }
   }

   public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         this.trackingPlayers.remove(playerMP);
         playerMP.removeEntity(this.trackedEntity);
      }
   }
}
