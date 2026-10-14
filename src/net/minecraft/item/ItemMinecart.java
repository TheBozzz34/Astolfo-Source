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
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing enumFacing = BlockDispenser.getFacing(source.getBlockMetadata());
         World world = source.getWorld();
         double d = source.getX() + enumFacing.getFrontOffsetX() * 1.125;
         double e = Math.floor(source.getY()) + enumFacing.getFrontOffsetY();
         double d2 = source.getZ() + enumFacing.getFrontOffsetZ() * 1.125;
         BlockPos blockPos = source.getBlockPos().offset(enumFacing);
         IBlockState iBlockState = world.getBlockState(blockPos);
         BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getBlock() instanceof BlockRailBase
            ? iBlockState.getValue(((BlockRailBase)iBlockState.getBlock()).getShapeProperty())
            : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         double d3;
         if (BlockRailBase.isRailBlock(iBlockState)) {
            if (enumRailDirection.isAscending()) {
               d3 = 0.6;
            } else {
               d3 = 0.1;
            }
         } else {
            if (iBlockState.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(world.getBlockState(blockPos.down()))) {
               return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            IBlockState iBlockState2 = world.getBlockState(blockPos.down());
            BlockRailBase.EnumRailDirection enumRailDirection2 = iBlockState2.getBlock() instanceof BlockRailBase
               ? iBlockState2.getValue(((BlockRailBase)iBlockState2.getBlock()).getShapeProperty())
               : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (enumFacing != EnumFacing.DOWN && enumRailDirection2.isAscending()) {
               d3 = -0.4;
            } else {
               d3 = -0.9;
            }
         }

         EntityMinecart entityMinecart = EntityMinecart.getMinecart(world, d, e + d3, d2, ((ItemMinecart)stack.getItem()).minecartType);
         if (stack.hasDisplayName()) {
            entityMinecart.setCustomNameTag(stack.getDisplayName());
         }

         world.spawnEntityInWorld(entityMinecart);
         stack.splitStack(1);
         return stack;
      }

      @Override
      public void playDispenseSound(IBlockSource source) {
         source.getWorld().playAuxSFX((int)c, source.getBlockPos(), 0);
      }
   };
   private final EntityMinecart.EnumMinecartType minecartType;
   private static final long a = 90419106221286L;

   public ItemMinecart(EntityMinecart.EnumMinecartType typeIn) {
      this.maxStackSize = 1;
      this.minecartType = typeIn;
      this.setCreativeTab(CreativeTabs.tabTransport);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (BlockRailBase.isRailBlock(iBlockState)) {
         if (!worldIn.isRemote) {
            BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getBlock() instanceof BlockRailBase
               ? iBlockState.getValue(((BlockRailBase)iBlockState.getBlock()).getShapeProperty())
               : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double d = 0.0;
            if (enumRailDirection.isAscending()) {
               d = 0.5;
            }

            EntityMinecart entityMinecart = EntityMinecart.getMinecart(worldIn, pos.getX() + 0.5, pos.getY() + 0.0625 + d, pos.getZ() + 0.5, this.minecartType);
            if (stack.hasDisplayName()) {
               entityMinecart.setCustomNameTag(stack.getDisplayName());
            }

            worldIn.spawnEntityInWorld(entityMinecart);
         }

         stack.stackSize--;
         return true;
      } else {
         return false;
      }
   }
}
