package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   private static final Logger logger = LogManager.getLogger();
   public final NetworkManager netManager;
   private final MinecraftServer serverController;
   public EntityPlayerMP playerEntity;
   private int networkTickCount;
   private int field_175090_f;
   private int floatingTickCount;
   private boolean field_147366_g;
   private int field_147378_h;
   private long lastPingTime;
   private long lastSentPingPacket;
   private int chatSpamThresholdCount;
   private int itemDropThreshold;
   private IntHashMap<Short> field_147372_n = new IntHashMap<>();
   private double lastPosX;
   private double lastPosY;
   private double lastPosZ;
   private boolean hasMoved = true;

   public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
      this.serverController = server;
      this.netManager = networkManagerIn;
      networkManagerIn.setNetHandler(this);
      this.playerEntity = playerIn;
      playerIn.playerNetServerHandler = this;
   }

   @Override
   public void update() {
      this.field_147366_g = false;
      this.networkTickCount++;
      this.serverController.theProfiler.startSection("keepAlive");
      if (this.networkTickCount - this.lastSentPingPacket > 40L) {
         this.lastSentPingPacket = this.networkTickCount;
         this.lastPingTime = this.currentTimeMillis();
         this.field_147378_h = (int)this.lastPingTime;
         this.sendPacket(new S00PacketKeepAlive(this.field_147378_h));
      }

      this.serverController.theProfiler.endSection();
      if (this.chatSpamThresholdCount > 0) {
         this.chatSpamThresholdCount--;
      }

      if (this.itemDropThreshold > 0) {
         this.itemDropThreshold--;
      }

      if (this.playerEntity.getLastActiveTime() > 0L
         && this.serverController.getMaxPlayerIdleMinutes() > 0
         && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60) {
         this.kickPlayerFromServer("You have been idle for too long!");
      }
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public void kickPlayerFromServer(String reason) {
      final ChatComponentText chatComponentText = new ChatComponentText(reason);
      this.netManager.sendPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener<Future<? super Void>>() {
         @Override
         public void operationComplete(Future<? super Void> future) throws Exception {
            NetHandlerPlayServer.this.netManager.closeChannel(chatComponentText);
         }
      });
      this.netManager.disableAutoRead();
      Futures.getUnchecked(this.serverController.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            NetHandlerPlayServer.this.netManager.checkDisconnected();
         }
      }));
   }

   @Override
   public void processInput(C0CPacketInput packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
   }

   private boolean func_183006_b(C03PacketPlayer c03PacketPlayer) {
      return !Doubles.isFinite(c03PacketPlayer.getPositionX())
         || !Doubles.isFinite(c03PacketPlayer.getPositionY())
         || !Doubles.isFinite(c03PacketPlayer.getPositionZ())
         || !Floats.isFinite(c03PacketPlayer.getPitch())
         || !Floats.isFinite(c03PacketPlayer.getYaw());
   }

   @Override
   public void processPlayer(C03PacketPlayer packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.func_183006_b(packetIn)) {
         this.kickPlayerFromServer("Invalid move packet received");
      } else {
         WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
         this.field_147366_g = true;
         if (!this.playerEntity.playerConqueredTheEnd) {
            double d = this.playerEntity.posX;
            double e = this.playerEntity.posY;
            double d2 = this.playerEntity.posZ;
            double d3 = 0.0;
            double d4 = packetIn.getPositionX() - this.lastPosX;
            double d5 = packetIn.getPositionY() - this.lastPosY;
            double d6 = packetIn.getPositionZ() - this.lastPosZ;
            if (packetIn.isMoving()) {
               d3 = d4 * d4 + d5 * d5 + d6 * d6;
               if (!this.hasMoved && d3 < 0.25) {
                  this.hasMoved = true;
               }
            }

            if (this.hasMoved) {
               this.field_175090_f = this.networkTickCount;
               if (this.playerEntity.ridingEntity != null) {
                  float f = this.playerEntity.rotationYaw;
                  float g = this.playerEntity.rotationPitch;
                  this.playerEntity.ridingEntity.updateRiderPosition();
                  double d7 = this.playerEntity.posX;
                  double d8 = this.playerEntity.posY;
                  double d9 = this.playerEntity.posZ;
                  if (packetIn.getRotating()) {
                     f = packetIn.getYaw();
                     g = packetIn.getPitch();
                  }

                  this.playerEntity.onGround = packetIn.isOnGround();
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.setPositionAndRotation(d7, d8, d9, f, g);
                  if (this.playerEntity.ridingEntity != null) {
                     this.playerEntity.ridingEntity.updateRiderPosition();
                  }

                  this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                  if (this.playerEntity.ridingEntity != null) {
                     if (d3 > 4.0) {
                        Entity entity = this.playerEntity.ridingEntity;
                        this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(entity));
                        this.setPlayerLocation(
                           this.playerEntity.posX,
                           this.playerEntity.posY,
                           this.playerEntity.posZ,
                           this.playerEntity.rotationYaw,
                           this.playerEntity.rotationPitch
                        );
                     }

                     this.playerEntity.ridingEntity.isAirBorne = true;
                  }

                  if (this.hasMoved) {
                     this.lastPosX = this.playerEntity.posX;
                     this.lastPosY = this.playerEntity.posY;
                     this.lastPosZ = this.playerEntity.posZ;
                  }

                  worldServer.updateEntity(this.playerEntity);
                  return;
               }

               if (this.playerEntity.isPlayerSleeping()) {
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity
                     .setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  worldServer.updateEntity(this.playerEntity);
                  return;
               }

               double d10 = this.playerEntity.posY;
               this.lastPosX = this.playerEntity.posX;
               this.lastPosY = this.playerEntity.posY;
               this.lastPosZ = this.playerEntity.posZ;
               double d11 = this.playerEntity.posX;
               double d12 = this.playerEntity.posY;
               double d13 = this.playerEntity.posZ;
               float h = this.playerEntity.rotationYaw;
               float f2 = this.playerEntity.rotationPitch;
               if (packetIn.isMoving() && packetIn.getPositionY() == -999.0) {
                  packetIn.setMoving(false);
               }

               if (packetIn.isMoving()) {
                  d11 = packetIn.getPositionX();
                  d12 = packetIn.getPositionY();
                  d13 = packetIn.getPositionZ();
                  if (Math.abs(packetIn.getPositionX()) > 3.0E7 || Math.abs(packetIn.getPositionZ()) > 3.0E7) {
                     this.kickPlayerFromServer("Illegal position");
                     return;
                  }
               }

               if (packetIn.getRotating()) {
                  h = packetIn.getYaw();
                  f2 = packetIn.getPitch();
               }

               this.playerEntity.onUpdateEntity();
               this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, h, f2);
               if (!this.hasMoved) {
                  return;
               }

               double d14 = d11 - this.playerEntity.posX;
               double d15 = d12 - this.playerEntity.posY;
               double d16 = d13 - this.playerEntity.posZ;
               double d17 = this.playerEntity.motionX * this.playerEntity.motionX
                  + this.playerEntity.motionY * this.playerEntity.motionY
                  + this.playerEntity.motionZ * this.playerEntity.motionZ;
               double d18 = d14 * d14 + d15 * d15 + d16 * d16;
               if (d18 - d17 > 100.0
                  && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getName()))) {
                  logger.warn(
                     this.playerEntity.getName()
                        + " moved too quickly! "
                        + d14
                        + ","
                        + d15
                        + ","
                        + d16
                        + " ("
                        + d14
                        + ", "
                        + d15
                        + ", "
                        + d16
                        + ")"
                  );
                  this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  return;
               }

               float f3 = 0.0625F;
               boolean bl = worldServer.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f3, f3, f3))
                  .isEmpty();
               if (this.playerEntity.onGround && !packetIn.isOnGround() && d15 > 0.0) {
                  this.playerEntity.jump();
               }

               this.playerEntity.moveEntity(d14, d15, d16);
               this.playerEntity.onGround = packetIn.isOnGround();
               d14 = d11 - this.playerEntity.posX;
               d15 = d12 - this.playerEntity.posY;
               if (d15 > -0.5 || d15 < 0.5) {
                  d15 = 0.0;
               }

               d16 = d13 - this.playerEntity.posZ;
               d18 = d14 * d14 + d15 * d15 + d16 * d16;
               boolean bl2 = false;
               if (d18 > 0.0625 && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                  bl2 = true;
                  logger.warn(this.playerEntity.getName() + " moved wrongly!");
               }

               this.playerEntity.setPositionAndRotation(d11, d12, d13, h, f2);
               this.playerEntity.addMovementStat(this.playerEntity.posX - d, this.playerEntity.posY - e, this.playerEntity.posZ - d2);
               if (!this.playerEntity.noClip) {
                  boolean bl3 = worldServer.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f3, f3, f3))
                     .isEmpty();
                  if (bl && (bl2 || !bl3) && !this.playerEntity.isPlayerSleeping()) {
                     this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, h, f2);
                     return;
                  }
               }

               AxisAlignedBB axisAlignedBB = this.playerEntity.getEntityBoundingBox().expand(f3, f3, f3).addCoord(0.0, -0.55, 0.0);
               if (this.serverController.isFlightAllowed() || this.playerEntity.capabilities.allowFlying || worldServer.checkBlockCollision(axisAlignedBB)) {
                  this.floatingTickCount = 0;
               } else if (d15 >= -0.03125) {
                  this.floatingTickCount++;
                  if (this.floatingTickCount > 80) {
                     logger.warn(this.playerEntity.getName() + " was kicked for floating too long!");
                     this.kickPlayerFromServer("Flying is not enabled on this server");
                     return;
                  }
               }

               this.playerEntity.onGround = packetIn.isOnGround();
               this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
               this.playerEntity.handleFalling(this.playerEntity.posY - d10, packetIn.isOnGround());
            } else if (this.networkTickCount - this.field_175090_f > 20) {
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
         }
      }
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
      this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<S08PacketPlayerPosLook.EnumFlags> relativeSet) {
      this.hasMoved = false;
      this.lastPosX = x;
      this.lastPosY = y;
      this.lastPosZ = z;
      if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         this.lastPosX = this.lastPosX + this.playerEntity.posX;
      }

      if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         this.lastPosY = this.lastPosY + this.playerEntity.posY;
      }

      if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         this.lastPosZ = this.lastPosZ + this.playerEntity.posZ;
      }

      float f = yaw;
      float g = pitch;
      if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         f = yaw + this.playerEntity.rotationYaw;
      }

      if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         g = pitch + this.playerEntity.rotationPitch;
      }

      this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f, g);
      this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, yaw, pitch, relativeSet));
   }

   @Override
   public void processPlayerDigging(C07PacketPlayerDigging packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos blockPos = packetIn.getPosition();
      this.playerEntity.markPlayerActive();
      switch (packetIn.getStatus()) {
         case DROP_ITEM:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(false);
            }

            return;
         case DROP_ALL_ITEMS:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(true);
            }

            return;
         case RELEASE_USE_ITEM:
            this.playerEntity.stopUsingItem();
            return;
         case START_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            double d = this.playerEntity.posX - (blockPos.getX() + 0.5);
            double e = this.playerEntity.posY - (blockPos.getY() + 0.5) + 1.5;
            double d2 = this.playerEntity.posZ - (blockPos.getZ() + 0.5);
            double d3 = d * d + e * e + d2 * d2;
            if (d3 > 36.0) {
               return;
            } else if (blockPos.getY() >= this.serverController.getBuildLimit()) {
               return;
            } else {
               if (packetIn.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  if (!this.serverController.isBlockProtected(worldServer, blockPos, this.playerEntity) && worldServer.getWorldBorder().contains(blockPos)) {
                     this.playerEntity.theItemInWorldManager.onBlockClicked(blockPos, packetIn.getFacing());
                  } else {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos));
                  }
               } else {
                  if (packetIn.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.blockRemoving(blockPos);
                  } else if (packetIn.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                  }

                  if (worldServer.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos));
                  }
               }

               return;
            }
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   @Override
   public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      ItemStack itemStack = this.playerEntity.inventory.getCurrentItem();
      boolean bl = false;
      BlockPos blockPos = packetIn.getPosition();
      EnumFacing enumFacing = EnumFacing.getFront(packetIn.getPlacedBlockDirection());
      this.playerEntity.markPlayerActive();
      if (packetIn.getPlacedBlockDirection() == 255) {
         if (itemStack == null) {
            return;
         }

         this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldServer, itemStack);
      } else if (blockPos.getY() < this.serverController.getBuildLimit() - 1 || enumFacing != EnumFacing.UP && blockPos.getY() < this.serverController.getBuildLimit()) {
         if (this.hasMoved
            && this.playerEntity.getDistanceSq(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) < 64.0
            && !this.serverController.isBlockProtected(worldServer, blockPos, this.playerEntity)
            && worldServer.getWorldBorder().contains(blockPos)) {
            this.playerEntity
               .theItemInWorldManager
               .activateBlockOrUseItem(
                  this.playerEntity,
                  worldServer,
                  itemStack,
                  blockPos,
                  enumFacing,
                  packetIn.getPlacedBlockOffsetX(),
                  packetIn.getPlacedBlockOffsetY(),
                  packetIn.getPlacedBlockOffsetZ()
               );
         }

         bl = true;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("build.tooHigh", this.serverController.getBuildLimit());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponentTranslation));
         bl = true;
      }

      if (bl) {
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos));
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos.offset(enumFacing)));
      }

      itemStack = this.playerEntity.inventory.getCurrentItem();
      if (itemStack != null && itemStack.stackSize == 0) {
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
         itemStack = null;
      }

      if (itemStack == null || itemStack.getMaxItemUseDuration() == 0) {
         this.playerEntity.isChangingQuantityOnly = true;
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]
         );
         Slot slot = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
         this.playerEntity.openContainer.detectAndSendChanges();
         this.playerEntity.isChangingQuantityOnly = false;
         if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.getStack())) {
            this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, slot.slotNumber, this.playerEntity.inventory.getCurrentItem()));
         }
      }
   }

   @Override
   public void handleSpectate(C18PacketSpectate packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.isSpectator()) {
         Entity entity = null;

         for (WorldServer worldServer : this.serverController.worldServers) {
            if (worldServer != null) {
               entity = packetIn.getEntity(worldServer);
               if (entity != null) {
                  break;
               }
            }
         }

         if (entity != null) {
            this.playerEntity.setSpectatingEntity(this.playerEntity);
            this.playerEntity.mountEntity((Entity)null);
            if (entity.worldObj != this.playerEntity.worldObj) {
               WorldServer worldServer2 = this.playerEntity.getServerForPlayer();
               WorldServer worldServer3 = (WorldServer)entity.worldObj;
               this.playerEntity.dimension = entity.dimension;
               this.sendPacket(
                  new S07PacketRespawn(
                     this.playerEntity.dimension,
                     worldServer2.getDifficulty(),
                     worldServer2.getWorldInfo().getTerrainType(),
                     this.playerEntity.theItemInWorldManager.getGameType()
                  )
               );
               worldServer2.removePlayerEntityDangerously(this.playerEntity);
               this.playerEntity.isDead = false;
               this.playerEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
               if (this.playerEntity.isEntityAlive()) {
                  worldServer2.updateEntityWithOptionalForce(this.playerEntity, false);
                  worldServer3.spawnEntityInWorld(this.playerEntity);
                  worldServer3.updateEntityWithOptionalForce(this.playerEntity, false);
               }

               this.playerEntity.setWorld(worldServer3);
               this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, worldServer2);
               this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
               this.playerEntity.theItemInWorldManager.setWorld(worldServer3);
               this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, worldServer3);
               this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
            } else {
               this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
            }
         }
      }
   }

   @Override
   public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn) {
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      logger.info(this.playerEntity.getName() + " lost connection: " + reason);
      this.serverController.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.serverController.getConfigurationManager().sendChatMsg(chatComponentTranslation);
      this.playerEntity.mountEntityAndWakeUp();
      this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);
      if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
         logger.info("Stopping singleplayer server as player logged out");
         this.serverController.initiateShutdown();
      }
   }

   public void sendPacket(final Packet packetIn) {
      if (packetIn instanceof S02PacketChat) {
         S02PacketChat s02PacketChat = (S02PacketChat)packetIn;
         EntityPlayer.EnumChatVisibility enumChatVisibility = this.playerEntity.getChatVisibility();
         if (enumChatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
         }

         if (enumChatVisibility == EntityPlayer.EnumChatVisibility.SYSTEM && !s02PacketChat.isChat()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(packetIn);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Sending packet");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Packet being sent");
         crashReportCategory.addCrashSectionCallable("Packet class", new Callable<String>() {
            public String call() throws Exception {
               return packetIn.getClass().getCanonicalName();
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   @Override
   public void processHeldItemChange(C09PacketHeldItemChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize()) {
         this.playerEntity.inventory.currentItem = packetIn.getSlotId();
         this.playerEntity.markPlayerActive();
      } else {
         logger.warn(this.playerEntity.getName() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void processChatMessage(C01PacketChatMessage packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("chat.cannotSend");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.sendPacket(new S02PacketChat(chatComponentTranslation));
      } else {
         this.playerEntity.markPlayerActive();
         String string = packetIn.getMessage();
         string = StringUtils.normalizeSpace(string);

         for (int i = 0; i < string.length(); i++) {
            if (!ChatAllowedCharacters.isAllowedCharacter(string.charAt(i))) {
               this.kickPlayerFromServer("Illegal characters in chat");
               return;
            }
         }

         if (string.startsWith("/")) {
            this.handleSlashCommand(string);
         } else {
            ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("chat.type.text", this.playerEntity.getDisplayName(), string);
            this.serverController.getConfigurationManager().sendChatMsgImpl(chatComponentTranslation2, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())) {
            this.kickPlayerFromServer("disconnect.spam");
         }
      }
   }

   private void handleSlashCommand(String command) {
      this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
   }

   @Override
   public void handleAnimation(C0APacketAnimation packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      this.playerEntity.swingItem();
   }

   @Override
   public void processEntityAction(C0BPacketEntityAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      switch (packetIn.getAction()) {
         case START_SNEAKING:
            this.playerEntity.setSneaking(true);
            break;
         case STOP_SNEAKING:
            this.playerEntity.setSneaking(false);
            break;
         case START_SPRINTING:
            this.playerEntity.setSprinting(true);
            break;
         case STOP_SPRINTING:
            this.playerEntity.setSprinting(false);
            break;
         case STOP_SLEEPING:
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
            break;
         case RIDING_JUMP:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.getAuxData());
            }
            break;
         case OPEN_INVENTORY:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid client command!");
      }
   }

   @Override
   public void processUseEntity(C02PacketUseEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      Entity entity2 = packetIn.getEntityFromWorld(worldServer);
      this.playerEntity.markPlayerActive();
      if (entity2 != null) {
         boolean bl = this.playerEntity.canEntityBeSeen(entity2);
         double d = 36.0;
         if (!bl) {
            d = 9.0;
         }

         if (this.playerEntity.getDistanceSqToEntity(entity2) < d) {
            if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT) {
               this.playerEntity.interactWith(entity2);
            } else if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
               entity2.interactAt(this.playerEntity, packetIn.getHitVec());
            } else if (packetIn.getAction() == C02PacketUseEntity.Action.ATTACK) {
               if (entity2 instanceof EntityItem || entity2 instanceof EntityXPOrb || entity2 instanceof EntityArrow || entity2 == this.playerEntity) {
                  this.kickPlayerFromServer("Attempting to attack an invalid entity");
                  this.serverController.logWarning("Player " + this.playerEntity.getName() + " tried to attack an invalid entity");
                  return;
               }

               this.playerEntity.attackTargetEntityWithCurrentItem(entity2);
            }
         }
      }
   }

   @Override
   public void processClientStatus(C16PacketClientStatus packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      C16PacketClientStatus.EnumState enumState = packetIn.getStatus();
      switch (enumState) {
         case PERFORM_RESPAWN:
            if (this.playerEntity.playerConqueredTheEnd) {
               this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
            } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
               if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                  this.serverController.deleteWorldAndStopServer();
               } else {
                  UserListBansEntry userListBansEntry = new UserListBansEntry(
                     this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore"
                  );
                  this.serverController.getConfigurationManager().getBannedPlayers().addEntry(userListBansEntry);
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
               }
            } else {
               if (this.playerEntity.getHealth() > 0.0F) {
                  return;
               }

               this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, false);
            }
            break;
         case REQUEST_STATS:
            this.playerEntity.getStatFile().func_150876_a(this.playerEntity);
            break;
         case OPEN_INVENTORY_ACHIEVEMENT:
            this.playerEntity.triggerAchievement(AchievementList.openInventory);
      }
   }

   @Override
   public void processCloseWindow(C0DPacketCloseWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.closeContainer();
   }

   @Override
   public void processClickWindow(C0EPacketClickWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
         if (this.playerEntity.isSpectator()) {
            ArrayList arrayList = Lists.newArrayList();

            for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); i++) {
               arrayList.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
            }

            this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arrayList);
         } else {
            ItemStack itemStack = this.playerEntity.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getMode(), this.playerEntity);
            if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), itemStack)) {
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
               this.playerEntity.isChangingQuantityOnly = true;
               this.playerEntity.openContainer.detectAndSendChanges();
               this.playerEntity.updateHeldItem();
               this.playerEntity.isChangingQuantityOnly = false;
            } else {
               this.field_147372_n.addKey(this.playerEntity.openContainer.windowId, packetIn.getActionNumber());
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), false));
               this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
               ArrayList arrayList2 = Lists.newArrayList();

               for (int j = 0; j < this.playerEntity.openContainer.inventorySlots.size(); j++) {
                  arrayList2.add(this.playerEntity.openContainer.inventorySlots.get(j).getStack());
               }

               this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arrayList2);
            }
         }
      }
   }

   @Override
   public void processEnchantItem(C11PacketEnchantItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.getWindowId()
         && this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
         this.playerEntity.openContainer.detectAndSendChanges();
      }
   }

   @Override
   public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.theItemInWorldManager.isCreative()) {
         boolean bl = packetIn.getSlotId() < 0;
         ItemStack itemStack = packetIn.getStack();
         if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BlockEntityTag", 10)) {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound().getCompoundTag("BlockEntityTag");
            if (nbtTagCompound.hasKey("x") && nbtTagCompound.hasKey("y") && nbtTagCompound.hasKey("z")) {
               BlockPos blockPos = new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
               TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(blockPos);
               if (tileEntity != null) {
                  NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                  tileEntity.writeToNBT(nbtTagCompound2);
                  nbtTagCompound2.removeTag("x");
                  nbtTagCompound2.removeTag("y");
                  nbtTagCompound2.removeTag("z");
                  itemStack.setTagInfo("BlockEntityTag", nbtTagCompound2);
               }
            }
         }

         boolean bl2 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
         boolean bl3 = itemStack == null || itemStack.getItem() != null;
         boolean bl4 = itemStack == null || itemStack.getMetadata() >= 0 && itemStack.stackSize <= 64 && itemStack.stackSize > 0;
         if (bl2 && bl3 && bl4) {
            if (itemStack == null) {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), (ItemStack)null);
            } else {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), itemStack);
            }

            this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
         } else if (bl && bl3 && bl4 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            EntityItem entityItem = this.playerEntity.dropPlayerItemWithRandomChoice(itemStack, true);
            if (entityItem != null) {
               entityItem.setAgeToCreativeDespawnTime();
            }
         }
      }
   }

   @Override
   public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      Short short2 = this.field_147372_n.lookup(this.playerEntity.openContainer.windowId);
      if (short2 != null
         && packetIn.getUid() == short2
         && this.playerEntity.openContainer.windowId == packetIn.getWindowId()
         && !this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
      }
   }

   @Override
   public void processUpdateSign(C12PacketUpdateSign packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos blockPos = packetIn.getPosition();
      if (worldServer.isBlockLoaded(blockPos)) {
         TileEntity tileEntity = worldServer.getTileEntity(blockPos);
         if (!(tileEntity instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
         if (!tileEntitySign.getIsEditable() || tileEntitySign.getPlayer() != this.playerEntity) {
            this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
            return;
         }

         IChatComponent[] iChatComponents = packetIn.getLines();

         for (int i = 0; i < iChatComponents.length; i++) {
            tileEntitySign.signText[i] = new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(iChatComponents[i].getUnformattedText()));
         }

         tileEntitySign.markDirty();
         worldServer.markBlockForUpdate(blockPos);
      }
   }

   @Override
   public void processKeepAlive(C00PacketKeepAlive packetIn) {
      if (packetIn.getKey() == this.field_147378_h) {
         int i = (int)(this.currentTimeMillis() - this.lastPingTime);
         this.playerEntity.ping = (this.playerEntity.ping * 3 + i) / 4;
      }
   }

   private long currentTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void processPlayerAbilities(C13PacketPlayerAbilities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
   }

   @Override
   public void processTabComplete(C14PacketTabComplete packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      ArrayList arrayList = Lists.newArrayList();

      for (String string : this.serverController.getTabCompletions(this.playerEntity, packetIn.getMessage(), packetIn.getTargetBlock())) {
         arrayList.add(string);
      }

      this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((java.lang.String[])(arrayList.toArray(new String[arrayList.size()]))));
   }

   @Override
   public void processClientSettings(C15PacketClientSettings packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.handleClientSettings(packetIn);
   }

   @Override
   public void processVanilla250Packet(C17PacketCustomPayload packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if ("MC|BEdit".equals(packetIn.getChannelName())) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

         try {
            ItemStack itemStack = packetBuffer.readItemStackFromBuffer();
            if (itemStack == null) {
               return;
            }

            if (!ItemWritableBook.isNBTValid(itemStack.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack itemStack2 = this.playerEntity.inventory.getCurrentItem();
            if (itemStack2 == null) {
               return;
            }

            if (itemStack.getItem() == Items.writable_book && itemStack.getItem() == itemStack2.getItem()) {
               itemStack2.setTagInfo("pages", itemStack.getTagCompound().getTagList("pages", 8));
            }
         } catch (Exception exception) {
            logger.error("Couldn't handle book info", exception);
            return;
         } finally {
            packetBuffer.release();
         }
      } else if ("MC|BSign".equals(packetIn.getChannelName())) {
         PacketBuffer packetBuffer2 = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

         try {
            ItemStack itemStack3 = packetBuffer2.readItemStackFromBuffer();
            if (itemStack3 == null) {
               return;
            }

            if (!ItemEditableBook.validBookTagContents(itemStack3.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack itemStack4 = this.playerEntity.inventory.getCurrentItem();
            if (itemStack4 == null) {
               return;
            }

            if (itemStack3.getItem() == Items.written_book && itemStack4.getItem() == Items.writable_book) {
               itemStack4.setTagInfo("author", new NBTTagString(this.playerEntity.getName()));
               itemStack4.setTagInfo("title", new NBTTagString(itemStack3.getTagCompound().getString("title")));
               itemStack4.setTagInfo("pages", itemStack3.getTagCompound().getTagList("pages", 8));
               itemStack4.setItem(Items.written_book);
            }
         } catch (Exception exception2) {
            logger.error("Couldn't sign book", exception2);
            return;
         } finally {
            packetBuffer2.release();
         }
      } else {
         if ("MC|TrSel".equals(packetIn.getChannelName())) {
            try {
               int i = packetIn.getBufferData().readInt();
               Container container = this.playerEntity.openContainer;
               if (container instanceof ContainerMerchant) {
                  ((ContainerMerchant)container).setCurrentRecipeIndex(i);
               }
            } catch (Exception exception3) {
               logger.error("Couldn't select trade", exception3);
            }
         } else if ("MC|AdvCdm".equals(packetIn.getChannelName())) {
            if (!this.serverController.isCommandBlockEnabled()) {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
            } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
               PacketBuffer packetBuffer3 = packetIn.getBufferData();

               try {
                  byte b = packetBuffer3.readByte();
                  CommandBlockLogic commandBlockLogic = null;
                  if (b == 0) {
                     TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(new BlockPos(packetBuffer3.readInt(), packetBuffer3.readInt(), packetBuffer3.readInt()));
                     if (tileEntity instanceof TileEntityCommandBlock) {
                        commandBlockLogic = ((TileEntityCommandBlock)tileEntity).getCommandBlockLogic();
                     }
                  } else if (b == 1) {
                     Entity entity = this.playerEntity.worldObj.getEntityByID(packetBuffer3.readInt());
                     if (entity instanceof EntityMinecartCommandBlock) {
                        commandBlockLogic = ((EntityMinecartCommandBlock)entity).getCommandBlockLogic();
                     }
                  }

                  String string = packetBuffer3.readStringFromBuffer(packetBuffer3.readableBytes());
                  boolean bl = packetBuffer3.readBoolean();
                  if (commandBlockLogic != null) {
                     commandBlockLogic.setCommand(string);
                     commandBlockLogic.setTrackOutput(bl);
                     if (!bl) {
                        commandBlockLogic.setLastOutput((IChatComponent)null);
                     }

                     commandBlockLogic.updateCommand();
                     this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", string));
                  }
               } catch (Exception exception4) {
                  logger.error("Couldn't set command block", exception4);
               } finally {
                  packetBuffer3.release();
               }
            } else {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
            }
         } else if ("MC|Beacon".equals(packetIn.getChannelName())) {
            if (this.playerEntity.openContainer instanceof ContainerBeacon) {
               try {
                  PacketBuffer packetBuffer4 = packetIn.getBufferData();
                  int j = packetBuffer4.readInt();
                  int k = packetBuffer4.readInt();
                  ContainerBeacon containerBeacon = (ContainerBeacon)this.playerEntity.openContainer;
                  Slot slot = containerBeacon.getSlot(0);
                  if (slot.getHasStack()) {
                     slot.decrStackSize(1);
                     IInventory iInventory = containerBeacon.func_180611_e();
                     iInventory.setField(1, j);
                     iInventory.setField(2, k);
                     iInventory.markDirty();
                  }
               } catch (Exception exception5) {
                  logger.error("Couldn't set beacon", exception5);
               }
            }
         } else if ("MC|ItemName".equals(packetIn.getChannelName()) && this.playerEntity.openContainer instanceof ContainerRepair) {
            ContainerRepair containerRepair = (ContainerRepair)this.playerEntity.openContainer;
            if (packetIn.getBufferData() != null && packetIn.getBufferData().readableBytes() >= 1) {
               String string2 = ChatAllowedCharacters.filterAllowedCharacters(packetIn.getBufferData().readStringFromBuffer(32767));
               if (string2.length() <= 30) {
                  containerRepair.updateItemName(string2);
               }
            } else {
               containerRepair.updateItemName("");
            }
         }
      }
   }
}
