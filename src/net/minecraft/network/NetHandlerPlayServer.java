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
   private static final long a = 82462100243065L;

   public NetHandlerPlayServer(MinecraftServer var1, NetworkManager var2, EntityPlayerMP var3) {
      this.serverController = var1;
      this.netManager = var2;
      var2.setNetHandler(this);
      this.playerEntity = var3;
      var3.playerNetServerHandler = this;
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

   public void kickPlayerFromServer(String var1) {
      final ChatComponentText var2 = new ChatComponentText(var1);
      this.netManager.sendPacket(new S40PacketDisconnect(var2), new GenericFutureListener<Future<? super Void>>() {
         @Override
         public void operationComplete(Future<? super Void> var1) throws Exception {
            NetHandlerPlayServer.this.netManager.closeChannel(var2);
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
   public void processInput(C0CPacketInput var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.setEntityActionState(var1.getStrafeSpeed(), var1.getForwardSpeed(), var1.isJumping(), var1.isSneaking());
   }

   private boolean func_183006_b(C03PacketPlayer var1) {
      return !Doubles.isFinite(var1.getPositionX())
         || !Doubles.isFinite(var1.getPositionY())
         || !Doubles.isFinite(var1.getPositionZ())
         || !Floats.isFinite(var1.getPitch())
         || !Floats.isFinite(var1.getYaw());
   }

   @Override
   public void processPlayer(C03PacketPlayer var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if (this.func_183006_b(var1)) {
         this.kickPlayerFromServer("Invalid move packet received");
      } else {
         WorldServer var5 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
         this.field_147366_g = true;
         if (!this.playerEntity.playerConqueredTheEnd) {
            double var6 = this.playerEntity.posX;
            double var8 = this.playerEntity.posY;
            double var10 = this.playerEntity.posZ;
            double var12 = 0.0;
            double var14 = var1.getPositionX() - this.lastPosX;
            double var16 = var1.getPositionY() - this.lastPosY;
            double var18 = var1.getPositionZ() - this.lastPosZ;
            if (var1.isMoving()) {
               var12 = var14 * var14 + var16 * var16 + var18 * var18;
               if (!this.hasMoved && var12 < 0.25) {
                  this.hasMoved = true;
               }
            }

            if (this.hasMoved) {
               this.field_175090_f = this.networkTickCount;
               if (this.playerEntity.ridingEntity != null) {
                  float var44 = this.playerEntity.rotationYaw;
                  float var21 = this.playerEntity.rotationPitch;
                  this.playerEntity.ridingEntity.updateRiderPosition();
                  double var45 = this.playerEntity.posX;
                  double var46 = this.playerEntity.posY;
                  double var47 = this.playerEntity.posZ;
                  if (var1.getRotating()) {
                     var44 = var1.getYaw();
                     var21 = var1.getPitch();
                  }

                  this.playerEntity.onGround = var1.isOnGround();
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.setPositionAndRotation(var45, var46, var47, var44, var21);
                  if (this.playerEntity.ridingEntity != null) {
                     this.playerEntity.ridingEntity.updateRiderPosition();
                  }

                  this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                  if (this.playerEntity.ridingEntity != null) {
                     if (var12 > 4.0) {
                        Entity var48 = this.playerEntity.ridingEntity;
                        this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(var48));
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

                  var5.updateEntity(this.playerEntity);
                  return;
               }

               if (this.playerEntity.isPlayerSleeping()) {
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity
                     .setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  var5.updateEntity(this.playerEntity);
                  return;
               }

               double var20 = this.playerEntity.posY;
               this.lastPosX = this.playerEntity.posX;
               this.lastPosY = this.playerEntity.posY;
               this.lastPosZ = this.playerEntity.posZ;
               double var22 = this.playerEntity.posX;
               double var24 = this.playerEntity.posY;
               double var26 = this.playerEntity.posZ;
               float var28 = this.playerEntity.rotationYaw;
               float var29 = this.playerEntity.rotationPitch;
               if (var1.isMoving() && var1.getPositionY() == -999.0) {
                  var1.setMoving(false);
               }

               if (var1.isMoving()) {
                  var22 = var1.getPositionX();
                  var24 = var1.getPositionY();
                  var26 = var1.getPositionZ();
                  if (Math.abs(var1.getPositionX()) > 3.0E7 || Math.abs(var1.getPositionZ()) > 3.0E7) {
                     this.kickPlayerFromServer("Illegal position");
                     return;
                  }
               }

               if (var1.getRotating()) {
                  var28 = var1.getYaw();
                  var29 = var1.getPitch();
               }

               this.playerEntity.onUpdateEntity();
               this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var28, var29);
               if (!this.hasMoved) {
                  return;
               }

               double var30 = var22 - this.playerEntity.posX;
               double var32 = var24 - this.playerEntity.posY;
               double var34 = var26 - this.playerEntity.posZ;
               double var36 = this.playerEntity.motionX * this.playerEntity.motionX
                  + this.playerEntity.motionY * this.playerEntity.motionY
                  + this.playerEntity.motionZ * this.playerEntity.motionZ;
               double var38 = var30 * var30 + var32 * var32 + var34 * var34;
               if (var38 - var36 > 100.0
                  && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getName()))) {
                  logger.warn(
                     this.playerEntity.getName()
                        + " moved too quickly! "
                        + var30
                        + ","
                        + var32
                        + ","
                        + var34
                        + " ("
                        + var30
                        + ", "
                        + var32
                        + ", "
                        + var34
                        + ")"
                  );
                  this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  return;
               }

               float var40 = 0.0625F;
               boolean var41 = var5.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(var40, var40, var40))
                  .isEmpty();
               if (this.playerEntity.onGround && !var1.isOnGround() && var32 > 0.0) {
                  this.playerEntity.jump();
               }

               this.playerEntity.moveEntity(var30, var32, var34);
               this.playerEntity.onGround = var1.isOnGround();
               var30 = var22 - this.playerEntity.posX;
               var32 = var24 - this.playerEntity.posY;
               if (var32 > -0.5 || var32 < 0.5) {
                  var32 = 0.0;
               }

               var34 = var26 - this.playerEntity.posZ;
               var38 = var30 * var30 + var32 * var32 + var34 * var34;
               boolean var42 = false;
               if (var38 > 0.0625 && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                  var42 = true;
                  logger.warn(this.playerEntity.getName() + " moved wrongly!");
               }

               this.playerEntity.setPositionAndRotation(var22, var24, var26, var28, var29);
               this.playerEntity.addMovementStat(this.playerEntity.posX - var6, this.playerEntity.posY - var8, this.playerEntity.posZ - var10);
               if (!this.playerEntity.noClip) {
                  boolean var43 = var5.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(var40, var40, var40))
                     .isEmpty();
                  if (var41 && (var42 || !var43) && !this.playerEntity.isPlayerSleeping()) {
                     this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, var28, var29);
                     return;
                  }
               }

               AxisAlignedBB var53 = this.playerEntity.getEntityBoundingBox().expand(var40, var40, var40).addCoord(0.0, -0.55, 0.0);
               if (this.serverController.isFlightAllowed() || this.playerEntity.capabilities.allowFlying || var5.X(var53)) {
                  this.floatingTickCount = 0;
               } else if (var32 >= -0.03125) {
                  this.floatingTickCount++;
                  if (this.floatingTickCount > 80) {
                     logger.warn(this.playerEntity.getName() + " was kicked for floating too long!");
                     this.kickPlayerFromServer("Flying is not enabled on this server");
                     return;
                  }
               }

               this.playerEntity.onGround = var1.isOnGround();
               this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
               this.playerEntity.handleFalling(this.playerEntity.posY - var20, var1.isOnGround());
            } else if (this.networkTickCount - this.field_175090_f > 20) {
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
         }
      }
   }

   public void setPlayerLocation(double var1, double var3, double var5, float var7, float var8) {
      this.setPlayerLocation(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void setPlayerLocation(double var1, double var3, double var5, float var7, float var8, Set<S08PacketPlayerPosLook.EnumFlags> var9) {
      this.hasMoved = false;
      this.lastPosX = var1;
      this.lastPosY = var3;
      this.lastPosZ = var5;
      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         this.lastPosX = this.lastPosX + this.playerEntity.posX;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         this.lastPosY = this.lastPosY + this.playerEntity.posY;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         this.lastPosZ = this.lastPosZ + this.playerEntity.posZ;
      }

      float var14 = var7;
      float var15 = var8;
      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         var14 = var7 + this.playerEntity.rotationYaw;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         var15 = var8 + this.playerEntity.rotationPitch;
      }

      this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var14, var15);
      this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(var1, var3, var5, var7, var8, var9));
   }

   @Override
   public void processPlayerDigging(C07PacketPlayerDigging var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      WorldServer var5 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos var6 = var1.getPosition();
      this.playerEntity.markPlayerActive();
      switch (var1.getStatus()) {
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
            double var7 = this.playerEntity.posX - (var6.getX() + 0.5);
            double var9 = this.playerEntity.posY - (var6.getY() + 0.5) + 1.5;
            double var11 = this.playerEntity.posZ - (var6.getZ() + 0.5);
            double var13 = var7 * var7 + var9 * var9 + var11 * var11;
            if (var13 > 36.0) {
               return;
            } else if (var6.getY() >= this.serverController.getBuildLimit()) {
               return;
            } else {
               if (var1.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  if (!this.serverController.isBlockProtected(var5, var6, this.playerEntity) && var5.getWorldBorder().contains(var6)) {
                     this.playerEntity.theItemInWorldManager.onBlockClicked(var6, var1.getFacing());
                  } else {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(var5, var6));
                  }
               } else {
                  if (var1.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.blockRemoving(var6);
                  } else if (var1.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                  }

                  if (var5.getBlockState(var6).getBlock().getMaterial() != Material.air) {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(var5, var6));
                  }
               }

               return;
            }
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   @Override
   public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      WorldServer var5 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      ItemStack var6 = this.playerEntity.inventory.getCurrentItem();
      boolean var7 = false;
      BlockPos var8 = var1.getPosition();
      EnumFacing var9 = EnumFacing.getFront(var1.getPlacedBlockDirection());
      this.playerEntity.markPlayerActive();
      if (var1.getPlacedBlockDirection() == 255) {
         if (var6 == null) {
            return;
         }

         this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, var5, var6);
      } else if (var8.getY() < this.serverController.getBuildLimit() - 1 || var9 != EnumFacing.UP && var8.getY() < this.serverController.getBuildLimit()) {
         if (this.hasMoved
            && this.playerEntity.getDistanceSq(var8.getX() + 0.5, var8.getY() + 0.5, var8.getZ() + 0.5) < 64.0
            && !this.serverController.isBlockProtected(var5, var8, this.playerEntity)
            && var5.getWorldBorder().contains(var8)) {
            this.playerEntity
               .theItemInWorldManager
               .activateBlockOrUseItem(
                  this.playerEntity, var5, var6, var8, var9, var1.getPlacedBlockOffsetX(), var1.getPlacedBlockOffsetY(), var1.getPlacedBlockOffsetZ()
               );
         }

         var7 = true;
      } else {
         ChatComponentTranslation var10 = new ChatComponentTranslation("build.tooHigh", this.serverController.getBuildLimit());
         var10.getChatStyle().setColor(EnumChatFormatting.RED);
         this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(var10));
         var7 = true;
      }

      if (var7) {
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(var5, var8));
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(var5, var8.offset(var9)));
      }

      var6 = this.playerEntity.inventory.getCurrentItem();
      if (var6 != null && var6.stackSize == 0) {
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
         var6 = null;
      }

      if (var6 == null || var6.getMaxItemUseDuration() == 0) {
         this.playerEntity.isChangingQuantityOnly = true;
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]
         );
         Slot var12 = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
         this.playerEntity.openContainer.detectAndSendChanges();
         this.playerEntity.isChangingQuantityOnly = false;
         if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), var1.getStack())) {
            this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, var12.slotNumber, this.playerEntity.inventory.getCurrentItem()));
         }
      }
   }

   @Override
   public void handleSpectate(C18PacketSpectate var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.isSpectator()) {
         Entity var5 = null;

         for (WorldServer var9 : this.serverController.worldServers) {
            if (var9 != null) {
               var5 = var1.getEntity(var9);
               if (var5 != null) {
                  break;
               }
            }
         }

         if (var5 != null) {
            this.playerEntity.setSpectatingEntity(this.playerEntity);
            this.playerEntity.mountEntity((Entity)null);
            if (var5.worldObj != this.playerEntity.worldObj) {
               WorldServer var10 = this.playerEntity.getServerForPlayer();
               WorldServer var11 = (WorldServer)var5.worldObj;
               this.playerEntity.dimension = var5.dimension;
               this.sendPacket(
                  new S07PacketRespawn(
                     this.playerEntity.dimension,
                     var10.getDifficulty(),
                     var10.getWorldInfo().getTerrainType(),
                     this.playerEntity.theItemInWorldManager.getGameType()
                  )
               );
               var10.removePlayerEntityDangerously(this.playerEntity);
               this.playerEntity.isDead = false;
               this.playerEntity.setLocationAndAngles(var5.posX, var5.posY, var5.posZ, var5.rotationYaw, var5.rotationPitch);
               if (this.playerEntity.isEntityAlive()) {
                  var10.updateEntityWithOptionalForce(this.playerEntity, false);
                  var11.spawnEntityInWorld(this.playerEntity);
                  var11.updateEntityWithOptionalForce(this.playerEntity, false);
               }

               this.playerEntity.setWorld(var11);
               this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, var10);
               this.playerEntity.setPositionAndUpdate(var5.posX, var5.posY, var5.posZ);
               this.playerEntity.theItemInWorldManager.setWorld(var11);
               this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, var11);
               this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
            } else {
               this.playerEntity.setPositionAndUpdate(var5.posX, var5.posY, var5.posZ);
            }
         }
      }
   }

   @Override
   public void handleResourcePackStatus(C19PacketResourcePackStatus var1) {
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
      logger.info(this.playerEntity.getName() + " lost connection: " + var1);
      this.serverController.refreshStatusNextTick();
      ChatComponentTranslation var5 = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
      var5.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.serverController.getConfigurationManager().sendChatMsg(var5);
      this.playerEntity.mountEntityAndWakeUp();
      this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);
      if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
         logger.info("Stopping singleplayer server as player logged out");
         this.serverController.initiateShutdown();
      }
   }

   public void sendPacket(final Packet var1) {
      if (var1 instanceof S02PacketChat) {
         S02PacketChat var5 = (S02PacketChat)var1;
         EntityPlayer.EnumChatVisibility var6 = this.playerEntity.getChatVisibility();
         if (var6 == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
         }

         if (var6 == EntityPlayer.EnumChatVisibility.SYSTEM && !var5.isChat()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(var1);
      } catch (Throwable var8) {
         CrashReport var9 = CrashReport.makeCrashReport(var8, "Sending packet");
         CrashReportCategory var7 = var9.makeCategory("Packet being sent");
         var7.addCrashSectionCallable("Packet class", new Callable<String>() {
            public String call() throws Exception {
               return var1.getClass().getCanonicalName();
            }
         });
         throw new ReportedException(var9);
      }
   }

   @Override
   public void processHeldItemChange(C09PacketHeldItemChange var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if (var1.getSlotId() >= 0 && var1.getSlotId() < InventoryPlayer.getHotbarSize()) {
         this.playerEntity.inventory.currentItem = var1.getSlotId();
         this.playerEntity.markPlayerActive();
      } else {
         logger.warn(this.playerEntity.getName() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void processChatMessage(C01PacketChatMessage var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation var5 = new ChatComponentTranslation("chat.cannotSend");
         var5.getChatStyle().setColor(EnumChatFormatting.RED);
         this.sendPacket(new S02PacketChat(var5));
      } else {
         this.playerEntity.markPlayerActive();
         String var7 = var1.getMessage();
         var7 = StringUtils.normalizeSpace(var7);

         for (int var6 = 0; var6 < var7.length(); var6++) {
            if (!ChatAllowedCharacters.isAllowedCharacter(var7.charAt(var6))) {
               this.kickPlayerFromServer("Illegal characters in chat");
               return;
            }
         }

         if (var7.startsWith("/")) {
            this.handleSlashCommand(var7);
         } else {
            ChatComponentTranslation var9 = new ChatComponentTranslation("chat.type.text", this.playerEntity.getDisplayName(), var7);
            this.serverController.getConfigurationManager().sendChatMsgImpl(var9, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())) {
            this.kickPlayerFromServer("disconnect.spam");
         }
      }
   }

   private void handleSlashCommand(String var1) {
      this.serverController.getCommandManager().executeCommand(this.playerEntity, var1);
   }

   @Override
   public void handleAnimation(C0APacketAnimation var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      this.playerEntity.swingItem();
   }

   @Override
   public void processEntityAction(C0BPacketEntityAction var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      switch (var1.getAction()) {
         case START_SNEAKING:
            this.playerEntity.v(true);
            break;
         case STOP_SNEAKING:
            this.playerEntity.v(false);
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
               ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(var1.getAuxData());
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
   public void processUseEntity(C02PacketUseEntity var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      WorldServer var5 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      Entity var6 = var1.getEntityFromWorld(var5);
      this.playerEntity.markPlayerActive();
      if (var6 != null) {
         boolean var7 = this.playerEntity.C(var6);
         double var8 = 36.0;
         if (!var7) {
            var8 = 9.0;
         }

         if (this.playerEntity.getDistanceSqToEntity(var6) < var8) {
            if (var1.getAction() == C02PacketUseEntity.Action.INTERACT) {
               this.playerEntity.interactWith(var6);
            } else if (var1.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
               var6.interactAt(this.playerEntity, var1.getHitVec());
            } else if (var1.getAction() == C02PacketUseEntity.Action.ATTACK) {
               if (var6 instanceof EntityItem || var6 instanceof EntityXPOrb || var6 instanceof EntityArrow || var6 == this.playerEntity) {
                  this.kickPlayerFromServer("Attempting to attack an invalid entity");
                  this.serverController.logWarning("Player " + this.playerEntity.getName() + " tried to attack an invalid entity");
                  return;
               }

               this.playerEntity.attackTargetEntityWithCurrentItem(var6);
            }
         }
      }
   }

   @Override
   public void processClientStatus(C16PacketClientStatus var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      C16PacketClientStatus.EnumState var5 = var1.getStatus();
      switch (var5) {
         case PERFORM_RESPAWN:
            if (this.playerEntity.playerConqueredTheEnd) {
               this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
            } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
               if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                  this.serverController.deleteWorldAndStopServer();
               } else {
                  UserListBansEntry var6 = new UserListBansEntry(
                     this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore"
                  );
                  this.serverController.getConfigurationManager().getBannedPlayers().addEntry(var6);
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
   public void processCloseWindow(C0DPacketCloseWindow var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.closeContainer();
   }

   @Override
   public void processClickWindow(C0EPacketClickWindow var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == var1.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
         if (this.playerEntity.isSpectator()) {
            ArrayList var5 = Lists.newArrayList();

            for (int var6 = 0; var6 < this.playerEntity.openContainer.inventorySlots.size(); var6++) {
               var5.add(this.playerEntity.openContainer.inventorySlots.get(var6).getStack());
            }

            this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, var5);
         } else {
            ItemStack var8 = this.playerEntity.openContainer.slotClick(var1.getSlotId(), var1.getUsedButton(), var1.getMode(), this.playerEntity);
            if (ItemStack.areItemStacksEqual(var1.getClickedItem(), var8)) {
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(var1.getWindowId(), var1.getActionNumber(), true));
               this.playerEntity.isChangingQuantityOnly = true;
               this.playerEntity.openContainer.detectAndSendChanges();
               this.playerEntity.updateHeldItem();
               this.playerEntity.isChangingQuantityOnly = false;
            } else {
               this.field_147372_n.addKey(this.playerEntity.openContainer.windowId, var1.getActionNumber());
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(var1.getWindowId(), var1.getActionNumber(), false));
               this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
               ArrayList var9 = Lists.newArrayList();

               for (int var7 = 0; var7 < this.playerEntity.openContainer.inventorySlots.size(); var7++) {
                  var9.add(this.playerEntity.openContainer.inventorySlots.get(var7).getStack());
               }

               this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, var9);
            }
         }
      }
   }

   @Override
   public void processEnchantItem(C11PacketEnchantItem var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == var1.getWindowId()
         && this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.enchantItem(this.playerEntity, var1.getButton());
         this.playerEntity.openContainer.detectAndSendChanges();
      }
   }

   @Override
   public void processCreativeInventoryAction(C10PacketCreativeInventoryAction var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.theItemInWorldManager.isCreative()) {
         boolean var5 = var1.getSlotId() < 0;
         ItemStack var6 = var1.getStack();
         if (var6 != null && var6.hasTagCompound() && var6.getTagCompound().hasKey("BlockEntityTag", 10)) {
            NBTTagCompound var7 = var6.getTagCompound().getCompoundTag("BlockEntityTag");
            if (var7.hasKey("x") && var7.hasKey("y") && var7.hasKey("z")) {
               BlockPos var8 = new BlockPos(var7.getInteger("x"), var7.getInteger("y"), var7.getInteger("z"));
               TileEntity var9 = this.playerEntity.worldObj.getTileEntity(var8);
               if (var9 != null) {
                  NBTTagCompound var10 = new NBTTagCompound();
                  var9.writeToNBT(var10);
                  var10.removeTag("x");
                  var10.removeTag("y");
                  var10.removeTag("z");
                  var6.setTagInfo("BlockEntityTag", var10);
               }
            }
         }

         boolean var11 = var1.getSlotId() >= 1 && var1.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
         boolean var12 = var6 == null || var6.getItem() != null;
         boolean var13 = var6 == null || var6.getMetadata() >= 0 && var6.stackSize <= 64 && var6.stackSize > 0;
         if (var11 && var12 && var13) {
            if (var6 == null) {
               this.playerEntity.inventoryContainer.putStackInSlot(var1.getSlotId(), (ItemStack)null);
            } else {
               this.playerEntity.inventoryContainer.putStackInSlot(var1.getSlotId(), var6);
            }

            this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
         } else if (var5 && var12 && var13 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            EntityItem var14 = this.playerEntity.dropPlayerItemWithRandomChoice(var6, true);
            if (var14 != null) {
               var14.setAgeToCreativeDespawnTime();
            }
         }
      }
   }

   @Override
   public void processConfirmTransaction(C0FPacketConfirmTransaction var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      Short var5 = this.field_147372_n.lookup(this.playerEntity.openContainer.windowId);
      if (var5 != null
         && var1.getUid() == var5
         && this.playerEntity.openContainer.windowId == var1.getWindowId()
         && !this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
      }
   }

   @Override
   public void processUpdateSign(C12PacketUpdateSign var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      WorldServer var5 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos var6 = var1.getPosition();
      if (var5.isBlockLoaded(var6)) {
         TileEntity var7 = var5.getTileEntity(var6);
         if (!(var7 instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign var8 = (TileEntitySign)var7;
         if (!var8.getIsEditable() || var8.getPlayer() != this.playerEntity) {
            this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
            return;
         }

         IChatComponent[] var9 = var1.getLines();

         for (int var10 = 0; var10 < var9.length; var10++) {
            var8.signText[var10] = new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(var9[var10].getUnformattedText()));
         }

         var8.markDirty();
         var5.markBlockForUpdate(var6);
      }
   }

   @Override
   public void processKeepAlive(C00PacketKeepAlive var1) {
      if (var1.getKey() == this.field_147378_h) {
         int var5 = (int)(this.currentTimeMillis() - this.lastPingTime);
         this.playerEntity.ping = (this.playerEntity.ping * 3 + var5) / 4;
      }
   }

   private long currentTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void processPlayerAbilities(C13PacketPlayerAbilities var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.capabilities.isFlying = var1.isFlying() && this.playerEntity.capabilities.allowFlying;
   }

   @Override
   public void processTabComplete(C14PacketTabComplete var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      ArrayList var5 = Lists.newArrayList();

      for (String var7 : this.serverController.getTabCompletions(this.playerEntity, var1.getMessage(), var1.getTargetBlock())) {
         var5.add(var7);
      }

      this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((java.lang.String[])(var5.toArray(new String[var5.size()]))));
   }

   @Override
   public void processClientSettings(C15PacketClientSettings var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.handleClientSettings(var1);
   }

   @Override
   public void processVanilla250Packet(C17PacketCustomPayload var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.playerEntity.getServerForPlayer());
      if ("MC|BEdit".equals(var1.getChannelName())) {
         PacketBuffer var47 = new PacketBuffer(Unpooled.wrappedBuffer(var1.getBufferData()));

         try {
            ItemStack var52 = var47.readItemStackFromBuffer();
            if (var52 == null) {
               return;
            }

            if (!ItemWritableBook.isNBTValid(var52.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var55 = this.playerEntity.inventory.getCurrentItem();
            if (var55 == null) {
               return;
            }

            if (var52.getItem() == Items.writable_book && var52.getItem() == var55.getItem()) {
               var55.setTagInfo("pages", var52.getTagCompound().getTagList("pages", 8));
            }
         } catch (Exception var39) {
            logger.error("Couldn't handle book info", var39);
            return;
         } finally {
            var47.release();
         }
      } else if ("MC|BSign".equals(var1.getChannelName())) {
         PacketBuffer var46 = new PacketBuffer(Unpooled.wrappedBuffer(var1.getBufferData()));

         try {
            ItemStack var51 = var46.readItemStackFromBuffer();
            if (var51 == null) {
               return;
            }

            if (!ItemEditableBook.validBookTagContents(var51.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var54 = this.playerEntity.inventory.getCurrentItem();
            if (var54 == null) {
               return;
            }

            if (var51.getItem() == Items.written_book && var54.getItem() == Items.writable_book) {
               var54.setTagInfo("author", new NBTTagString(this.playerEntity.getName()));
               var54.setTagInfo("title", new NBTTagString(var51.getTagCompound().getString("title")));
               var54.setTagInfo("pages", var51.getTagCompound().getTagList("pages", 8));
               var54.setItem(Items.written_book);
            }
         } catch (Exception var41) {
            logger.error("Couldn't sign book", var41);
            return;
         } finally {
            var46.release();
         }
      } else {
         if ("MC|TrSel".equals(var1.getChannelName())) {
            try {
               int var5 = var1.getBufferData().readInt();
               Container var6 = this.playerEntity.openContainer;
               if (var6 instanceof ContainerMerchant) {
                  ((ContainerMerchant)var6).setCurrentRecipeIndex(var5);
               }
            } catch (Exception var38) {
               logger.error("Couldn't select trade", var38);
            }
         } else if ("MC|AdvCdm".equals(var1.getChannelName())) {
            if (!this.serverController.isCommandBlockEnabled()) {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
            } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
               PacketBuffer var43 = var1.getBufferData();

               try {
                  byte var48 = var43.readByte();
                  CommandBlockLogic var7 = null;
                  if (var48 == 0) {
                     TileEntity var8 = this.playerEntity.worldObj.getTileEntity(new BlockPos(var43.readInt(), var43.readInt(), var43.readInt()));
                     if (var8 instanceof TileEntityCommandBlock) {
                        var7 = ((TileEntityCommandBlock)var8).getCommandBlockLogic();
                     }
                  } else if (var48 == 1) {
                     Entity var56 = this.playerEntity.worldObj.getEntityByID(var43.readInt());
                     if (var56 instanceof EntityMinecartCommandBlock) {
                        var7 = ((EntityMinecartCommandBlock)var56).getCommandBlockLogic();
                     }
                  }

                  String var57 = var43.readStringFromBuffer(var43.readableBytes());
                  boolean var9 = var43.readBoolean();
                  if (var7 != null) {
                     var7.setCommand(var57);
                     var7.setTrackOutput(var9);
                     if (!var9) {
                        var7.setLastOutput((IChatComponent)null);
                     }

                     var7.updateCommand();
                     this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", var57));
                  }
               } catch (Exception var36) {
                  logger.error("Couldn't set command block", var36);
               } finally {
                  var43.release();
               }
            } else {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
            }
         } else if ("MC|Beacon".equals(var1.getChannelName())) {
            if (this.playerEntity.openContainer instanceof ContainerBeacon) {
               try {
                  PacketBuffer var44 = var1.getBufferData();
                  int var49 = var44.readInt();
                  int var53 = var44.readInt();
                  ContainerBeacon var58 = (ContainerBeacon)this.playerEntity.openContainer;
                  Slot var59 = var58.getSlot(0);
                  if (var59.getHasStack()) {
                     var59.decrStackSize(1);
                     IInventory var10 = var58.func_180611_e();
                     var10.setField(1, var49);
                     var10.setField(2, var53);
                     var10.markDirty();
                  }
               } catch (Exception var35) {
                  logger.error("Couldn't set beacon", var35);
               }
            }
         } else if ("MC|ItemName".equals(var1.getChannelName()) && this.playerEntity.openContainer instanceof ContainerRepair) {
            ContainerRepair var45 = (ContainerRepair)this.playerEntity.openContainer;
            if (var1.getBufferData() != null && var1.getBufferData().readableBytes() >= 1) {
               String var50 = ChatAllowedCharacters.filterAllowedCharacters(var1.getBufferData().readStringFromBuffer(32767));
               if (var50.length() <= 30) {
                  var45.updateItemName(var50);
               }
            } else {
               var45.updateItemName("");
            }
         }
      }
   }
}
