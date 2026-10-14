package bypass.module.other;

import bypass.module.Category;
import bypass.module.Module;

public final class NoMouseIntersect extends Module {
   public NoMouseIntersect(Category category) {
      super(category);
      this.setHidden(false);
   }
}
