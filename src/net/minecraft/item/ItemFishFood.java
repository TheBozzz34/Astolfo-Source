package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean cooked;
   private static final long a = 101241598013922L;

   public ItemFishFood(boolean var1) {
      super(0, 0.0F, false);
      this.cooked = var1;
   }

   @Override
   public int getHealAmount(ItemStack var1) {
      ItemFishFood.FishType var5 = ItemFishFood.FishType.byItemStack(var1);
      return this.cooked && var5.canCook() ? var5.getCookedHealAmount() : var5.getUncookedHealAmount();
   }

   @Override
   public float getSaturationModifier(ItemStack var1) {
      ItemFishFood.FishType var5 = ItemFishFood.FishType.byItemStack(var1);
      return this.cooked && var5.canCook() ? var5.getCookedSaturationModifier() : var5.getUncookedSaturationModifier();
   }

   @Override
   public String getPotionEffect(ItemStack var1) {
      return ItemFishFood.FishType.byItemStack(var1) == ItemFishFood.FishType.PUFFERFISH ? "+0-1+2+3+13&4-4" : null;
   }

   @Override
   public void onFoodEaten(ItemStack var1, World var2, EntityPlayer var3) {
      ItemFishFood.FishType var7 = ItemFishFood.FishType.byItemStack(var1);
      if (var7 == ItemFishFood.FishType.PUFFERFISH) {
         var3.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
         var3.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
         var3.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
      }

      super.onFoodEaten(var1, var2, var3);
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (ItemFishFood.FishType var10 : ItemFishFood.FishType.values()) {
         if (!this.cooked || var10.canCook()) {
            var3.add(new ItemStack(this, 1, var10.getMetadata()));
         }
      }
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      ItemFishFood.FishType var5 = ItemFishFood.FishType.byItemStack(var1);
      return this.getUnlocalizedName() + "." + var5.getUnlocalizedName() + "." + (this.cooked && var5.canCook() ? "cooked" : "raw");
   }

   public boolean w() {
      return this.cooked;
   }

   public enum FishType {
      COD(0, "cod", 2, 0.1F, 5, 0.6F),
      SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
      CLOWNFISH(2, "clownfish", 1, 0.1F),
      PUFFERFISH(3, "pufferfish", 1, 0.1F);

      private static final Map<Integer, ItemFishFood.FishType> META_LOOKUP = Maps.newHashMap();
      private final int meta;
      private final String unlocalizedName;
      private final int uncookedHealAmount;
      private final float uncookedSaturationModifier;
      private final int cookedHealAmount;
      private final float cookedSaturationModifier;
      private boolean cookable = false;
      private static final long a = 27987982860666L;

      FishType(int var3, String var4, int var5, float var6, int var7, float var8) {
         this.meta = var3;
         this.unlocalizedName = var4;
         this.uncookedHealAmount = var5;
         this.uncookedSaturationModifier = var6;
         this.cookedHealAmount = var7;
         this.cookedSaturationModifier = var8;
         this.cookable = true;
      }

      FishType(int var3, String var4, int var5, float var6) {
         this.meta = var3;
         this.unlocalizedName = var4;
         this.uncookedHealAmount = var5;
         this.uncookedSaturationModifier = var6;
         this.cookedHealAmount = 0;
         this.cookedSaturationModifier = 0.0F;
         this.cookable = false;
      }

      public int getMetadata() {
         return this.meta;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      public boolean canCook() {
         return this.cookable;
      }

      public static ItemFishFood.FishType byMetadata(int var0) {
         ItemFishFood.FishType var4 = META_LOOKUP.get(var0);
         return var4 == null ? COD : var4;
      }

      public static ItemFishFood.FishType byItemStack(ItemStack var0) {
         return var0.getItem() instanceof ItemFishFood ? byMetadata(var0.getMetadata()) : COD;
      }

      static {
         for (ItemFishFood.FishType var20 : values()) {
            META_LOOKUP.put(var20.getMetadata(), var20);
         }
      }
   }
}
