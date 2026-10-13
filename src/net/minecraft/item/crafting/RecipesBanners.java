package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.world.World;

public class RecipesBanners {
   private static final long a = 70204417053631L;

   public void addRecipes(CraftingManager var1) {
      for (EnumDyeColor var7 : EnumDyeColor.values()) {
         var1.addRecipe(
            new ItemStack(Items.banner, 1, var7.getDyeDamage()), "###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, var7.getMetadata()), '|', Items.stick
         );
      }

      var1.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
      var1.addRecipe(new RecipesBanners.RecipeAddPattern());
   }

   public static class RecipeAddPattern implements IRecipe {
      private static final long a = 114534032667084L;

      public RecipeAddPattern() {
      }

      @Override
      public boolean matches(InventoryCrafting var1, World var2) {
         boolean var6 = false;

         for (int var7 = 0; var7 < var1.getSizeInventory(); var7++) {
            ItemStack var8 = var1.getStackInSlot(var7);
            if (var8 != null && var8.getItem() == Items.banner) {
               if (var6) {
                  return false;
               }

               if (TileEntityBanner.getPatterns(var8) >= 6) {
                  return false;
               }

               var6 = true;
            }
         }

         return !var6 ? false : this.func_179533_c(var1) != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting var1) {
         ItemStack var5 = null;

         for (int var6 = 0; var6 < var1.getSizeInventory(); var6++) {
            ItemStack var7 = var1.getStackInSlot(var6);
            if (var7 != null && var7.getItem() == Items.banner) {
               var5 = var7.copy();
               var5.stackSize = 1;
               break;
            }
         }

         TileEntityBanner.EnumBannerPattern var11 = this.func_179533_c(var1);
         if (var11 != null) {
            int var12 = 0;

            for (int var8 = 0; var8 < var1.getSizeInventory(); var8++) {
               ItemStack var9 = var1.getStackInSlot(var8);
               if (var9 != null && var9.getItem() == Items.dye) {
                  var12 = var9.getMetadata();
                  break;
               }
            }

            NBTTagCompound var13 = var5.getSubCompound("BlockEntityTag", true);
            NBTTagList var14 = null;
            if (var13.hasKey("Patterns", 9)) {
               var14 = var13.getTagList("Patterns", 10);
            } else {
               var14 = new NBTTagList();
               var13.setTag("Patterns", var14);
            }

            NBTTagCompound var10 = new NBTTagCompound();
            var10.setString("Pattern", var11.getPatternID());
            var10.setInteger("Color", var12);
            var14.appendTag(var10);
         }

         return var5;
      }

      @Override
      public int getRecipeSize() {
         return 10;
      }

      @Override
      public ItemStack getRecipeOutput() {
         return null;
      }

      @Override
      public ItemStack[] getRemainingItems(InventoryCrafting var1) {
         ItemStack[] var5 = new ItemStack[var1.getSizeInventory()];

         for (int var6 = 0; var6 < var5.length; var6++) {
            ItemStack var7 = var1.getStackInSlot(var6);
            if (var7 != null && var7.getItem().hasContainerItem()) {
               var5[var6] = new ItemStack(var7.getItem().getContainerItem());
            }
         }

         return var5;
      }

