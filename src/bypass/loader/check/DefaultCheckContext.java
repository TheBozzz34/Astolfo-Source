package bypass.loader.check;

import java.util.List;

public final class DefaultCheckContext implements CheckContext {
   private final CheckRequest c;
   private final List<StageCheck> m;
   private final List<VerdictProcessor> o;

   public DefaultCheckContext(CheckRequest checkRequest, List<StageCheck> list, List<VerdictProcessor> list2) {
      this.c = checkRequest;
      this.m = list;
      this.o = list2;
   }

   @Override
   public CheckRequest f() {
      return this.c;
   }

   @Override
   public List<StageCheck> i() {
      return this.m;
   }

   @Override
   public List<VerdictProcessor> a() {
      return this.o;
   }
}
