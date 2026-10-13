package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class ItemInWorldManager {
   public World theWorld;
   public EntityPlayerMP thisPlayerMP;
   private WorldSettings.GameType gameType = WorldSettings.GameType.NOT_SET;
   private boolean isDestroyingBlock;
   private int initialDamage;
   private BlockPos field_180240_f = BlockPos.ORIGIN;
   private int curblockDamage;
   private boolean receivedFinishDiggingPacket;
   private BlockPos field_180241_i = BlockPos.ORIGIN;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;
   private static final long a = 109636540683536L;
   private static final long j = -6710022656306968623L;

   public ItemInWorldManager(World var1) {
      this.theWorld = var1;
   }

   public void setGameType(WorldSettings.GameType var1) {
      this.gameType = var1;
      var1.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
      this.thisPlayerMP.sendPlayerAbilities();
      this.thisPlayerMP
         .mcServer
         .getConfigurationManager()
         .sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, this.thisPlayerMP));
   }

   public WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   public void initializeGameType(WorldSettings.GameType var1) {
      if (this.gameType == WorldSettings.GameType.NOT_SET) {
         this.gameType = var1;
      }

      this.setGameType(this.gameType);
   }

   public void updateBlockRemoving() {
      this.curblockDamage++;
      if (this.receivedFinishDiggingPacket) {
         int var4 = this.curblockDamage - this.initialBlockDamage;
         Block var5 = this.theWorld.getBlockState(this.field_180241_i).getBlock();
         if (var5.getMaterial() == Material.air) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float var6 = var5.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (var4 + 1);
            int var7 = (int)(var6 * 10.0F);
            if (var7 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180241_i, var7);
               this.durabilityRemainingOnBlock = var7;
            }

            if (var6 >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.field_180241_i);
            }
         }
      } else if (this.isDestroyingBlock) {
         Block var8 = this.theWorld.getBlockState(this.field_180240_f).getBlock();
         if (var8.getMaterial() == Material.air) {
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int var9 = this.curblockDamage - this.initialDamage;
            float var10 = var8.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (var9 + 1);
            int var11 = (int)(var10 * 10.0F);
            if (var11 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, var11);
               this.durabilityRemainingOnBlock = var11;
            }
         }
      }
   }

   public void onBlockClicked(BlockPos var1, EnumFacing var2) {
      if (this.isCreative()) {
         if (!this.theWorld.extinguishFire((EntityPlayer)null, var1, var2)) {
            this.tryHarvestBlock(var1);
         }
      } else {
         Block var6 = this.theWorld.getBlockState(var1).getBlock();
         if (this.gameType.isAdventure()) {
            if (this.gameType == WorldSettings.GameType.SPECTATOR) {
               return;
            }

            if (!this.thisPlayerMP.isAllowEdit()) {
               ItemStack var7 = this.thisPlayerMP.getCurrentEquippedItem();
               if (var7 == null) {
                  return;
               }

               if (!var7.canDestroy(var6)) {
                  return;
               }
            }
         }

         this.theWorld.extinguishFire((EntityPlayer)null, var1, var2);
         this.initialDamage = this.curblockDamage;
         float var9 = 1.0F;
         if (var6.getMaterial() != Material.air) {
            var6.onBlockClicked(this.theWorld, var1, this.thisPlayerMP);
            var9 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, var1);
         }

         if (var6.getMaterial() != Material.air && var9 >= 1.0F) {
            this.tryHarvestBlock(var1);
         } else {
            this.isDestroyingBlock = true;
            this.field_180240_f = var1;
            int var8 = (int)(var9 * 10.0F);
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), var1, var8);
            this.durabilityRemainingOnBlock = var8;
         }
      }
   }

   public void blockRemoving(BlockPos var1) {
      if (var1.equals(this.field_180240_f)) {
         int var5 = this.curblockDamage - this.initialDamage;
         Block var6 = this.theWorld.getBlockState(var1).getBlock();
         if (var6.getMaterial() != Material.air) {
            float var7 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, var1) * (var5 + 1);
            if (var7 >= 0.7F) {
               this.isDestroyingBlock = false;
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), var1, -1);
               this.tryHarvestBlock(var1);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.field_180241_i = var1;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }
   }

   public void cancelDestroyingBlock() {
      this.isDestroyingBlock = false;
      this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
   }

   private boolean removeBlock(BlockPos var1) {
      IBlockState var5 = this.theWorld.getBlockState(var1);
      var5.getBlock().onBlockHarvested(this.theWorld, var1, var5, this.thisPlayerMP);
      boolean var6 = this.theWorld.setBlockToAir(var1);
      if (var6) {
         var5.getBlock().onBlockDestroyedByPlayer(this.theWorld, var1, var5);
      }

      return var6;
   }

   public boolean tryHarvestBlock(BlockPos var1) {
      if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      IBlockState var5 = this.theWorld.getBlockState(var1);
      TileEntity var6 = this.theWorld.getTileEntity(var1);
      if (this.gameType.isAdventure()) {
         if (this.gameType == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.thisPlayerMP.isAllowEdit()) {
            ItemStack var7 = this.thisPlayerMP.getCurrentEquippedItem();
            if (var7 == null) {
               return false;
            }

            if (!var7.canDestroy(var5.getBlock())) {
               return false;
            }
         }
      }

      this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, (int)j, var1, Block.getStateId(var5));
      boolean var10 = this.removeBlock(var1);
      if (this.isCreative()) {
         this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, var1));
      } else {
         ItemStack var8 = this.thisPlayerMP.getCurrentEquippedItem();
         boolean var9 = this.thisPlayerMP.canHarvestBlock(var5.getBlock());
         if (var8 != null) {
            var8.onBlockDestroyed(this.theWorld, var5.getBlock(), var1, this.thisPlayerMP);
            if (var8.stackSize == 0) {
               this.thisPlayerMP.destroyCurrentEquippedItem();
            }
         }

         if (var10 && var9) {
            var5.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, var1, var5, var6);
         }
      }

      return var10;
   }

   public boolean tryUseItem(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.gameType == WorldSettings.GameType.SPECTATOR) {
         return false;
      }

      int var7 = var3.stackSize;
      int var8 = var3.getMetadata();
      ItemStack var9 = var3.useItemRightClick(var2, var1);
      if (var9 != var3 || var9 != null && (var9.stackSize != var7 || var9.getMaxItemUseDuration() > 0 || var9.getMetadata() != var8)) {
         var1.inventory.mainInventory[var1.inventory.currentItem] = var9;
         if (this.isCreative()) {
            var9.stackSize = var7;
            if (var9.isItemStackDamageable()) {
               var9.setItemDamage(var8);
            }
         }

         if (var9.stackSize == 0) {
            var1.inventory.mainInventory[var1.inventory.currentItem] = null;
         }

         if (!var1.isUsingItem()) {
            ((EntityPlayerMP)var1).sendContainerToPlayer(var1.inventoryContainer);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean activateBlockOrUseItem(EntityPlayer var1, World var2, ItemStack var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.gameType == WorldSettings.GameType.SPECTATOR) {
         TileEntity var16 = var2.getTileEntity(var4);
         if (var16 instanceof ILockableContainer) {
            Block var17 = var2.getBlockState(var4).getBlock();
            ILockableContainer var18 = (ILockableContainer)var16;
            if (var18 instanceof TileEntityChest && var17 instanceof BlockChest) {
               var18 = ((BlockChest)var17).getLockableContainer(var2, var4);
            }

            if (var18 != null) {
               var1.displayGUIChest(var18);
               return true;
            }
         } else if (var16 instanceof IInventory) {
            var1.displayGUIChest((IInventory)var16);
            return true;
         }

         return false;
      } else {
         if (!var1.isSneaking() || var1.getHeldItem() == null) {
            IBlockState var12 = var2.getBlockState(var4);
            if (var12.getBlock().onBlockActivated(var2, var4, var12, var1, var5, var6, var7, var8)) {
               return true;
            }
         }

         if (var3 == null) {
            return false;
         } else if (this.isCreative()) {
            int var15 = var3.getMetadata();
            int var13 = var3.stackSize;
            boolean var14 = var3.onItemUse(var1, var2, var4, var5, var6, var7, var8);
            var3.setItemDamage(var15);
            var3.stackSize = var13;
            return var14;
         } else {
            return var3.onItemUse(var1, var2, var4, var5, var6, var7, var8);
         }
      }
   }

   public void setWorld(WorldServer var1) {
      this.theWorld = var1;
   }
}
