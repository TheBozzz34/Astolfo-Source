package bypass.util;

import bypass.ClientAccess;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.player.OldInventoryManager;
import com.google.common.collect.Multimap;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.StringUtils;

public final class InventoryUtil implements ClientAccess {
   private static final long b = 92494590215507L;

   public static void clickSlot(int var0, int var1, boolean var2) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var0, var1, var2 ? 1 : 0, mc.thePlayer);
   }

   public static void dropSlot(int var0, int var1) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var0, 1, 4, mc.thePlayer);
   }

   public static int findEmptyHotbarSlot() {
      for (int var3 = 36; var3 <= 44; var3++) {
         ItemStack var4 = mc.thePlayer.inventoryContainer.getSlot(var3).getStack();
         if (var4 == null) {
            return var3;
         }
      }

      return -1;
   }

   public static float getDamageScore(ItemStack var0) {
      if (var0 == null) {
         return -1.0F;
      }

      Multimap var4 = var0.getAttributeModifiers();
      double var5 = 0.0;
      boolean var7 = false;

      for (Object var9$raw : var4.entries()) {
         Entry var9 = (Entry)var9$raw;
         AttributeModifier var10 = (AttributeModifier)var9.getValue();
         double var11 = var10.getAmount();
         if (var10.getID() == Item.itemModifierUUID) {
            var11 += EnchantmentHelper.getModifierForCreature(var0, EnumCreatureAttribute.UNDEFINED);
            var7 = true;
         }

         if (var10.getOperation() != 1 && var10.getOperation() != 2) {
            var5 = var11;
         } else {
            var5 = var11 * 100.0;
         }
      }

      if (var5 == 0.0 && var0.getItem() instanceof ItemSword) {
         ItemSword var22 = (ItemSword)var0.getItem();
         var5 = var22.E();
      }

      if (HypixelGameTracker.game == 3) {
         String var23 = OldInventoryManager.getLore(var0);
         NBTTagList var25 = var0.getEnchantmentTagList();
         if (var25 != null && !var7) {
            for (int var26 = 0; var26 < var25.tagCount(); var26++) {
               short var28 = var25.getCompoundTagAt(var26).getShort("id");
               short var12 = var25.getCompoundTagAt(var26).getShort("lvl");
               Enchantment var13 = Enchantment.getEnchantmentById(var28);
               if (var13 != null && var13 == Enchantment.sharpness) {
                  var5 += var13.calcDamageByCreature(var12, EnumCreatureAttribute.UNDEFINED);
               }
            }
         }

         String[] var27 = var23.split("\n");

         for (String var14 : var27) {
            if (var14.startsWith("Sword Master")) {
               String[] var15 = StringUtils.substringsBetween(var14, "(+", " dmg)");

               for (String var19 : var15) {
                  try {
                     float var20 = Float.parseFloat(var19);
                     var5 += var20;
                  } catch (Exception var21) {
                  }
               }
            }
         }
      }

      int var24 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, var0);
      return (float)var5 + var24;
   }

   public static float getBowScore(ItemStack var0) {
      return 1 + EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, var0)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, var0) * 0.25F;
   }

   public static float getToolScore(ItemStack var0) {
      if (var0 == null) {
         return -1.0F;
      }

      Block var4;
      if (var0.getItem() instanceof ItemAxe) {
         var4 = Blocks.log;
      } else if (var0.getItem() instanceof ItemPickaxe) {
         var4 = Blocks.stone;
      } else {
         if (!(var0.getItem() instanceof ItemSpade)) {
            return -1.0F;
         }

         var4 = Blocks.grass;
      }

      return getToolEfficiency(var0, var4);
   }

   public static float getToolEfficiency(ItemStack var0, Block var1) {
      if (var0 == null) {
         return -1.0F;
      }

      float var5 = var0.getStrVsBlock(var1);
      if (var5 <= 1.0F) {
         return -1.0F;
      }

      if (var0.getItem() instanceof ItemMultiTexture) {
         return 0.0F;
      }

      if (var0.getItem() instanceof ItemTool) {
         ItemTool var6 = (ItemTool)var0.getItem();
         var5 += (var6.getToolMaterial().getHarvestLevel() + 1) * 30;
      }

      if (var5 > 1.0F) {
         int var7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, var0);
         if (var7 > 0) {
            var5 += var7 * var7 + 1;
         }
      }

      return var5;
   }

   public static boolean isGoodSplashPotion(ItemStack var0) {
      if (var0 != null && var0.getItem() instanceof ItemPotion && ItemPotion.isSplash(var0.getItemDamage())) {
         for (PotionEffect var5 : Items.potionitem.getEffects(var0)) {
            Potion var6 = Potion.potionTypes[var5.getPotionID()];
            if (!var6.isBadEffect()) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasPotionEffect(ItemStack var0, int var1) {
      try {
         if (var0 != null && var0.getItem() instanceof ItemPotion) {
            for (PotionEffect var6 : Items.potionitem.getEffects(var0)) {
               Potion var7 = Potion.potionTypes[var6.getPotionID()];
               if (var1 == var7.id) {
                  return true;
               }
            }
         }

         return false;
      } catch (Exception var8) {
         return false;
      }
   }

   public static int getArmorScore(ItemStack var0) {
      if (var0 == null) {
         return -1;
      }

      if (!(var0.getItem() instanceof ItemArmor)) {
         return -1;
      }

      try {
         int var4 = ((ItemArmor)var0.getItem()).damageReduceAmount;
         int var5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, var0) / 2;
         if (HypixelGameTracker.game == 3) {
            String var6 = OldInventoryManager.getLore(var0);
            if (var6.contains("Resistance") || var6.contains("Aeon") || var6.contains("Provides the wearer") || var6.contains("Survivalism Ultimate")) {
               var5 += 15;
            }

            var5 += EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, var0) / 3;
         }

         return var4 + var5;
      } catch (Exception var7) {
         return -1;
      }
   }
}
