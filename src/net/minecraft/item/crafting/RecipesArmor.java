package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesArmor {
   private String[][] recipePatterns = new String[][]{{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
   private Item[][] recipeItems = new Item[][]{
      {Items.leather, Items.iron_ingot, Items.diamond, Items.gold_ingot},
      {Items.leather_helmet, Items.iron_helmet, Items.diamond_helmet, Items.golden_helmet},
      {Items.leather_chestplate, Items.iron_chestplate, Items.diamond_chestplate, Items.golden_chestplate},
      {Items.leather_leggings, Items.iron_leggings, Items.diamond_leggings, Items.golden_leggings},
      {Items.leather_boots, Items.iron_boots, Items.diamond_boots, Items.golden_boots}
   };
   private static final long b = 132361122403266L;
   private static final long f = 3710999232920944728L;

   public void addRecipes(CraftingManager var1) {
      for (int var4 = 0; var4 < this.recipeItems[0].length; var4++) {
         Item var5 = this.recipeItems[0][var4];

         for (int var6 = 0; var6 < this.recipeItems.length - 1; var6++) {
            Item var7 = this.recipeItems[var6 + 1][var4];
            var1.addRecipe(new ItemStack(var7), this.recipePatterns[var6], Character.valueOf((char)((int)f)), var5);
         }
      }
   }
}
