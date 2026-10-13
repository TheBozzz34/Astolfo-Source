package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
   public Entity damageSourceEntity;
   private boolean isThornsDamage = false;
   private static final long g = 92196515917146L;

   public EntityDamageSource(String var1, Entity var2) {
      super(var1);
      this.damageSourceEntity = var2;
   }

   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   @Override
   public Entity getEntity() {
      return this.damageSourceEntity;
   }

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase var1) {
      ItemStack var5 = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
      String var6 = "death.attack." + this.damageType;
      String var7 = var6 + ".item";
      return var5 != null && var5.hasDisplayName() && StatCollector.canTranslate(var7)
         ? new ChatComponentTranslation(var7, var1.getDisplayName(), this.damageSourceEntity.getDisplayName(), var5.getChatComponent())
         : new ChatComponentTranslation(var6, var1.getDisplayName(), this.damageSourceEntity.getDisplayName());
   }

   @Override
   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
   }
}
