package bypass.loader.check;

public interface EnvironmentKind {
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
