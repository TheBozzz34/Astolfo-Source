package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.util.container.ListContainerOwner;

public class LoadStageList {
   private final ListContainerOwner<LoadStage> stageContainer;

   public LoadStageList(ListContainerOwner<LoadStage> listContainerOwner) {
      this.stageContainer = listContainerOwner;
   }

   public ListContainerOwner<LoadStage> getStageContainer() {
      return this.stageContainer;
   }
}
