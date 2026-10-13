package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemSword extends Item {
   private float attackDamage;
   private final Item.ToolMaterial material;
   private static final long a = 116543646305325L;
   private static final long d = 7996582622379710784L;

   public ItemSword(Item.ToolMaterial var1) {
      this.material = var1;
      this.maxStackSize = 1;
      this.setMaxDamage(var1.getMaxUses());
      this.setCreativeTab(CreativeTabs.tabCombat);
      this.attackDamage = 4.0F + var1.getDamageVsEntity();
   }

   public float Q() {
      return this.material.getDamageVsEntity();
   }

   @Override
   public float getStrVsBlock(ItemStack var1, Block var2) {
      if (var2 == Blocks.web) {
         return 15.0F;
      }

      Material var6 = var2.getMaterial();
      return var6 != Material.plants && var6 != Material.vine && var6 != Material.coral && var6 != Material.leaves && var6 != Material.gourd ? 1.0F : 1.5F;
   }

   @Override
   public boolean hitEntity(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.damageItem(1, var3);
      return true;
   }

   @Override
   public boolean onBlockDestroyed(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      if (var3.getBlockHardness(var2, var4) != 0.0) {
         var1.damageItem(2, var5);
      }

      return true;
   }

   @Override
   public boolean isFull3D() {
      return true;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack var1) {
      return EnumAction.BLOCK;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack var1) {
      return (int)d;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
      return var1;
   }

   @Override
   public boolean canHarvestBlock(Block var1) {
      return var1 == Blocks.web;
   }

   @Override
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public String getToolMaterialName() {
      return this.material.toString();
   }

   @Override
   public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
      return this.material.getRepairItem() == var2.getItem() ? true : super.getIsRepairable(var1, var2);
   }

   @Override
   public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
      Multimap var3 = super.getItemAttributeModifiers();
      var3.put(
         SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", this.attackDamage, 0)
      );
      return var3;
   }

   public float E() {
      return this.attackDamage;
   }
}
