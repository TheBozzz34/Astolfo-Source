package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.util.container.ListContainerOwner;

public class LoadStageListFactory {
   private final ListContainerOwner<LoadStage> q;

   public LoadStageListFactory() {
      LoadStagesList loadStagesList = new LoadStagesList();
      this.q = new ListContainerOwner<>(loadStagesList.p());
   }

   public ListContainerOwner<LoadStage> e() {
      return this.q;
   }
}
