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

   public NpcMerchant(EntityPlayer var1, IChatComponent var2) {
      this.customer = var1;
      this.field_175548_d = var2;
      this.theMerchantInventory = new InventoryMerchant(var1, this);
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.customer;
   }

   @Override
   public void setCustomer(EntityPlayer var1) {
   }

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer var1) {
      return this.recipeList;
   }

   @Override
   public void setRecipes(MerchantRecipeList var1) {
      this.recipeList = var1;
   }

   @Override
   public void useRecipe(MerchantRecipe var1) {
      var1.incrementToolUses();
   }

   @Override
   public void verifySellingItem(ItemStack var1) {
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.field_175548_d != null ? this.field_175548_d : new ChatComponentTranslation("entity.Villager.name");
   }
}
