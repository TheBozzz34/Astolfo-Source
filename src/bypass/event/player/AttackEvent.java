package bypass.event.player;

import bypass.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent implements Event {
   private Entity entity;

   public AttackEvent(Entity entity) {
      this.entity = entity;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
