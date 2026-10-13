package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesIngots {
   private Object[][] recipeItems = new Object[][]{
      {Blocks.gold_block, new ItemStack(Items.gold_ingot, 9)},
      {Blocks.iron_block, new ItemStack(Items.iron_ingot, 9)},
      {Blocks.diamond_block, new ItemStack(Items.diamond, 9)},
      {Blocks.emerald_block, new ItemStack(Items.emerald, 9)},
      {Blocks.lapis_block, new ItemStack(Items.dye, 9, EnumDyeColor.BLUE.getDyeDamage())},
      {Blocks.redstone_block, new ItemStack(Items.redstone, 9)},
      {Blocks.coal_block, new ItemStack(Items.coal, 9, 0)},
      {Blocks.hay_block, new ItemStack(Items.wheat, 9)},
      {Blocks.slime_block, new ItemStack(Items.slime_ball, 9)}
   };
   private static final long a = 102935300485656L;

   public void addRecipes(CraftingManager var1) {
      for (int var4 = 0; var4 < this.recipeItems.length; var4++) {
         Block var5 = (Block)this.recipeItems[var4][0];
         ItemStack var6 = (ItemStack)this.recipeItems[var4][1];
         var1.addRecipe(new ItemStack(var5), "###", "###", "###", '#', var6);
         var1.addRecipe(var6, "#", '#', var5);
      }

      var1.addRecipe(new ItemStack(Items.gold_ingot), "###", "###", "###", '#', Items.gold_nugget);
      var1.addRecipe(new ItemStack(Items.gold_nugget, 9), "#", '#', Items.gold_ingot);
   }
}
