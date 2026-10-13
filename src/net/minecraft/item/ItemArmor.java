package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
   public static final String[] EMPTY_SLOT_NAMES = new String[]{
      "minecraft:items/empty_armor_slot_helmet",
      "minecraft:items/empty_armor_slot_chestplate",
      "minecraft:items/empty_armor_slot_leggings",
      "minecraft:items/empty_armor_slot_boots"
   };
   private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
      private static final long b = 34130440522126L;

      @Override
      public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
         BlockPos var6 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
         int var7 = var6.getX();
         int var8 = var6.getY();
         int var9 = var6.getZ();
         AxisAlignedBB var10 = new AxisAlignedBB(var7, var8, var9, var7 + 1, var8 + 1, var9 + 1);
         List var11 = var1.getWorld()
            .getEntitiesWithinAABB(EntityLivingBase.class, var10, Predicates.and(EntitySelectors.NOT_SPECTATING, new EntitySelectors.ArmoredMob(var2)));
         if (var11.size() > 0) {
            EntityLivingBase var12 = (EntityLivingBase)var11.get(0);
            int var13 = var12 instanceof EntityPlayer ? 1 : 0;
            int var14 = EntityLiving.getArmorPosition(var2);
            ItemStack var15 = var2.copy();
            var15.stackSize = 1;
            var12.setCurrentItemOrArmor(var14 - var13, var15);
            if (var12 instanceof EntityLiving) {
               ((EntityLiving)var12).setEquipmentDropChance(var14, 2.0F);
            }

            var2.stackSize--;
            return var2;
         } else {
            return super.dispenseStack(var1, var2);
         }
      }
   };
   public final int armorType;
   public final int damageReduceAmount;
   public final int renderIndex;
   private final ItemArmor.ArmorMaterial material;
   private static final long a = 29010270412698L;

   public ItemArmor(ItemArmor.ArmorMaterial var1, int var2, int var3) {
      this.material = var1;
      this.armorType = var3;
      this.renderIndex = var2;
      this.damageReduceAmount = var1.getDamageReductionAmount(var3);
      this.setMaxDamage(var1.getDurability(var3));
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabCombat);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      if (var2 > 0) {
         return 16777215;
      }

      int var6 = this.getColor(var1);
      if (var6 < 0) {
         var6 = 16777215;
      }

      return var6;
   }

   @Override
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public ItemArmor.ArmorMaterial getArmorMaterial() {
      return this.material;
   }

   public boolean hasColor(ItemStack var1) {
      return this.material != ItemArmor.ArmorMaterial.LEATHER
         ? false
         : (
            !var1.hasTagCompound()
               ? false
               : (!var1.getTagCompound().hasKey("display", 10) ? false : var1.getTagCompound().getCompoundTag("display").hasKey("color", 3))
         );
   }

   public int getColor(ItemStack var1) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         return -1;
      }

      NBTTagCompound var5 = var1.getTagCompound();
      if (var5 != null) {
         NBTTagCompound var6 = var5.getCompoundTag("display");
         if (var6 != null && var6.hasKey("color", 3)) {
            return var6.getInteger("color");
         }
      }

      return 10511680;
   }

   public void removeColor(ItemStack var1) {
      if (this.material == ItemArmor.ArmorMaterial.LEATHER) {
         NBTTagCompound var5 = var1.getTagCompound();
         if (var5 != null) {
            NBTTagCompound var6 = var5.getCompoundTag("display");
            if (var6.hasKey("color")) {
               var6.removeTag("color");
            }
         }
      }
   }

   public void setColor(ItemStack var1, int var2) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      }

      NBTTagCompound var6 = var1.getTagCompound();
      if (var6 == null) {
         var6 = new NBTTagCompound();
         var1.setTagCompound(var6);
      }

      NBTTagCompound var7 = var6.getCompoundTag("display");
      if (!var6.hasKey("display", 10)) {
         var6.setTag("display", var7);
      }

      var7.setInteger("color", var2);
   }

   @Override
   public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
      return this.material.getRepairItem() == var2.getItem() ? true : super.getIsRepairable(var1, var2);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      int var7 = EntityLiving.getArmorPosition(var1) - 1;
      ItemStack var8 = var3.getCurrentArmor(var7);
      if (var8 == null) {
         var3.setCurrentItemOrArmor(var7, var1.copy());
         var1.stackSize = 0;
      }

      return var1;
   }

   public enum ArmorMaterial {
      LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
      CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
      IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
      GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
      DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

      private final String name;
      private final int maxDamageFactor;
      private final int[] damageReductionAmountArray;
      private final int enchantability;
      private static final long a = 33212273193048L;

      ArmorMaterial(String var3, int var4, int[] var5, int var6) {
         this.name = var3;
         this.maxDamageFactor = var4;
         this.damageReductionAmountArray = var5;
         this.enchantability = var6;
      }

      public int getDurability(int var1) {
         return ItemArmor.maxDamageArray[var1] * this.maxDamageFactor;
      }

      public int getDamageReductionAmount(int var1) {
         return this.damageReductionAmountArray[var1];
      }

      public int getEnchantability() {
         return this.enchantability;
      }

      public Item getRepairItem() {
         return this == LEATHER
            ? Items.leather
            : (
               this == CHAIN
                  ? Items.iron_ingot
                  : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null)))
            );
      }

      public String getName() {
         return this.name;
      }
   }
}
