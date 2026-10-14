package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.util.container.ListContainerOwner;

public class LoadStageList {
   private final ListContainerOwner<LoadStage> E;

   public LoadStageList(ListContainerOwner<LoadStage> listContainerOwner) {
      this.E = listContainerOwner;
   }

   public ListContainerOwner<LoadStage> a() {
      return this.E;
   }
}
