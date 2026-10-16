package bypass.loader.check;

public interface EnvironmentKind {
   static EnvironmentKindC createKindC() {
      return new EnvironmentKindC();
   }

   static EnvironmentKindD createKindD() {
      return new EnvironmentKindD();
   }

   static EnvironmentKind fromCondition(BooleanCondition booleanCondition) {
      return booleanCondition.test() ? createKindC() : createKindD();
   }

   EnvironmentType getType();
}
