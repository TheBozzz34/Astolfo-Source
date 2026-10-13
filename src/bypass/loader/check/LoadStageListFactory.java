package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.util.container.ListContainerOwner;

public class LoadStageListFactory {
   private final ListContainerOwner<LoadStage> q;

   public LoadStageListFactory() {
      LoadStagesList var1 = new LoadStagesList();
      this.q = new ListContainerOwner<>(var1.p());
   }

   public ListContainerOwner<LoadStage> e() {
      return this.q;
   }
}
