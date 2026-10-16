package bypass.loader.check;

public class JvmArgumentsCheckSetup {
   private final JvmArgumentsListOwner listOwner = new JvmArgumentsListOwner();
   private final StringListHolder stringListHolder = new StringListHolder(this.listOwner.getPatternContainer());
   private final JvmArgumentsCheck jvmArgumentsCheck = new JvmArgumentsCheck(this.stringListHolder);

   public StringListHolder getStringListHolder() {
      return this.stringListHolder;
   }

   public JvmArgumentsCheck getJvmArgumentsCheck() {
      return this.jvmArgumentsCheck;
   }
}
