package bypass.loader.check;

import java.util.Collections;

public class StageVerification {
   private final DefaultCheckRegistry registry = new DefaultCheckRegistry(Collections.singleton(new StageValueCheck()), new CachingVerdictResolver());
   private final StageVerifier verifier;

   public StageVerification(LoadStageList loadStageList) {
      this.verifier = new StageVerifier(loadStageList, this.registry);
   }

   public DefaultCheckRegistry getRegistry() {
      return this.registry;
   }

   public StageVerifier getVerifier() {
      return this.verifier;
   }
}
