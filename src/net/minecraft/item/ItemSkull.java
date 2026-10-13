package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {
   private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
   private static final long a = 109277541148903L;

   public ItemSkull() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      }

      IBlockState var12 = var3.getBlockState(var4);
      Block var13 = var12.getBlock();
      boolean var14 = var13.isReplaceable(var3, var4);
      if (!var14) {
         if (!var3.getBlockState(var4).getBlock().getMaterial().isSolid()) {
            return false;
         }

         var4 = var4.offset(var5);
      }

      if (!var2.canPlayerEdit(var4, var5, var1)) {
         return false;
      }

      if (!Blocks.skull.canPlaceBlockAt(var3, var4)) {
         return false;
      }

      if (!var3.isRemote) {
         var3.setBlockState(var4, Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, var5), 3);
         int var15 = 0;
         if (var5 == EnumFacing.UP) {
            var15 = MathHelper.floor_double(var2.rotationYaw * 16.0F / 360.0F + 0.5) & 15;
         }

         TileEntity var16 = var3.getTileEntity(var4);
         if (var16 instanceof TileEntitySkull) {
            TileEntitySkull var17 = (TileEntitySkull)var16;
            if (var1.getMetadata() == 3) {
               GameProfile var18 = null;
               if (var1.hasTagCompound()) {
                  NBTTagCompound var19 = var1.getTagCompound();
                  if (var19.hasKey("SkullOwner", 10)) {
                     var18 = NBTUtil.readGameProfileFromNBT(var19.getCompoundTag("SkullOwner"));
                  } else if (var19.hasKey("SkullOwner", 8) && var19.getString("SkullOwner").length() > 0) {
                     var18 = new GameProfile((UUID)null, var19.getString("SkullOwner"));
                  }
               }

               var17.setPlayerProfile(var18);
            } else {
               var17.setType(var1.getMetadata());
            }

            var17.setSkullRotation(var15);
            Blocks.skull.checkWitherSpawn(var3, var4, var17);
         }

         var1.stackSize--;
      }

      return true;
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (int var7 = 0; var7 < skullTypes.length; var7++) {
         var3.add(new ItemStack(var1, 1, var7));
      }
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      int var5 = var1.getMetadata();
      if (var5 < 0 || var5 >= skullTypes.length) {
         var5 = 0;
      }

      return super.getUnlocalizedName() + "." + skullTypes[var5];
   }

   @Override
   public String getItemStackDisplayName(ItemStack var1) {
      if (var1.getMetadata() == 3 && var1.hasTagCompound()) {
         if (var1.getTagCompound().hasKey("SkullOwner", 8)) {
            return StatCollector.translateToLocalFormatted("item.skull.player.name", var1.getTagCompound().getString("SkullOwner"));
         }

         if (var1.getTagCompound().hasKey("SkullOwner", 10)) {
            NBTTagCompound var5 = var1.getTagCompound().getCompoundTag("SkullOwner");
            if (var5.hasKey("Name", 8)) {
               return StatCollector.translateToLocalFormatted("item.skull.player.name", var5.getString("Name"));
            }
         }
      }

      return super.getItemStackDisplayName(var1);
   }

   @Override
   public boolean updateItemStackNBT(NBTTagCompound var1) {
      super.updateItemStackNBT(var1);
      if (var1.hasKey("SkullOwner", 8) && var1.getString("SkullOwner").length() > 0) {
         GameProfile var5 = new GameProfile((UUID)null, var1.getString("SkullOwner"));
         var5 = TileEntitySkull.updateGameprofile(var5);
         var1.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), var5));
         return true;
      } else {
         return false;
      }
   }
}
