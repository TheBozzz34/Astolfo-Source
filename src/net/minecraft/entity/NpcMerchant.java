package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
   private InventoryMerchant theMerchantInventory;
   private EntityPlayer customer;
   private MerchantRecipeList recipeList;
   private IChatComponent field_175548_d;
   private static final long a = 127329321400188L;

   public NpcMerchant(EntityPlayer entityPlayer, IChatComponent iChatComponent) {
      this.customer = entityPlayer;
      this.field_175548_d = iChatComponent;
      this.theMerchantInventory = new InventoryMerchant(entityPlayer, this);
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.customer;
   }

   @Override
   public void setCustomer(EntityPlayer entityPlayer) {
   }

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer entityPlayer) {
      return this.recipeList;
   }

   @Override
   public void setRecipes(MerchantRecipeList recipeList) {
      this.recipeList = recipeList;
   }

   @Override
   public void useRecipe(MerchantRecipe recipe) {
      recipe.incrementToolUses();
   }

   @Override
   public void verifySellingItem(ItemStack stack) {
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.field_175548_d != null ? this.field_175548_d : new ChatComponentTranslation("entity.Villager.name");
   }
}
