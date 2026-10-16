package bypass.loader;

import bypass.loader.check.GameJarHashList;
import bypass.loader.check.StringListHolder;

public class IntegrityCheckHolder {
   private final GameJarHashList gameJarHashList = new GameJarHashList();
   private final StringListHolder stringListHolder = new StringListHolder(this.gameJarHashList.getHashContainer());
   private final IntegrityCheck integrityCheck = new IntegrityCheck(this.stringListHolder);

   public IntegrityCheck getIntegrityCheck() {
      return this.integrityCheck;
   }
}
