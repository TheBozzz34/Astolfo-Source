package bypass.loader.check;

import bypass.loader.LoadStage;

public class StageVerifier {
   private final LoadStageList S;
   private final DefaultCheckRegistry C;
   private static final long a = 27229906820923L;

   public StageVerifier(LoadStageList var1, DefaultCheckRegistry var2) {
      this.S = var1;
      this.C = var2;
   }

   public ThreadDeath t() {
      ThreadDeath var4 = null;

      for (LoadStage var6 : this.S.a().L().getValue()) {
         Verdict var7 = this.C.u(new StageCheckRequest(var6));
         switch (var7) {
            case H:
               var4 = null;
               break;
            default:
               var4 = new ThreadDeath();
               var4.setStackTrace(new StackTraceElement[0]);
               return var4;
         }
      }

      return var4;
   }
}
