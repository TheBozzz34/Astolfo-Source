package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock {
   private static final long a = 57123452889711L;

   public ItemBanner() {
      super(Blocks.standing_banner);
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      }

      if (!var3.getBlockState(var4).getBlock().getMaterial().isSolid()) {
         return false;
      }

      var4 = var4.offset(var5);
      if (!var2.canPlayerEdit(var4, var5, var1)) {
         return false;
      }

      if (!Blocks.standing_banner.canPlaceBlockAt(var3, var4)) {
         return false;
      }

      if (var3.isRemote) {
         return true;
      }

      if (var5 == EnumFacing.UP) {
         int var12 = MathHelper.floor_double((var2.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
         var3.setBlockState(var4, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, var12), 3);
      } else {
         var3.setBlockState(var4, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, var5), 3);
      }

      var1.stackSize--;
      TileEntity var14 = var3.getTileEntity(var4);
      if (var14 instanceof TileEntityBanner) {
         ((TileEntityBanner)var14).setItemValues(var1);
      }

      return true;
   }

   @Override
   public String getItemStackDisplayName(ItemStack var1) {
      String var4 = "item.banner.";
      EnumDyeColor var5 = this.getBaseColor(var1);
      var4 = var4 + var5.getUnlocalizedName() + ".name";
      return StatCollector.translateToLocal(var4);
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      NBTTagCompound var8 = var1.getSubCompound("BlockEntityTag", false);
      if (var8 != null && var8.hasKey("Patterns")) {
         NBTTagList var9 = var8.getTagList("Patterns", 10);

         for (int var10 = 0; var10 < var9.tagCount() && var10 < 6; var10++) {
            NBTTagCompound var11 = var9.getCompoundTagAt(var10);
            EnumDyeColor var12 = EnumDyeColor.byDyeDamage(var11.getInteger("Color"));
            TileEntityBanner.EnumBannerPattern var13 = TileEntityBanner.EnumBannerPattern.getPatternByID(var11.getString("Pattern"));
            if (var13 != null) {
               var3.add(StatCollector.translateToLocal("item.banner." + var13.getPatternName() + "." + var12.getUnlocalizedName()));
            }
         }
      }
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      if (var2 == 0) {
         return 16777215;
      }

      EnumDyeColor var6 = this.getBaseColor(var1);
      return var6.getMapColor().colorValue;
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (EnumDyeColor var10 : EnumDyeColor.values()) {
         NBTTagCompound var11 = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(var11, var10.getDyeDamage(), (NBTTagList)null);
         NBTTagCompound var12 = new NBTTagCompound();
         var12.setTag("BlockEntityTag", var11);
         ItemStack var13 = new ItemStack(var1, 1, var10.getDyeDamage());
         var13.setTagCompound(var12);
         var3.add(var13);
      }
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return CreativeTabs.tabDecorations;
   }

   private EnumDyeColor getBaseColor(ItemStack var1) {
      NBTTagCompound var5 = var1.getSubCompound("BlockEntityTag", false);
      EnumDyeColor var6 = null;
      if (var5 != null && var5.hasKey("Base")) {
         var6 = EnumDyeColor.byDyeDamage(var5.getInteger("Base"));
      } else {
         var6 = EnumDyeColor.byDyeDamage(var1.getMetadata());
      }

      return var6;
   }
}