      private TileEntityBanner.EnumBannerPattern func_179533_c(InventoryCrafting var1) {
         for (TileEntityBanner.EnumBannerPattern var8 : TileEntityBanner.EnumBannerPattern.values()) {
            if (var8.m()) {
               boolean var9 = true;
               if (var8.o()) {
                  boolean var10 = false;
                  boolean var11 = false;

                  for (int var12 = 0; var12 < var1.getSizeInventory() && var9; var12++) {
                     ItemStack var13 = var1.getStackInSlot(var12);
                     if (var13 != null && var13.getItem() != Items.banner) {
                        if (var13.getItem() == Items.dye) {
                           if (var11) {
                              var9 = false;
                              break;
                           }

                           var11 = true;
                        } else {
                           if (var10 || !var13.isItemEqual(var8.getCraftingStack())) {
                              var9 = false;
                              break;
                           }

                           var10 = true;
                        }
                     }
                  }

                  if (!var10) {
                     var9 = false;
                  }
               } else if (var1.getSizeInventory() == var8.getCraftingLayers().length * var8.getCraftingLayers()[0].length()) {
                  int var15 = -1;

                  for (int var16 = 0; var16 < var1.getSizeInventory() && var9; var16++) {
                     int var17 = var16 / 3;
                     int var18 = var16 % 3;
                     ItemStack var14 = var1.getStackInSlot(var16);
                     if (var14 != null && var14.getItem() != Items.banner) {
                        if (var14.getItem() != Items.dye) {
                           var9 = false;
                           break;
                        }

                        if (var15 != -1 && var15 != var14.getMetadata()) {
                           var9 = false;
                           break;
                        }

                        if (var8.getCraftingLayers()[var17].charAt(var18) == ' ') {
                           var9 = false;
                           break;
                        }

                        var15 = var14.getMetadata();
                     } else if (var8.getCraftingLayers()[var17].charAt(var18) != ' ') {
                        var9 = false;
                        break;
                     }
                  }
               } else {
                  var9 = false;
               }

               if (var9) {
                  return var8;
               }
            }
         }

         return null;
      }
   }

   public static class RecipeDuplicatePattern implements IRecipe {
      private static final long a = 131059367198762L;

      public RecipeDuplicatePattern() {
      }

      @Override
      public boolean matches(InventoryCrafting var1, World var2) {
         ItemStack var6 = null;
         ItemStack var7 = null;

         for (int var8 = 0; var8 < var1.getSizeInventory(); var8++) {
            ItemStack var9 = var1.getStackInSlot(var8);
            if (var9 != null) {
               if (var9.getItem() != Items.banner) {
                  return false;
               }

               if (var6 != null && var7 != null) {
                  return false;
               }

               int var10 = TileEntityBanner.getBaseColor(var9);
               boolean var11 = TileEntityBanner.getPatterns(var9) > 0;
               if (var6 != null) {
                  if (var11) {
                     return false;
                  }

                  if (var10 != TileEntityBanner.getBaseColor(var6)) {
                     return false;
                  }

                  var7 = var9;
               } else if (var7 != null) {
                  if (!var11) {
                     return false;
                  }

                  if (var10 != TileEntityBanner.getBaseColor(var7)) {
                     return false;
                  }

                  var6 = var9;
               } else if (var11) {
                  var6 = var9;
               } else {
                  var7 = var9;
               }
            }
         }

         return var6 != null && var7 != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting var1) {
         for (int var5 = 0; var5 < var1.getSizeInventory(); var5++) {
            ItemStack var6 = var1.getStackInSlot(var5);
            if (var6 != null && TileEntityBanner.getPatterns(var6) > 0) {
               ItemStack var7 = var6.copy();
               var7.stackSize = 1;
               return var7;
            }
         }

         return null;
      }

      @Override
      public int getRecipeSize() {
         return 2;
      }

      @Override
      public ItemStack getRecipeOutput() {
         return null;
      }

      @Override
      public ItemStack[] getRemainingItems(InventoryCrafting var1) {
         ItemStack[] var5 = new ItemStack[var1.getSizeInventory()];

         for (int var6 = 0; var6 < var5.length; var6++) {
            ItemStack var7 = var1.getStackInSlot(var6);
            if (var7 != null) {
               if (var7.getItem().hasContainerItem()) {
                  var5[var6] = new ItemStack(var7.getItem().getContainerItem());
               } else if (var7.hasTagCompound() && TileEntityBanner.getPatterns(var7) > 0) {
                  var5[var6] = var7.copy();
                  var5[var6].stackSize = 1;
               }
            }
         }

         return var5;
      }
   }
}
