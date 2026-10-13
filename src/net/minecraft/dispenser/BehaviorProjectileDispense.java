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
   public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
      World var6 = var1.getWorld();
      IPosition var7 = BlockDispenser.getDispensePosition(var1);
      EnumFacing var8 = BlockDispenser.getFacing(var1.getBlockMetadata());
      IProjectile var9 = this.getProjectileEntity(var6, var7);
      var9.setThrowableHeading(var8.getFrontOffsetX(), var8.getFrontOffsetY() + 0.1F, var8.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
      var6.spawnEntityInWorld((Entity)var9);
      var2.splitStack(1);
      return var2;
   }

   @Override
   public void playDispenseSound(IBlockSource var1) {
      var1.getWorld().playAuxSFX((int)c, var1.getBlockPos(), 0);
   }

   public abstract IProjectile getProjectileEntity(World var1, IPosition var2);

   public float func_82498_a() {
      return 6.0F;
   }

   public float func_82500_b() {
      return 1.1F;
   }
}
