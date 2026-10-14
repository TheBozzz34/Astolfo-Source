package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   @Override
   public final ItemStack dispense(IBlockSource source, ItemStack stack) {
      ItemStack itemStack = this.dispenseStack(source, stack);
      this.playDispenseSound(source);
      this.spawnDispenseParticles(source, BlockDispenser.getFacing(source.getBlockMetadata()));
      return itemStack;
   }

   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      EnumFacing enumFacing = BlockDispenser.getFacing(source.getBlockMetadata());
      IPosition iPosition = BlockDispenser.getDispensePosition(source);
      ItemStack itemStack = stack.splitStack(1);
      doDispense(source.getWorld(), itemStack, 6, enumFacing, iPosition);
      return stack;
   }

   public static void doDispense(World worldIn, ItemStack stack, int speed, EnumFacing facing, IPosition position) {
      double d = position.getX();
      double e = position.getY();
      double d2 = position.getZ();
      if (facing.getAxis() == EnumFacing.Axis.Y) {
         e -= 0.125;
      } else {
         e -= 0.15625;
      }

      EntityItem entityItem = new EntityItem(worldIn, d, e, d2, stack);
      double d3 = worldIn.rand.nextDouble() * 0.1 + 0.2;
      entityItem.motionX = facing.getFrontOffsetX() * d3;
      entityItem.motionY = 0.2F;
      entityItem.motionZ = facing.getFrontOffsetZ() * d3;
      entityItem.motionX = entityItem.motionX + worldIn.rand.nextGaussian() * 0.0075F * speed;
      entityItem.motionY = entityItem.motionY + worldIn.rand.nextGaussian() * 0.0075F * speed;
      entityItem.motionZ = entityItem.motionZ + worldIn.rand.nextGaussian() * 0.0075F * speed;
      worldIn.spawnEntityInWorld(entityItem);
   }

   public void playDispenseSound(IBlockSource source) {
      source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
   }

   public void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn) {
      source.getWorld().playAuxSFX(2000, source.getBlockPos(), this.func_82488_a(facingIn));
   }

   private int func_82488_a(EnumFacing facingIn) {
      return facingIn.getFrontOffsetX() + 1 + (facingIn.getFrontOffsetZ() + 1) * 3;
   }
}
