package bypass.loader.check;

import java.util.List;

public final class DefaultCheckContext implements CheckContext {
   private final CheckRequest c;
   private final List<StageCheck> m;
   private final List<VerdictProcessor> o;
   private static final long a = 107289293741782L;

   public DefaultCheckContext(CheckRequest var1, List<StageCheck> var2, List<VerdictProcessor> var3) {
      this.c = var1;
      this.m = var2;
      this.o = var3;
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
