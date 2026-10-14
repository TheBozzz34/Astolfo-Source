package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private Entity indirectEntity;
   private static final long h = 3164152674115L;

   public EntityDamageSourceIndirect(String damageTypeIn, Entity source, Entity indirectEntityIn) {
      super(damageTypeIn, source);
      this.indirectEntity = indirectEntityIn;
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
   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      IChatComponent iChatComponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemStack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
      String string = "death.attack." + this.damageType;
      String string2 = string + ".item";
      return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(string2)
         ? new ChatComponentTranslation(string2, entityLivingBaseIn.getDisplayName(), iChatComponent, itemStack.getChatComponent())
         : new ChatComponentTranslation(string, entityLivingBaseIn.getDisplayName(), iChatComponent);
   }
}
