package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private Entity indirectEntity;
   private static final long h = 3164152674115L;

   public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
      super(var1, var2);
      this.indirectEntity = var3;
   }

   @Override
   public Entity getSourceOfDamage() {
      return this.damageSourceEntity;
   }

   @Override
   public Entity getEntity() {
      return this.indirectEntity;
   }

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase var1) {
      IChatComponent var5 = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack var6 = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
      String var7 = "death.attack." + this.damageType;
      String var8 = var7 + ".item";
      return var6 != null && var6.hasDisplayName() && StatCollector.canTranslate(var8)
         ? new ChatComponentTranslation(var8, var1.getDisplayName(), var5, var6.getChatComponent())
         : new ChatComponentTranslation(var7, var1.getDisplayName(), var5);
   }
}
