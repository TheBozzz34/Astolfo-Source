package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
   private static final long e = 97407124030115L;
   private static final long i = -4084739573257601004L;

   public BlockCauldron() {
      super(Material.iron, MapColor.stoneColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      float var7 = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBoundsForItemRender();
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      int var8 = var3.getValue(LEVEL);
      float var9 = var2.getY() + (6.0F + 3 * var8) / 16.0F;
      if (!var1.isRemote && var4.isBurning() && var8 > 0 && var4.getEntityBoundingBox().minY <= var9) {
         var4.extinguish();
         this.setWaterLevel(var1, var2, var3, var8 - 1);
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      ItemStack var12 = var4.inventory.getCurrentItem();
      if (var12 == null) {
         return true;
      }

      int var13 = var3.getValue(LEVEL);
      Item var14 = var12.getItem();
      if (var14 == Items.water_bucket) {
         if (var13 < 3) {
            if (!var4.capabilities.isCreativeMode) {
               var4.inventory.setInventorySlotContents(var4.inventory.currentItem, new ItemStack(Items.bucket));
            }

            var4.triggerAchievement(StatList.field_181725_I);
            this.setWaterLevel(var1, var2, var3, 3);
         }

         return true;
      } else if (var14 == Items.glass_bottle) {
         if (var13 > 0) {
            if (!var4.capabilities.isCreativeMode) {
               ItemStack var17 = new ItemStack(Items.potionitem, 1, 0);
               if (!var4.inventory.addItemStackToInventory(var17)) {
                  var1.spawnEntityInWorld(new EntityItem(var1, var2.getX() + 0.5, var2.getY() + 1.5, var2.getZ() + 0.5, var17));
               } else if (var4 instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)var4).sendContainerToPlayer(var4.inventoryContainer);
               }

               var4.triggerAchievement(StatList.field_181726_J);
               var12.stackSize--;
               if (var12.stackSize <= 0) {
                  var4.inventory.setInventorySlotContents(var4.inventory.currentItem, (ItemStack)null);
               }
            }

            this.setWaterLevel(var1, var2, var3, var13 - 1);
         }

         return true;
      } else {
         if (var13 > 0 && var14 instanceof ItemArmor) {
            ItemArmor var15 = (ItemArmor)var14;
            if (var15.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && var15.hasColor(var12)) {
               var15.removeColor(var12);
               this.setWaterLevel(var1, var2, var3, var13 - 1);
               var4.triggerAchievement(StatList.field_181727_K);
               return true;
            }
         }

         if (var13 > 0 && var14 instanceof ItemBanner && TileEntityBanner.getPatterns(var12) > 0) {
            ItemStack var16 = var12.copy();
            var16.stackSize = 1;
            TileEntityBanner.removeBannerData(var16);
            if (var12.stackSize <= 1 && !var4.capabilities.isCreativeMode) {
               var4.inventory.setInventorySlotContents(var4.inventory.currentItem, var16);
            } else {
               if (!var4.inventory.addItemStackToInventory(var16)) {
                  var1.spawnEntityInWorld(new EntityItem(var1, var2.getX() + 0.5, var2.getY() + 1.5, var2.getZ() + 0.5, var16));
               } else if (var4 instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)var4).sendContainerToPlayer(var4.inventoryContainer);
               }

               var4.triggerAchievement(StatList.field_181728_L);
               if (!var4.capabilities.isCreativeMode) {
                  var12.stackSize--;
               }
            }

            if (!var4.capabilities.isCreativeMode) {
               this.setWaterLevel(var1, var2, var3, var13 - 1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void setWaterLevel(World var1, BlockPos var2, IBlockState var3, int var4) {
      var1.setBlockState(var2, var3.withProperty(LEVEL, MathHelper.clamp_int(var4, 0, 3)), 2);
      var1.updateComparatorOutputLevel(var2, this);
   }

   @Override
   public void fillWithRain(World var1, BlockPos var2) {
      if (var1.rand.nextInt((int)i) == 1) {
         IBlockState var6 = var1.getBlockState(var2);
         if (var6.getValue(LEVEL) < 3) {
            var1.setBlockState(var2, var6.cycleProperty(LEVEL), 2);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.cauldron;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.cauldron;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getValue(LEVEL);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(LEVEL, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(LEVEL);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, LEVEL);
   }
}
