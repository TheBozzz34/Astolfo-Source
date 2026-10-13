package bypass.loader;

import bypass.loader.check.GameJarHashList;
import bypass.loader.check.StringListHolder;

public class IntegrityCheckHolder {
   private final GameJarHashList Q = new GameJarHashList();
   private final StringListHolder H = new StringListHolder(this.Q.E());
   private final IntegrityCheck z = new IntegrityCheck(this.H);
   private static final long a = 119136717163238L;

   public IntegrityCheck z() {
      return this.z;
   }
}
