package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
      private static final long b = 85268479376584L;
      private static final long c = -1707401812585217048L;

      @Override
      public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
         EnumFacing var6 = BlockDispenser.getFacing(var1.getBlockMetadata());
         World var7 = var1.getWorld();
         double var8 = var1.getX() + var6.getFrontOffsetX() * 1.125;
         double var10 = Math.floor(var1.getY()) + var6.getFrontOffsetY();
         double var12 = var1.getZ() + var6.getFrontOffsetZ() * 1.125;
         BlockPos var14 = var1.getBlockPos().offset(var6);
         IBlockState var15 = var7.getBlockState(var14);
         BlockRailBase.EnumRailDirection var16 = var15.getBlock() instanceof BlockRailBase
            ? var15.getValue(((BlockRailBase)var15.getBlock()).getShapeProperty())
            : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         double var17;
         if (BlockRailBase.isRailBlock(var15)) {
            if (var16.isAscending()) {
               var17 = 0.6;
            } else {
               var17 = 0.1;
            }
         } else {
            if (var15.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(var7.getBlockState(var14.down()))) {
               return this.behaviourDefaultDispenseItem.dispense(var1, var2);
            }

            IBlockState var19 = var7.getBlockState(var14.down());
            BlockRailBase.EnumRailDirection var20 = var19.getBlock() instanceof BlockRailBase
               ? var19.getValue(((BlockRailBase)var19.getBlock()).getShapeProperty())
               : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (var6 != EnumFacing.DOWN && var20.isAscending()) {
               var17 = -0.4;
            } else {
               var17 = -0.9;
            }
         }

         EntityMinecart var21 = EntityMinecart.getMinecart(var7, var8, var10 + var17, var12, ((ItemMinecart)var2.getItem()).minecartType);
         if (var2.hasDisplayName()) {
            var21.setCustomNameTag(var2.getDisplayName());
         }

         var7.spawnEntityInWorld(var21);
         var2.splitStack(1);
         return var2;
      }

      @Override
      public void playDispenseSound(IBlockSource var1) {
         var1.getWorld().playAuxSFX((int)c, var1.getBlockPos(), 0);
      }
   };
   private final EntityMinecart.EnumMinecartType minecartType;
   private static final long a = 90419106221286L;

   public ItemMinecart(EntityMinecart.EnumMinecartType var1) {
      this.maxStackSize = 1;
      this.minecartType = var1;
      this.setCreativeTab(CreativeTabs.tabTransport);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var12 = var3.getBlockState(var4);
      if (BlockRailBase.isRailBlock(var12)) {
         if (!var3.isRemote) {
            BlockRailBase.EnumRailDirection var13 = var12.getBlock() instanceof BlockRailBase
               ? var12.getValue(((BlockRailBase)var12.getBlock()).getShapeProperty())
               : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double var14 = 0.0;
            if (var13.isAscending()) {
               var14 = 0.5;
            }

            EntityMinecart var16 = EntityMinecart.getMinecart(var3, var4.getX() + 0.5, var4.getY() + 0.0625 + var14, var4.getZ() + 0.5, this.minecartType);
            if (var1.hasDisplayName()) {
               var16.setCustomNameTag(var1.getDisplayName());
            }

            var3.spawnEntityInWorld(var16);
         }

         var1.stackSize--;
         return true;
      } else {
         return false;
      }
   }
}
