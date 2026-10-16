package bypass.loader.check;

public class EnvironmentKinds {
   public static boolean isNotKindD(EnvironmentKind environmentKind) {
      return environmentKind.getType() != EnvironmentType.D;
   }

   public static boolean isNotKindC(EnvironmentKind environmentKind) {
      return environmentKind.getType() != EnvironmentType.C;
   }
}
