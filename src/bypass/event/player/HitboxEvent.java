package bypass.event.player;

import bypass.event.Event;
import net.minecraft.entity.Entity;

public class HitboxEvent implements Event {
   public float borderSize = 0.1F;
   public Entity entity;

   public HitboxEvent(Entity entity) {
      this.entity = entity;
   }
}
