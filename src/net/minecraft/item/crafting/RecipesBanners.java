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
   public void addRecipes(CraftingManager craftingManager) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         craftingManager.addRecipe(
            new ItemStack(Items.banner, 1, enumDyeColor.getDyeDamage()), "###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, enumDyeColor.getMetadata()), '|', Items.stick
         );
      }

      craftingManager.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
      craftingManager.addRecipe(new RecipesBanners.RecipeAddPattern());
   }

   public static class RecipeAddPattern implements IRecipe {
      public RecipeAddPattern() {
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         boolean bl = false;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() == Items.banner) {
               if (bl) {
                  return false;
               }

               if (TileEntityBanner.getPatterns(itemStack) >= 6) {
                  return false;
               }

               bl = true;
            }
         }

         return !bl ? false : this.func_179533_c(inv) != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         ItemStack itemStack = null;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack2 = inv.getStackInSlot(i);
            if (itemStack2 != null && itemStack2.getItem() == Items.banner) {
               itemStack = itemStack2.copy();
               itemStack.stackSize = 1;
               break;
            }
         }

         TileEntityBanner.EnumBannerPattern enumBannerPattern = this.func_179533_c(inv);
         if (enumBannerPattern != null) {
            int j = 0;

            for (int k = 0; k < inv.getSizeInventory(); k++) {
               ItemStack itemStack3 = inv.getStackInSlot(k);
               if (itemStack3 != null && itemStack3.getItem() == Items.dye) {
                  j = itemStack3.getMetadata();
                  break;
               }
            }

            NBTTagCompound nbtTagCompound = itemStack.getSubCompound("BlockEntityTag", true);
            NBTTagList nbtTagList = null;
            if (nbtTagCompound.hasKey("Patterns", 9)) {
               nbtTagList = nbtTagCompound.getTagList("Patterns", 10);
            } else {
               nbtTagList = new NBTTagList();
               nbtTagCompound.setTag("Patterns", nbtTagList);
            }

            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            nbtTagCompound2.setString("Pattern", enumBannerPattern.getPatternID());
            nbtTagCompound2.setInteger("Color", j);
            nbtTagList.appendTag(nbtTagCompound2);
         }

         return itemStack;
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
      public ItemStack[] getRemainingItems(InventoryCrafting inv) {
         ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];

         for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem().hasContainerItem()) {
               itemStacks[i] = new ItemStack(itemStack.getItem().getContainerItem());
            }
         }

         return itemStacks;
      }

      private TileEntityBanner.EnumBannerPattern func_179533_c(InventoryCrafting inventoryCrafting) {
         for (TileEntityBanner.EnumBannerPattern enumBannerPattern : TileEntityBanner.EnumBannerPattern.values()) {
            if (enumBannerPattern.hasValidCrafting()) {
               boolean bl = true;
               if (enumBannerPattern.hasCraftingStack()) {
                  boolean bl2 = false;
                  boolean bl3 = false;

                  for (int i = 0; i < inventoryCrafting.getSizeInventory() && bl; i++) {
                     ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
                     if (itemStack != null && itemStack.getItem() != Items.banner) {
                        if (itemStack.getItem() == Items.dye) {
                           if (bl3) {
                              bl = false;
                              break;
                           }

                           bl3 = true;
                        } else {
                           if (bl2 || !itemStack.isItemEqual(enumBannerPattern.getCraftingStack())) {
                              bl = false;
                              break;
                           }

                           bl2 = true;
                        }
                     }
                  }

                  if (!bl2) {
                     bl = false;
                  }
               } else if (inventoryCrafting.getSizeInventory() == enumBannerPattern.getCraftingLayers().length * enumBannerPattern.getCraftingLayers()[0].length()) {
                  int j = -1;

                  for (int k = 0; k < inventoryCrafting.getSizeInventory() && bl; k++) {
                     int l = k / 3;
                     int m = k % 3;
                     ItemStack itemStack2 = inventoryCrafting.getStackInSlot(k);
                     if (itemStack2 != null && itemStack2.getItem() != Items.banner) {
                        if (itemStack2.getItem() != Items.dye) {
                           bl = false;
                           break;
                        }

                        if (j != -1 && j != itemStack2.getMetadata()) {
                           bl = false;
                           break;
                        }

                        if (enumBannerPattern.getCraftingLayers()[l].charAt(m) == ' ') {
                           bl = false;
                           break;
                        }

                        j = itemStack2.getMetadata();
                     } else if (enumBannerPattern.getCraftingLayers()[l].charAt(m) != ' ') {
                        bl = false;
                        break;
                     }
                  }
               } else {
                  bl = false;
               }

               if (bl) {
                  return enumBannerPattern;
               }
            }
         }

         return null;
      }
   }

   public static class RecipeDuplicatePattern implements IRecipe {
      public RecipeDuplicatePattern() {
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         ItemStack itemStack = null;
         ItemStack itemStack2 = null;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack3 = inv.getStackInSlot(i);
            if (itemStack3 != null) {
               if (itemStack3.getItem() != Items.banner) {
                  return false;
               }

               if (itemStack != null && itemStack2 != null) {
                  return false;
               }

               int j = TileEntityBanner.getBaseColor(itemStack3);
               boolean bl = TileEntityBanner.getPatterns(itemStack3) > 0;
               if (itemStack != null) {
                  if (bl) {
                     return false;
                  }

                  if (j != TileEntityBanner.getBaseColor(itemStack)) {
                     return false;
                  }

                  itemStack2 = itemStack3;
               } else if (itemStack2 != null) {
                  if (!bl) {
                     return false;
                  }

                  if (j != TileEntityBanner.getBaseColor(itemStack2)) {
                     return false;
                  }

                  itemStack = itemStack3;
               } else if (bl) {
                  itemStack = itemStack3;
               } else {
                  itemStack2 = itemStack3;
               }
            }
         }

         return itemStack != null && itemStack2 != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null && TileEntityBanner.getPatterns(itemStack) > 0) {
               ItemStack itemStack2 = itemStack.copy();
               itemStack2.stackSize = 1;
               return itemStack2;
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
      public ItemStack[] getRemainingItems(InventoryCrafting inv) {
         ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];

         for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null) {
               if (itemStack.getItem().hasContainerItem()) {
                  itemStacks[i] = new ItemStack(itemStack.getItem().getContainerItem());
               } else if (itemStack.hasTagCompound() && TileEntityBanner.getPatterns(itemStack) > 0) {
                  itemStacks[i] = itemStack.copy();
                  itemStacks[i].stackSize = 1;
               }
            }
         }

         return itemStacks;
      }
   }
}
