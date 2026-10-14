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

   public static void clickSlot(int i, int j, boolean bl) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, j, bl ? 1 : 0, mc.thePlayer);
   }

   public static void dropSlot(int i, int j) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
   }

   public static int findEmptyHotbarSlot() {
      for (int i = 36; i <= 44; i++) {
         ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
         if (itemStack == null) {
            return i;
         }
      }

      return -1;
   }

   public static float getDamageScore(ItemStack itemStack) {
      if (itemStack == null) {
         return -1.0F;
      }

      Multimap multimap = itemStack.getAttributeModifiers();
      double d = 0.0;
      boolean bl = false;

      for (Object object : multimap.entries()) {
         Entry entry = (Entry)object;
         AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
         double e = attributeModifier.getAmount();
         if (attributeModifier.getID() == Item.itemModifierUUID) {
            e += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
            bl = true;
         }

         if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
            d = e;
         } else {
            d = e * 100.0;
         }
      }

      if (d == 0.0 && itemStack.getItem() instanceof ItemSword) {
         ItemSword itemSword = (ItemSword)itemStack.getItem();
         d = itemSword.E();
      }

      if (HypixelGameTracker.game == 3) {
         String string = OldInventoryManager.getLore(itemStack);
         NBTTagList nbtTagList = itemStack.getEnchantmentTagList();
         if (nbtTagList != null && !bl) {
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               short s = nbtTagList.getCompoundTagAt(i).getShort("id");
               short s2 = nbtTagList.getCompoundTagAt(i).getShort("lvl");
               Enchantment enchantment = Enchantment.getEnchantmentById(s);
               if (enchantment != null && enchantment == Enchantment.sharpness) {
                  d += enchantment.calcDamageByCreature(s2, EnumCreatureAttribute.UNDEFINED);
               }
            }
         }

         String[] strings = string.split("\n");

         for (String string2 : strings) {
            if (string2.startsWith("Sword Master")) {
               String[] strings2 = StringUtils.substringsBetween(string2, "(+", " dmg)");

               for (String string3 : strings2) {
                  try {
                     float f = Float.parseFloat(string3);
                     d += f;
                  } catch (Exception exception) {
                  }
               }
            }
         }
      }

      int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack);
      return (float)d + j;
   }

   public static float getBowScore(ItemStack itemStack) {
      return 1 + EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.25F;
   }

   public static float getToolScore(ItemStack itemStack) {
      if (itemStack == null) {
         return -1.0F;
      }

      Block block;
      if (itemStack.getItem() instanceof ItemAxe) {
         block = Blocks.log;
      } else if (itemStack.getItem() instanceof ItemPickaxe) {
         block = Blocks.stone;
      } else {
         if (!(itemStack.getItem() instanceof ItemSpade)) {
            return -1.0F;
         }

         block = Blocks.grass;
      }

      return getToolEfficiency(itemStack, block);
   }

   public static float getToolEfficiency(ItemStack itemStack, Block block) {
      if (itemStack == null) {
         return -1.0F;
      }

      float f = itemStack.getStrVsBlock(block);
      if (f <= 1.0F) {
         return -1.0F;
      }

      if (itemStack.getItem() instanceof ItemMultiTexture) {
         return 0.0F;
      }

      if (itemStack.getItem() instanceof ItemTool) {
         ItemTool itemTool = (ItemTool)itemStack.getItem();
         f += (itemTool.getToolMaterial().getHarvestLevel() + 1) * 30;
      }

      if (f > 1.0F) {
         int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         if (i > 0) {
            f += i * i + 1;
         }
      }

      return f;
   }

   public static boolean isGoodSplashPotion(ItemStack itemStack) {
      if (itemStack != null && itemStack.getItem() instanceof ItemPotion && ItemPotion.isSplash(itemStack.getItemDamage())) {
         for (PotionEffect potionEffect : Items.potionitem.getEffects(itemStack)) {
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            if (!potion.isBadEffect()) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasPotionEffect(ItemStack itemStack, int i) {
      try {
         if (itemStack != null && itemStack.getItem() instanceof ItemPotion) {
            for (PotionEffect potionEffect : Items.potionitem.getEffects(itemStack)) {
               Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
               if (i == potion.id) {
                  return true;
               }
            }
         }

         return false;
      } catch (Exception exception) {
         return false;
      }
   }

   public static int getArmorScore(ItemStack itemStack) {
      if (itemStack == null) {
         return -1;
      }

      if (!(itemStack.getItem() instanceof ItemArmor)) {
         return -1;
      }

      try {
         int i = ((ItemArmor)itemStack.getItem()).damageReduceAmount;
         int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) / 2;
         if (HypixelGameTracker.game == 3) {
            String string = OldInventoryManager.getLore(itemStack);
            if (string.contains("Resistance") || string.contains("Aeon") || string.contains("Provides the wearer") || string.contains("Survivalism Ultimate")) {
               j += 15;
            }

            j += EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, itemStack) / 3;
         }

         return i + j;
      } catch (Exception exception) {
         return -1;
      }
   }
}
