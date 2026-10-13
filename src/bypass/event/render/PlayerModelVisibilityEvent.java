package bypass.event.render;

import bypass.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class PlayerModelVisibilityEvent extends CancellableEvent {
   private Entity entity;

   public PlayerModelVisibilityEvent(Entity var1) {
      this.entity = var1;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
