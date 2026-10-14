package bypass.loader.check;

import bypass.loader.LoadStage;
import bypass.loader.LoadStages;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadStagesList {
   private final List<LoadStage> t = new ArrayList<>(Arrays.asList(LoadStages.N(), LoadStages.R(), LoadStages.D(), LoadStages.I()));

   public List<LoadStage> p() {
      return this.t;
   }
}
