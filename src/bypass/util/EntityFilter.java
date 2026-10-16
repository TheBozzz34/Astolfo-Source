package bypass.util;

import net.minecraft.entity.Entity;

public interface EntityFilter {
   boolean matches(Entity entity);
}
