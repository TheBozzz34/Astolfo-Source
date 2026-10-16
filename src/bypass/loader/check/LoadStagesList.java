package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.loader.LoadStages;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadStagesList {
   private final List<LoadStage> stages = new ArrayList<>(
      Arrays.asList(LoadStages.createDecoyStage1(), LoadStages.createDirectoryStage(), LoadStages.createManagersStage(), LoadStages.createModulesStage())
   );

   public List<LoadStage> getStages() {
      return this.stages;
   }
}
