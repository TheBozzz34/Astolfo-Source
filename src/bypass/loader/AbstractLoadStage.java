package bypass.loader;

import bypass.ClientAccess;

public abstract class AbstractLoadStage implements LoadStage, ClientAccess {
   public int state;

   public abstract StageAction getAction();
}
