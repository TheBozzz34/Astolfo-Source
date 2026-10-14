package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EnchantmentThorns extends Enchantment {
   private static final long m = 22177806924270L;

   public EnchantmentThorns(int i, ResourceLocation resourceLocation, int j) {
      super(i, resourceLocation, j, EnumEnchantmentType.ARMOR_TORSO);
      this.setName("thorns");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemArmor ? true : super.canApply(stack);
   }

   @Override
   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      Random random = user.getRNG();
      ItemStack itemStack = EnchantmentHelper.getEnchantedItem(Enchantment.thorns, user);
      if (func_92094_a(level, random)) {
         if (attacker != null) {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), func_92095_b(level, random));
            attacker.playSound("damage.thorns", 0.5F, 1.0F);
         }

         if (itemStack != null) {
            itemStack.damageItem(3, user);
         }
      } else if (itemStack != null) {
         itemStack.damageItem(1, user);
      }
   }

   public static boolean func_92094_a(int i, Random random2) {
      return i <= 0 ? false : random2.nextFloat() < 0.15F * i;
   }

   public static int func_92095_b(int i, Random random2) {
      return i > 10 ? i - 10 : 1 + random2.nextInt(4);
   }
}
