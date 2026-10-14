package bypass.event.render;

import bypass.event.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class RenderItemEvent extends CancellableEvent {
   private EntityLivingBase entity;
   private ItemStack stack;

   public RenderItemEvent(EntityLivingBase entityLivingBase, ItemStack itemStack) {
      this.entity = entityLivingBase;
      this.stack = itemStack;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
