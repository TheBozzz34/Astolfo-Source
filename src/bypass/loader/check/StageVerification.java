package bypass.loader.check;

import java.util.Collections;

public class StageVerification {
   private final DefaultCheckRegistry A = new DefaultCheckRegistry(Collections.singleton(new StageValueCheck()), new CachingVerdictResolver());
   private final StageVerifier I;

   public StageVerification(LoadStageList var1) {
      this.I = new StageVerifier(var1, this.A);
   }

   public DefaultCheckRegistry j() {
      return this.A;
   }

   public StageVerifier J() {
      return this.I;
   }
}
