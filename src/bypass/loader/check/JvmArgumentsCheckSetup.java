package bypass.loader.check;

public class JvmArgumentsCheckSetup {
   private final JvmArgumentsListOwner P = new JvmArgumentsListOwner();
   private final StringListHolder T = new StringListHolder(this.P.d());
   private final JvmArgumentsCheck C = new JvmArgumentsCheck(this.T);

   public StringListHolder P() {
      return this.T;
   }

   public JvmArgumentsCheck Y() {
      return this.C;
   }
}
