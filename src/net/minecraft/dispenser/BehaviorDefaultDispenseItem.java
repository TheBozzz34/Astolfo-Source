package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   private static final long a = 138942525702979L;

   @Override
   public final ItemStack dispense(IBlockSource var1, ItemStack var2) {
      ItemStack var3 = this.dispenseStack(var1, var2);
      this.playDispenseSound(var1);
      this.spawnDispenseParticles(var1, BlockDispenser.getFacing(var1.getBlockMetadata()));
      return var3;
   }

   public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
      EnumFacing var5 = BlockDispenser.getFacing(var1.getBlockMetadata());
      IPosition var6 = BlockDispenser.getDispensePosition(var1);
      ItemStack var7 = var2.splitStack(1);
      doDispense(var1.getWorld(), var7, 6, var5, var6);
      return var2;
   }

   public static void doDispense(World var0, ItemStack var1, int var2, EnumFacing var3, IPosition var4) {
      double var9 = var4.getX();
      double var11 = var4.getY();
      double var13 = var4.getZ();
      if (var3.getAxis() == EnumFacing.Axis.Y) {
         var11 -= 0.125;
      } else {
         var11 -= 0.15625;
      }

      EntityItem var15 = new EntityItem(var0, var9, var11, var13, var1);
      double var16 = var0.rand.nextDouble() * 0.1 + 0.2;
      var15.motionX = var3.getFrontOffsetX() * var16;
      var15.motionY = 0.2F;
      var15.motionZ = var3.getFrontOffsetZ() * var16;
      var15.motionX = var15.motionX + var0.rand.nextGaussian() * 0.0075F * var2;
      var15.motionY = var15.motionY + var0.rand.nextGaussian() * 0.0075F * var2;
      var15.motionZ = var15.motionZ + var0.rand.nextGaussian() * 0.0075F * var2;
      var0.spawnEntityInWorld(var15);
   }

   public void playDispenseSound(IBlockSource var1) {
      var1.getWorld().playAuxSFX(1000, var1.getBlockPos(), 0);
   }

   public void spawnDispenseParticles(IBlockSource var1, EnumFacing var2) {
      var1.getWorld().playAuxSFX(2000, var1.getBlockPos(), this.func_82488_a(var2));
   }

   private int func_82488_a(EnumFacing var1) {
      return var1.getFrontOffsetX() + 1 + (var1.getFrontOffsetZ() + 1) * 3;
   }
}
