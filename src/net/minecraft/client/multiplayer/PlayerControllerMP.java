package net.minecraft.client.multiplayer;

import bypass.Astolfo;
import bypass.event.EventState;
import bypass.event.player.AttackEvent;
import bypass.event.world.AbortBreakingEvent;
import bypass.event.world.BlockBrokenEvent;
import bypass.event.world.DamageBlockEvent;
import bypass.event.world.DestroyBlockEvent;
import bypass.event.world.RightClickBlockEvent;
import bypass.module.ModuleManager;
import bypass.module.other.InfiniteReach;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerControllerMP {
   private final Minecraft mc;
   public final NetHandlerPlayClient netClientHandler;
   private BlockPos currentBlock = new BlockPos(-1, -1, -1);
   private ItemStack currentItemHittingBlock;
   public float curBlockDamageMP;
   private float stepSoundTickCounter;
   public int blockHitDelay;
   public boolean isHittingBlock;
   public WorldSettings.GameType currentGameType = WorldSettings.GameType.SURVIVAL;
   public int currentPlayerItem;

   public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      this.mc = mcIn;
      this.netClientHandler = netHandler;
   }

   public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
      if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, pos, facing)) {
         playerController.onPlayerDestroyBlock(pos, facing);
      }
   }

   public void setPlayerCapabilities(EntityPlayer player) {
      this.currentGameType.configurePlayerCapabilities(player.capabilities);
   }

   public boolean isSpectator() {
      return this.currentGameType == WorldSettings.GameType.SPECTATOR;
   }

   public void setGameType(WorldSettings.GameType typeIn) {
      this.currentGameType = typeIn;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public void flipPlayer(EntityPlayer playerIn) {
      playerIn.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side) {
      DestroyBlockEvent destroyBlockEvent = new DestroyBlockEvent();
      Astolfo.INSTANCE.getEventBus().post(destroyBlockEvent);
      if (destroyBlockEvent.isCancelled()) {
         return false;
      }

      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            ItemStack itemStack = this.mc.thePlayer.getCurrentEquippedItem();
            if (itemStack == null) {
               return false;
            }

            if (!itemStack.canDestroy(block)) {
               return false;
            }
         }
      }

      if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      WorldClient worldClient = this.mc.theWorld;
      IBlockState iBlockState = worldClient.getBlockState(pos);
      Block block2 = iBlockState.getBlock();
      if (block2.getMaterial() == Material.air) {
         return false;
      }

      worldClient.playAuxSFX(2001, pos, Block.getStateId(iBlockState));
      boolean bl = worldClient.setBlockToAir(pos);
      if (bl) {
         block2.onBlockDestroyedByPlayer(worldClient, pos, iBlockState);
      }

      this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
      if (!this.currentGameType.isCreative()) {
         ItemStack itemStack2 = this.mc.thePlayer.getCurrentEquippedItem();
         if (itemStack2 != null) {
            itemStack2.onBlockDestroyed(worldClient, block2, pos, this.mc.thePlayer);
            if (itemStack2.stackSize == 0) {
               this.mc.thePlayer.destroyCurrentEquippedItem();
            }
         }
      }

      return bl;
   }

   public boolean clickBlock(BlockPos loc, EnumFacing face) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(loc).getBlock();
            ItemStack itemStack = this.mc.thePlayer.getCurrentEquippedItem();
            if (itemStack == null) {
               return false;
            }

            if (!itemStack.canDestroy(block)) {
               return false;
            }
         }
      }

      if (!this.mc.theWorld.getWorldBorder().contains(loc)) {
         return false;
      }

      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         clickBlockCreative(this.mc, this, loc, face);
         this.blockHitDelay = 5;
      } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
         if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
         }

         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         Block block2 = this.mc.theWorld.getBlockState(loc).getBlock();
         boolean bl = block2.getMaterial() != Material.air;
         if (bl && this.curBlockDamageMP == 0.0F) {
            block2.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
         }

         if (bl && block2.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F) {
            this.onPlayerDestroyBlock(loc, face);
         } else {
            this.isHittingBlock = true;
            this.currentBlock = loc;
            this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         }
      }

      return true;
   }

   public void resetBlockRemoving() {
      AbortBreakingEvent abortBreakingEvent = new AbortBreakingEvent(!this.isHittingBlock);
      Astolfo.INSTANCE.getEventBus().post(abortBreakingEvent);
      if (!abortBreakingEvent.isCancelled()) {
         this.netClientHandler
            .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
      }
   }

   public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
      DamageBlockEvent damageBlockEvent = new DamageBlockEvent(this.mc.theWorld.getBlockState(posBlock).getBlock(), posBlock, 4, 1.0);
      Astolfo.INSTANCE.getEventBus().post(damageBlockEvent);
      if (damageBlockEvent.isCancelled()) {
         return false;
      }

      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         this.blockHitDelay--;
         return true;
      }

      if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock)) {
         this.blockHitDelay = damageBlockEvent.getHitDelay();
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
         clickBlockCreative(this.mc, this, posBlock, directionFacing);
         return true;
      }

      if (this.isHittingPosition(posBlock)) {
         Block block = this.mc.theWorld.getBlockState(posBlock).getBlock();
         if (block.getMaterial() == Material.air) {
            this.isHittingBlock = false;
            return false;
         }

         this.curBlockDamageMP = (float)(
            this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock) * damageBlockEvent.getDamageMultiplier()
         );
         if (this.stepSoundTickCounter % 4.0F == 0.0F) {
            this.mc
               .getSoundHandler()
               .playSound(
                  new PositionedSoundRecord(
                     new ResourceLocation(block.stepSound.getStepSound()),
                     (block.stepSound.getVolume() + 1.0F) / 8.0F,
                     block.stepSound.getFrequency() * 0.5F,
                     posBlock.getX() + 0.5F,
                     posBlock.getY() + 0.5F,
                     posBlock.getZ() + 0.5F
                  )
               );
         }

         this.stepSoundTickCounter++;
         if (this.curBlockDamageMP >= 1.0F) {
            Astolfo.INSTANCE.getEventBus().post(new BlockBrokenEvent(block, directionFacing, posBlock));
            this.isHittingBlock = false;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
            this.onPlayerDestroyBlock(posBlock, directionFacing);
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.blockHitDelay = damageBlockEvent.getHitDelay();
         }

         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         return true;
      } else {
         return this.clickBlock(posBlock, directionFacing);
      }
   }

   public float getBlockReachDistance() {
      if (ModuleManager.infiniteReach.isEnabled()) {
         return InfiniteReach.range.getValue();
      } else {
         return this.currentGameType.isCreative() ? 5.0F : 4.5F;
      }
   }

   public void updateController() {
      this.syncCurrentPlayItem();
      if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
         this.netClientHandler.getNetworkManager().processReceivedPackets();
      } else {
         this.netClientHandler.getNetworkManager().checkDisconnected();
      }
   }

   private boolean isHittingPosition(BlockPos pos) {
      ItemStack itemStack = this.mc.thePlayer.getHeldItem();
      boolean bl = this.currentItemHittingBlock == null && itemStack == null;
      if (this.currentItemHittingBlock != null && itemStack != null) {
         bl = itemStack.getItem() == this.currentItemHittingBlock.getItem()
            && ItemStack.areItemStackTagsEqual(itemStack, this.currentItemHittingBlock)
            && (itemStack.isItemStackDamageable() || itemStack.getMetadata() == this.currentItemHittingBlock.getMetadata());
      }

      return pos.equals(this.currentBlock) && bl;
   }

   private void syncCurrentPlayItem() {
      int i = this.mc.thePlayer.inventory.currentItem;
      if (i != this.currentPlayerItem) {
         this.currentPlayerItem = i;
         this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
      }
   }

   public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec) {
      this.syncCurrentPlayItem();
      float f = (float)(hitVec.xCoord - hitPos.getX());
      float g = (float)(hitVec.yCoord - hitPos.getY());
      float h = (float)(hitVec.zCoord - hitPos.getZ());
      boolean bl = false;
      if (!this.mc.theWorld.getWorldBorder().contains(hitPos)) {
         return false;
      }

      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         IBlockState iBlockState = worldIn.getBlockState(hitPos);
         if ((!player.isSneaking() || player.getHeldItem() == null)
            && iBlockState.getBlock().onBlockActivated(worldIn, hitPos, iBlockState, player, side, f, g, h)) {
            bl = true;
         }

         if (!bl && heldStack != null && heldStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock)heldStack.getItem();
            if (!itemBlock.canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack)) {
               return false;
            }
         }
      }

      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(), heldStack, f, g, h));
      if (bl || this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return true;
      } else if (heldStack == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int i = heldStack.getMetadata();
         int j = heldStack.stackSize;
         boolean bl2 = heldStack.onItemUse(player, worldIn, hitPos, side, f, g, h);
         heldStack.setItemDamage(i);
         heldStack.stackSize = j;
         return bl2;
      } else {
         RightClickBlockEvent rightClickBlockEvent = new RightClickBlockEvent(EventState.PRE);
         Astolfo.INSTANCE.getEventBus().post(rightClickBlockEvent);
         return rightClickBlockEvent.isCancelled() ? false : heldStack.onItemUse(player, worldIn, hitPos, side, f, g, h);
      }
   }

   public boolean onPlayerRightClickHideItem(
      EntityPlayerSP entityPlayerSP, WorldClient worldClient, ItemStack itemStack, BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3, boolean bl
   ) {
      this.syncCurrentPlayItem();
      float f = (float)(vec3.xCoord - blockPos.getX());
      float g = (float)(vec3.yCoord - blockPos.getY());
      float h = (float)(vec3.zCoord - blockPos.getZ());
      boolean bl2 = false;
      if (!this.mc.theWorld.getWorldBorder().contains(blockPos)) {
         return false;
      }

      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         IBlockState iBlockState = worldClient.getBlockState(blockPos);
         if ((!entityPlayerSP.isSneaking() || entityPlayerSP.getHeldItem() == null)
            && iBlockState.getBlock().onBlockActivated(worldClient, blockPos, iBlockState, entityPlayerSP, enumFacing, f, g, h)) {
            bl2 = true;
         }

         if (!bl2 && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock)itemStack.getItem();
            if (!itemBlock.canPlaceBlockOnSide(worldClient, blockPos, enumFacing, entityPlayerSP, itemStack)) {
               return false;
            }
         }
      }

      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, enumFacing.getIndex(), bl ? null : itemStack, f, g, h));
      if (bl2 || this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return true;
      } else if (itemStack == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int i = itemStack.getMetadata();
         int j = itemStack.stackSize;
         boolean bl3 = itemStack.onItemUse(entityPlayerSP, worldClient, blockPos, enumFacing, f, g, h);
         itemStack.setItemDamage(i);
         itemStack.stackSize = j;
         return bl3;
      } else {
         RightClickBlockEvent rightClickBlockEvent = new RightClickBlockEvent(EventState.PRE);
         Astolfo.INSTANCE.getEventBus().post(rightClickBlockEvent);
         return rightClickBlockEvent.isCancelled() ? false : itemStack.onItemUse(entityPlayerSP, worldClient, blockPos, enumFacing, f, g, h);
      }
   }

   public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
      if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return false;
      }

      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(playerIn.inventory.getCurrentItem()));
      int i = itemStackIn.stackSize;
      ItemStack itemStack = itemStackIn.useItemRightClick(worldIn, playerIn);
      if (itemStack != itemStackIn || itemStack != null && itemStack.stackSize != i) {
         playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemStack;
         if (itemStack.stackSize == 0) {
            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public EntityPlayerSP func_178892_a(World worldIn, StatFileWriter statWriter) {
      return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, statWriter);
   }

   public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      Astolfo.INSTANCE.getEventBus().post(new AttackEvent(targetEntity));
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         playerIn.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
      return this.currentGameType != WorldSettings.GameType.SPECTATOR && playerIn.interactWith(targetEntity);
   }

   public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity entityIn, MovingObjectPosition movingObject) {
      this.syncCurrentPlayItem();
      Vec3 vec3 = new Vec3(movingObject.hitVec.xCoord - entityIn.posX, movingObject.hitVec.yCoord - entityIn.posY, movingObject.hitVec.zCoord - entityIn.posZ);
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entityIn, vec3));
      return this.currentGameType != WorldSettings.GameType.SPECTATOR && entityIn.interactAt(player, vec3);
   }

   public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
      short s = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
      ItemStack itemStack = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
      this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, itemStack, s));
      return itemStack;
   }

   public ItemStack windowClickNoEvent(int i, int j, int k, int l, EntityPlayer entityPlayer) {
      short s = entityPlayer.openContainer.getNextTransactionID(entityPlayer.inventory);
      ItemStack itemStack = entityPlayer.openContainer.slotClick(j, k, l, entityPlayer);
      this.netClientHandler.sendPacketNoEvent(new C0EPacketClickWindow(i, j, k, l, itemStack, s));
      return itemStack;
   }

   public void sendEnchantPacket(int windowID, int button) {
      this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(windowID, button));
   }

   public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
      }
   }

   public void sendPacketDropItem(ItemStack itemStackIn) {
      if (this.currentGameType.isCreative() && itemStackIn != null) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
      }
   }

   public void onStoppedUsingItem(EntityPlayer playerIn) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      playerIn.stopUsingItem();
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   public boolean isRidingHorse() {
      return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == WorldSettings.GameType.SPECTATOR;
   }

   public WorldSettings.GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }
}
