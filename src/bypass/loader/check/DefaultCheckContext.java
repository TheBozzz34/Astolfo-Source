package bypass.loader.check;

import java.util.List;

public final class DefaultCheckContext implements CheckContext {
   private final CheckRequest request;
   private final List<StageCheck> checks;
   private final List<VerdictProcessor> processors;

   public DefaultCheckContext(CheckRequest checkRequest, List<StageCheck> list, List<VerdictProcessor> list2) {
      this.request = checkRequest;
      this.checks = list;
      this.processors = list2;
   }

   @Override
   public CheckRequest getRequest() {
      return this.request;
   }

   @Override
   public List<StageCheck> getChecks() {
      return this.checks;
   }

   @Override
   public List<VerdictProcessor> getProcessors() {
      return this.processors;
   }
}
