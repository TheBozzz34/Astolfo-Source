package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
   private static final long a = 65289468248851L;
   private static final long f = 6916219767007215615L;

   public ItemMonsterPlacer() {
      this.setHasSubtypes(true);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public String getItemStackDisplayName(ItemStack var1) {
      String var5 = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
      String var6 = EntityList.getStringFromID(var1.getMetadata());
      if (var6 != null) {
         var5 = var5 + " " + StatCollector.translateToLocal("entity." + var6 + ".name");
      }

      return var5;
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      EntityList.EntityEggInfo var6 = EntityList.entityEggs.get(var1.getMetadata());
      return var6 != null ? (var2 == 0 ? var6.primaryColor : var6.secondaryColor) : (int)f;
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.isRemote) {
         return true;
      }

      if (!var2.canPlayerEdit(var4.offset(var5), var5, var1)) {
         return false;
      }

      IBlockState var12 = var3.getBlockState(var4);
      if (var12.getBlock() == Blocks.mob_spawner) {
         TileEntity var13 = var3.getTileEntity(var4);
         if (var13 instanceof TileEntityMobSpawner) {
            MobSpawnerBaseLogic var14 = ((TileEntityMobSpawner)var13).getSpawnerBaseLogic();
            var14.setEntityName(EntityList.getStringFromID(var1.getMetadata()));
            var13.markDirty();
            var3.markBlockForUpdate(var4);
            if (!var2.capabilities.isCreativeMode) {
               var1.stackSize--;
            }

            return true;
         }
      }

      var4 = var4.offset(var5);
      double var17 = 0.0;
      if (var5 == EnumFacing.UP && var12 instanceof BlockFence) {
         var17 = 0.5;
      }

      Entity var15 = spawnCreature(var3, var1.getMetadata(), var4.getX() + 0.5, var4.getY() + var17, var4.getZ() + 0.5);
      if (var15 != null) {
         if (var15 instanceof EntityLivingBase && var1.hasDisplayName()) {
            var15.setCustomNameTag(var1.getDisplayName());
         }

         if (!var2.capabilities.isCreativeMode) {
            var1.stackSize--;
         }
      }

      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if (var2.isRemote) {
         return var1;
      }

      MovingObjectPosition var7 = this.getMovingObjectPositionFromPlayer(var2, var3, true);
      if (var7 == null) {
         return var1;
      }

      if (var7.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos var8 = var7.getBlockPos();
         if (!var2.isBlockModifiable(var3, var8)) {
            return var1;
         }

         if (!var3.canPlayerEdit(var8, var7.sideHit, var1)) {
            return var1;
         }

         if (var2.getBlockState(var8).getBlock() instanceof BlockLiquid) {
            Entity var9 = spawnCreature(var2, var1.getMetadata(), var8.getX() + 0.5, var8.getY() + 0.5, var8.getZ() + 0.5);
            if (var9 != null) {
               if (var9 instanceof EntityLivingBase && var1.hasDisplayName()) {
                  ((EntityLiving)var9).setCustomNameTag(var1.getDisplayName());
               }

               if (!var3.capabilities.isCreativeMode) {
                  var1.stackSize--;
               }

               var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            }
         }
      }

      return var1;
   }

   public static Entity spawnCreature(World var0, int var1, double var2, double var4, double var6) {
      if (!EntityList.entityEggs.containsKey(var1)) {
         return null;
      }

      Entity var11 = null;

      for (int var12 = 0; var12 < 1; var12++) {
         var11 = EntityList.createEntityByID(var1, var0);
         if (var11 instanceof EntityLivingBase) {
            EntityLiving var13 = (EntityLiving)var11;
            var11.setLocationAndAngles(var2, var4, var6, MathHelper.wrapAngleTo180_float(var0.rand.nextFloat() * 360.0F), 0.0F);
            var13.rotationYawHead = var13.rotationYaw;
            var13.renderYawOffset = var13.rotationYaw;
            var13.onInitialSpawn(var0.getDifficultyForLocation(new BlockPos(var13)), (IEntityLivingData)null);
            var0.spawnEntityInWorld(var11);
            var13.playLivingSound();
         }
      }

      return var11;
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (EntityList.EntityEggInfo var8 : EntityList.entityEggs.values()) {
         var3.add(new ItemStack(var1, 1, var8.spawnedID));
      }
   }
}
