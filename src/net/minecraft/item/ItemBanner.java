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
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      }

      pos = pos.offset(side);
      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      if (side == EnumFacing.UP) {
         int i = MathHelper.floor_double((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
         worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, i), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
      }

      stack.stackSize--;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBanner) {
         ((TileEntityBanner)tileEntity).setItemValues(stack);
      }

      return true;
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      String string = "item.banner.";
      EnumDyeColor enumDyeColor = this.getBaseColor(stack);
      string = string + enumDyeColor.getUnlocalizedName() + ".name";
      return StatCollector.translateToLocal(string);
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      NBTTagCompound nbtTagCompound = stack.getSubCompound("BlockEntityTag", false);
      if (nbtTagCompound != null && nbtTagCompound.hasKey("Patterns")) {
         NBTTagList nbtTagList = nbtTagCompound.getTagList("Patterns", 10);

         for (int i = 0; i < nbtTagList.tagCount() && i < 6; i++) {
            NBTTagCompound nbtTagCompound2 = nbtTagList.getCompoundTagAt(i);
            EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(nbtTagCompound2.getInteger("Color"));
            TileEntityBanner.EnumBannerPattern enumBannerPattern = TileEntityBanner.EnumBannerPattern.getPatternByID(nbtTagCompound2.getString("Pattern"));
            if (enumBannerPattern != null) {
               tooltip.add(StatCollector.translateToLocal("item.banner." + enumBannerPattern.getPatternName() + "." + enumDyeColor.getUnlocalizedName()));
            }
         }
      }
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass == 0) {
         return 16777215;
      }

      EnumDyeColor enumDyeColor = this.getBaseColor(stack);
      return enumDyeColor.getMapColor().colorValue;
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(nbtTagCompound, enumDyeColor.getDyeDamage(), (NBTTagList)null);
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound2.setTag("BlockEntityTag", nbtTagCompound);
         ItemStack itemStack = new ItemStack(itemIn, 1, enumDyeColor.getDyeDamage());
         itemStack.setTagCompound(nbtTagCompound2);
         subItems.add(itemStack);
      }
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return CreativeTabs.tabDecorations;
   }

   private EnumDyeColor getBaseColor(ItemStack stack) {
      NBTTagCompound nbtTagCompound = stack.getSubCompound("BlockEntityTag", false);
      EnumDyeColor enumDyeColor = null;
      if (nbtTagCompound != null && nbtTagCompound.hasKey("Base")) {
         enumDyeColor = EnumDyeColor.byDyeDamage(nbtTagCompound.getInteger("Base"));
      } else {
         enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
      }

      return enumDyeColor;
   }
}
