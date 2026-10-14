package bypass.loader.check;

public interface EnvironmentKind {
   long a = 6853624290782L;

   static EnvironmentKindC k() {
      return new EnvironmentKindC();
   }

   static EnvironmentKindD O() {
      return new EnvironmentKindD();
   }

   static EnvironmentKind M(BooleanCondition booleanCondition) {
      return booleanCondition.c() ? k() : O();
   }

   EnvironmentType w();
}
