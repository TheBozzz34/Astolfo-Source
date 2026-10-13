package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons {
   private String[][] recipePatterns = new String[][]{{"X", "X", "#"}};
   private Object[][] recipeItems = new Object[][]{
      {Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot},
      {Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword}
   };
   private static final long b = 46100627285384L;

   public void addRecipes(CraftingManager var1) {
      for (int var4 = 0; var4 < this.recipeItems[0].length; var4++) {
         Object var5 = this.recipeItems[0][var4];

         for (int var6 = 0; var6 < this.recipeItems.length - 1; var6++) {
            Item var7 = (Item)this.recipeItems[var6 + 1][var4];
            var1.addRecipe(new ItemStack(var7), this.recipePatterns[var6], '#', Items.stick, 'X', var5);
         }
      }

      var1.addRecipe(new ItemStack(Items.bow, 1), " #X", "# X", " #X", 'X', Items.string, '#', Items.stick);
      var1.addRecipe(new ItemStack(Items.arrow, 4), "X", "#", "Y", 'Y', Items.feather, 'X', Items.flint, '#', Items.stick);
   }
}
