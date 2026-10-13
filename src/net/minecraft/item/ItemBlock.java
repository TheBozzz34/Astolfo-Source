package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlock extends Item {
   public final Block block;
   private static final long b = 116273878565036L;
   private static final long r = -8540948488534360054L;

   public ItemBlock(Block var1) {
      this.block = var1;
   }

   public ItemBlock setUnlocalizedName(String var1) {
      super.setUnlocalizedName(var1);
      return this;
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var12 = var3.getBlockState(var4);
      Block var13 = var12.getBlock();
      if (!var13.isReplaceable(var3, var4)) {
         var4 = var4.offset(var5);
      }

      if (var1.stackSize == 0) {
         return false;
      }

      if (!var2.canPlayerEdit(var4, var5, var1)) {
         return false;
      }

      if (var3.canBlockBePlaced(this.block, var4, false, var5, (Entity)null, var1)) {
         int var14 = this.getMetadata(var1.getMetadata());
         IBlockState var15 = this.block.onBlockPlaced(var3, var4, var5, var6, var7, var8, var14, var2);
         if (var3.setBlockState(var4, var15, 3)) {
            var15 = var3.getBlockState(var4);
            if (var15.getBlock() == this.block) {
               setTileEntityNBT(var3, var2, var4, var1);
               this.block.onBlockPlacedBy(var3, var4, var15, var2, var1);
            }

            var3.playSoundEffect(
               var4.getX() + 0.5F,
               var4.getY() + 0.5F,
               var4.getZ() + 0.5F,
               this.block.stepSound.getPlaceSound(),
               (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
               this.block.stepSound.getFrequency() * 0.8F
            );
            var1.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean setTileEntityNBT(World var0, EntityPlayer var1, BlockPos var2, ItemStack var3) {
      MinecraftServer var7 = MinecraftServer.getServer();
      if (var7 == null) {
         return false;
      }

      if (var3.hasTagCompound() && var3.getTagCompound().hasKey("BlockEntityTag", (int)r)) {
         TileEntity var8 = var0.getTileEntity(var2);
         if (var8 != null) {
            if (!var0.isRemote && var8.func_183000_F() && !var7.getConfigurationManager().canSendCommands(var1.getGameProfile())) {
               return false;
            }

            NBTTagCompound var9 = new NBTTagCompound();
            NBTTagCompound var10 = (NBTTagCompound)var9.copy();
            var8.writeToNBT(var9);
            NBTTagCompound var11 = (NBTTagCompound)var3.getTagCompound().getTag("BlockEntityTag");
            var9.merge(var11);
            var9.setInteger("x", var2.getX());
            var9.setInteger("y", var2.getY());
            var9.setInteger("z", var2.getZ());
            if (!var9.equals(var10)) {
               var8.readFromNBT(var9);
               var8.markDirty();
               return true;
            }
         }
      }

      return false;
   }

   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3, EntityPlayer var4, ItemStack var5) {
      Block var9 = var1.getBlockState(var2).getBlock();
      if (var9 == Blocks.snow_layer) {
         var3 = EnumFacing.UP;
      } else if (!var9.isReplaceable(var1, var2)) {
         var2 = var2.offset(var3);
      }

      return var1.canBlockBePlaced(this.block, var2, false, var3, (Entity)null, var5);
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      return this.block.getUnlocalizedName();
   }

   @Override
   public String getUnlocalizedName() {
      return this.block.getUnlocalizedName();
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return this.block.getCreativeTabToDisplayOn();
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      this.block.getSubBlocks(var1, var2, var3);
   }

   public Block getBlock() {
      return this.block;
   }
}
