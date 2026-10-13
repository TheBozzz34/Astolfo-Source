package bypass.loader;

public class LoadStages {
   public static LoadStage N() {
      return new DecoyStage1();
   }

   public static LoadStage R() {
      return new DirectoryStage();
   }

   public static LoadStage j() {
      return new DecoyStage2();
   }

   public static LoadStage h() {
      return new DecoyStage3();
   }

   public static LoadStage D() {
      return new ManagersStage();
   }

   public static LoadStage I() {
      return new ModulesStage();
   }

   public static LoadStage w() {
      return new GuiStage();
   }
}
