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
   private static final long a = 130267066837478L;

   public PlayerControllerMP(Minecraft var1, NetHandlerPlayClient var2) {
      this.mc = var1;
      this.netClientHandler = var2;
   }

   public static void clickBlockCreative(Minecraft var0, PlayerControllerMP var1, BlockPos var2, EnumFacing var3) {
      if (!var0.theWorld.extinguishFire(var0.thePlayer, var2, var3)) {
         var1.onPlayerDestroyBlock(var2, var3);
      }
   }

   public void setPlayerCapabilities(EntityPlayer var1) {
      this.currentGameType.configurePlayerCapabilities(var1.capabilities);
   }

   public boolean W() {
      return this.currentGameType == WorldSettings.GameType.SPECTATOR;
   }

   public void setGameType(WorldSettings.GameType var1) {
      this.currentGameType = var1;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public void flipPlayer(EntityPlayer var1) {
      var1.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos var1, EnumFacing var2) {
      DestroyBlockEvent var6 = new DestroyBlockEvent();
      Astolfo.INSTANCE.getEventBus().post(var6);
      if (var6.isCancelled()) {
         return false;
      }

      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block var7 = this.mc.theWorld.getBlockState(var1).getBlock();
            ItemStack var8 = this.mc.thePlayer.getCurrentEquippedItem();
            if (var8 == null) {
               return false;
            }

            if (!var8.canDestroy(var7)) {
               return false;
            }
         }
      }

      if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      WorldClient var12 = this.mc.theWorld;
      IBlockState var13 = var12.getBlockState(var1);
      Block var9 = var13.getBlock();
      if (var9.getMaterial() == Material.air) {
         return false;
      }

      var12.playAuxSFX(2001, var1, Block.getStateId(var13));
      boolean var10 = var12.setBlockToAir(var1);
      if (var10) {
         var9.onBlockDestroyedByPlayer(var12, var1, var13);
      }

      this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
      if (!this.currentGameType.isCreative()) {
         ItemStack var11 = this.mc.thePlayer.getCurrentEquippedItem();
         if (var11 != null) {
            var11.onBlockDestroyed(var12, var9, var1, this.mc.thePlayer);
            if (var11.stackSize == 0) {
               this.mc.thePlayer.destroyCurrentEquippedItem();
            }
         }
      }

      return var10;
   }

   public boolean clickBlock(BlockPos var1, EnumFacing var2) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block var6 = this.mc.theWorld.getBlockState(var1).getBlock();
            ItemStack var7 = this.mc.thePlayer.getCurrentEquippedItem();
            if (var7 == null) {
               return false;
            }

            if (!var7.canDestroy(var6)) {
               return false;
            }
         }
      }

      if (!this.mc.theWorld.getWorldBorder().contains(var1)) {
         return false;
      }

      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
         clickBlockCreative(this.mc, this, var1, var2);
         this.blockHitDelay = 5;
      } else if (!this.isHittingBlock || !this.isHittingPosition(var1)) {
         if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, var2));
         }

         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
         Block var8 = this.mc.theWorld.getBlockState(var1).getBlock();
         boolean var9 = var8.getMaterial() != Material.air;
         if (var9 && this.curBlockDamageMP == 0.0F) {
            var8.onBlockClicked(this.mc.theWorld, var1, this.mc.thePlayer);
         }

         if (var9 && var8.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, var1) >= 1.0F) {
            this.onPlayerDestroyBlock(var1, var2);
         } else {
            this.isHittingBlock = true;
            this.currentBlock = var1;
            this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         }
      }

      return true;
   }

   public void g() {
      AbortBreakingEvent var4 = new AbortBreakingEvent(!this.isHittingBlock);
      Astolfo.INSTANCE.getEventBus().post(var4);
      if (!var4.isCancelled()) {
         this.netClientHandler
            .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
      }
   }

   public boolean onPlayerDamageBlock(BlockPos var1, EnumFacing var2) {
      DamageBlockEvent var6 = new DamageBlockEvent(this.mc.theWorld.getBlockState(var1).getBlock(), var1, 4, 1.0);
      Astolfo.INSTANCE.getEventBus().post(var6);
      if (var6.isCancelled()) {
         return false;
      }

      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         this.blockHitDelay--;
         return true;
      }

      if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(var1)) {
         this.blockHitDelay = var6.getHitDelay();
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
         clickBlockCreative(this.mc, this, var1, var2);
         return true;
      }

      if (this.isHittingPosition(var1)) {
         Block var7 = this.mc.theWorld.getBlockState(var1).getBlock();
         if (var7.getMaterial() == Material.air) {
            this.isHittingBlock = false;
            return false;
         }

         this.curBlockDamageMP = (float)(
            this.curBlockDamageMP + var7.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, var1) * var6.getDamageMultiplier()
         );
         if (this.stepSoundTickCounter % 4.0F == 0.0F) {
            this.mc
               .getSoundHandler()
               .playSound(
                  new PositionedSoundRecord(
                     new ResourceLocation(var7.stepSound.getStepSound()),
                     (var7.stepSound.getVolume() + 1.0F) / 8.0F,
                     var7.stepSound.getFrequency() * 0.5F,
                     var1.getX() + 0.5F,
                     var1.getY() + 0.5F,
                     var1.getZ() + 0.5F
                  )
               );
         }

         this.stepSoundTickCounter++;
         if (this.curBlockDamageMP >= 1.0F) {
            Astolfo.INSTANCE.getEventBus().post(new BlockBrokenEvent(var7, var2, var1));
            this.isHittingBlock = false;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, var1, var2));
            this.onPlayerDestroyBlock(var1, var2);
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.blockHitDelay = var6.getHitDelay();
         }

         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         return true;
      } else {
         return this.clickBlock(var1, var2);
      }
   }

   public float getBlockReachDistance() {
      if (ModuleManager.Z.isEnabled()) {
         return InfiniteReach.range.getValue();
      } else {
         return this.currentGameType.isCreative() ? 5.0F : 4.5F;
      }
   }

   public void u() {
      this.syncCurrentPlayItem();
      if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
         this.netClientHandler.getNetworkManager().processReceivedPackets();
      } else {
         this.netClientHandler.getNetworkManager().checkDisconnected();
      }
   }

   private boolean isHittingPosition(BlockPos var1) {
      ItemStack var5 = this.mc.thePlayer.getHeldItem();
      boolean var6 = this.currentItemHittingBlock == null && var5 == null;
      if (this.currentItemHittingBlock != null && var5 != null) {
         var6 = var5.getItem() == this.currentItemHittingBlock.getItem()
            && ItemStack.areItemStackTagsEqual(var5, this.currentItemHittingBlock)
            && (var5.isItemStackDamageable() || var5.getMetadata() == this.currentItemHittingBlock.getMetadata());
      }

      return var1.equals(this.currentBlock) && var6;
   }

   private void syncCurrentPlayItem() {
      int var4 = this.mc.thePlayer.inventory.currentItem;
      if (var4 != this.currentPlayerItem) {
         this.currentPlayerItem = var4;
         this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
      }
   }

   public boolean onPlayerRightClick(EntityPlayerSP var1, WorldClient var2, ItemStack var3, BlockPos var4, EnumFacing var5, Vec3 var6) {
      this.syncCurrentPlayItem();
      float var10 = (float)(var6.xCoord - var4.getX());
      float var11 = (float)(var6.yCoord - var4.getY());
      float var12 = (float)(var6.zCoord - var4.getZ());
      boolean var13 = false;
      if (!this.mc.theWorld.getWorldBorder().contains(var4)) {
         return false;
      }

      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         IBlockState var14 = var2.getBlockState(var4);
         if ((!var1.isSneaking() || var1.getHeldItem() == null) && var14.getBlock().onBlockActivated(var2, var4, var14, var1, var5, var10, var11, var12)) {
            var13 = true;
         }

         if (!var13 && var3 != null && var3.getItem() instanceof ItemBlock) {
            ItemBlock var15 = (ItemBlock)var3.getItem();
            if (!var15.canPlaceBlockOnSide(var2, var4, var5, var1, var3)) {
               return false;
            }
         }
      }

      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(var4, var5.getIndex(), var3, var10, var11, var12));
      if (var13 || this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int var18 = var3.getMetadata();
         int var19 = var3.stackSize;
         boolean var16 = var3.onItemUse(var1, var2, var4, var5, var10, var11, var12);
         var3.setItemDamage(var18);
         var3.stackSize = var19;
         return var16;
      } else {
         RightClickBlockEvent var17 = new RightClickBlockEvent(EventState.PRE);
         Astolfo.INSTANCE.getEventBus().post(var17);
         return var17.isCancelled() ? false : var3.onItemUse(var1, var2, var4, var5, var10, var11, var12);
      }
   }

   public boolean K(EntityPlayerSP var1, WorldClient var2, ItemStack var3, BlockPos var4, EnumFacing var5, Vec3 var6, boolean var7) {
      this.syncCurrentPlayItem();
      float var11 = (float)(var6.xCoord - var4.getX());
      float var12 = (float)(var6.yCoord - var4.getY());
      float var13 = (float)(var6.zCoord - var4.getZ());
      boolean var14 = false;
      if (!this.mc.theWorld.getWorldBorder().contains(var4)) {
         return false;
      }

      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         IBlockState var15 = var2.getBlockState(var4);
         if ((!var1.isSneaking() || var1.getHeldItem() == null) && var15.getBlock().onBlockActivated(var2, var4, var15, var1, var5, var11, var12, var13)) {
            var14 = true;
         }

         if (!var14 && var3 != null && var3.getItem() instanceof ItemBlock) {
            ItemBlock var16 = (ItemBlock)var3.getItem();
            if (!var16.canPlaceBlockOnSide(var2, var4, var5, var1, var3)) {
               return false;
            }
         }
      }

      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(var4, var5.getIndex(), var7 ? null : var3, var11, var12, var13));
      if (var14 || this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int var19 = var3.getMetadata();
         int var20 = var3.stackSize;
         boolean var17 = var3.onItemUse(var1, var2, var4, var5, var11, var12, var13);
         var3.setItemDamage(var19);
         var3.stackSize = var20;
         return var17;
      } else {
         RightClickBlockEvent var18 = new RightClickBlockEvent(EventState.PRE);
         Astolfo.INSTANCE.getEventBus().post(var18);
         return var18.isCancelled() ? false : var3.onItemUse(var1, var2, var4, var5, var11, var12, var13);
      }
   }

   public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.currentGameType == WorldSettings.GameType.SPECTATOR) {
         return false;
      }

      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(var1.inventory.getCurrentItem()));
      int var7 = var3.stackSize;
      ItemStack var8 = var3.useItemRightClick(var2, var1);
      if (var8 != var3 || var8 != null && var8.stackSize != var7) {
         var1.inventory.mainInventory[var1.inventory.currentItem] = var8;
         if (var8.stackSize == 0) {
            var1.inventory.mainInventory[var1.inventory.currentItem] = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public EntityPlayerSP func_178892_a(World var1, StatFileWriter var2) {
      return new EntityPlayerSP(this.mc, var1, this.netClientHandler, var2);
   }

   public void attackEntity(EntityPlayer var1, Entity var2) {
      this.syncCurrentPlayItem();
      Astolfo.INSTANCE.getEventBus().post(new AttackEvent(var2));
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(var2, C02PacketUseEntity.Action.ATTACK));
      if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
         var1.attackTargetEntityWithCurrentItem(var2);
      }
   }

   public boolean interactWithEntitySendPacket(EntityPlayer var1, Entity var2) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(var2, C02PacketUseEntity.Action.INTERACT));
      return this.currentGameType != WorldSettings.GameType.SPECTATOR && var1.interactWith(var2);
   }

   public boolean isPlayerRightClickingOnEntity(EntityPlayer var1, Entity var2, MovingObjectPosition var3) {
      this.syncCurrentPlayItem();
      Vec3 var7 = new Vec3(var3.hitVec.xCoord - var2.posX, var3.hitVec.yCoord - var2.posY, var3.hitVec.zCoord - var2.posZ);
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(var2, var7));
      return this.currentGameType != WorldSettings.GameType.SPECTATOR && var2.interactAt(var1, var7);
   }

   public ItemStack windowClick(int var1, int var2, int var3, int var4, EntityPlayer var5) {
      short var6 = var5.openContainer.getNextTransactionID(var5.inventory);
      ItemStack var7 = var5.openContainer.slotClick(var2, var3, var4, var5);
      this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(var1, var2, var3, var4, var7, var6));
      return var7;
   }

   public ItemStack Z(int var1, int var2, int var3, int var4, EntityPlayer var5) {
      short var6 = var5.openContainer.getNextTransactionID(var5.inventory);
      ItemStack var7 = var5.openContainer.slotClick(var2, var3, var4, var5);
      this.netClientHandler.u(new C0EPacketClickWindow(var1, var2, var3, var4, var7, var6));
      return var7;
   }

   public void sendEnchantPacket(int var1, int var2) {
      this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(var1, var2));
   }

   public void sendSlotPacket(ItemStack var1, int var2) {
      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(var2, var1));
      }
   }

   public void sendPacketDropItem(ItemStack var1) {
      if (this.currentGameType.isCreative() && var1 != null) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, var1));
      }
   }

   public void onStoppedUsingItem(EntityPlayer var1) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      var1.stopUsingItem();
   }

   public boolean D() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean y() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean L() {
      return this.currentGameType.isCreative();
   }

   public boolean d() {
      return this.mc.thePlayer.E() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public boolean C() {
      return this.currentGameType == WorldSettings.GameType.SPECTATOR;
   }

   public WorldSettings.GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }
}
