package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   private static final long b = 27699350061526L;
   private static final long c = 5912687945176843242L;

   @Override
   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      World world = source.getWorld();
      IPosition iPosition = BlockDispenser.getDispensePosition(source);
      EnumFacing enumFacing = BlockDispenser.getFacing(source.getBlockMetadata());
      IProjectile iProjectile = this.getProjectileEntity(world, iPosition);
      iProjectile.setThrowableHeading(enumFacing.getFrontOffsetX(), enumFacing.getFrontOffsetY() + 0.1F, enumFacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
      world.spawnEntityInWorld((Entity)iProjectile);
      stack.splitStack(1);
      return stack;
   }

   @Override
   public void playDispenseSound(IBlockSource source) {
      source.getWorld().playAuxSFX((int)c, source.getBlockPos(), 0);
   }

   public abstract IProjectile getProjectileEntity(World worldIn, IPosition position);

   public float func_82498_a() {
      return 6.0F;
   }

   public float func_82500_b() {
      return 1.1F;
   }
}
