package bypass.loader.check;

public interface EnvironmentKind {
   long a = 6853624290782L;

   static EnvironmentKindC k() {
      return new EnvironmentKindC();
   }

   static EnvironmentKindD O() {
      return new EnvironmentKindD();
   }

   static EnvironmentKind M(BooleanCondition var0) {
      return var0.c() ? k() : O();
   }

   EnvironmentType w();
}
