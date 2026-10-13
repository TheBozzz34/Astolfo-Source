package net.minecraft.block;

import bypass.Astolfo;
import bypass.module.movement.NoSlow;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSoulSand extends Block {
   private static final long e = 31456647010979L;

   public BlockSoulSand() {
      super(Material.sand, MapColor.brownColor);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      float var4 = 0.125F;
      return new AxisAlignedBB(var2.getX(), var2.getY(), var2.getZ(), var2.getX() + 1, var2.getY() + 1 - var4, var2.getZ() + 1);
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      long var5 = e ^ 95524182448853L;
      long var7 = var5 ^ 137486825848261L;
      if (!Astolfo.INSTANCE.getModuleManager().get(NoSlow.class).isEnabled() || !NoSlow.soulSand.getValue()) {
         var4.motionX *= 0.4;
         var4.motionZ *= 0.4;
      }
   }
}
