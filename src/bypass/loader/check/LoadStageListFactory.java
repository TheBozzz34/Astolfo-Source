package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.util.container.ListContainerOwner;

public class LoadStageListFactory {
   private final ListContainerOwner<LoadStage> stageContainer;

   public LoadStageListFactory() {
      LoadStagesList loadStagesList = new LoadStagesList();
      this.stageContainer = new ListContainerOwner<>(loadStagesList.getStages());
   }

   public ListContainerOwner<LoadStage> getStageContainer() {
      return this.stageContainer;
   }
}
