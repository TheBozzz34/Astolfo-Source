package bypass.loader;

public class LoadStages {
   public static LoadStage createDecoyStage1() {
      return new DecoyStage1();
   }

   public static LoadStage createDirectoryStage() {
      return new DirectoryStage();
   }

   public static LoadStage createDecoyStage2() {
      return new DecoyStage2();
   }

   public static LoadStage createDecoyStage3() {
      return new DecoyStage3();
   }

   public static LoadStage createManagersStage() {
      return new ManagersStage();
   }

   public static LoadStage createModulesStage() {
      return new ModulesStage();
   }

   public static LoadStage createGuiStage() {
      return new GuiStage();
   }
}
