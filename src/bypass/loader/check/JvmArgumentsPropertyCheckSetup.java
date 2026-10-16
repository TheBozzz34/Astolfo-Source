package bypass.loader.check;

public class JvmArgumentsPropertyCheckSetup {
   private final JvmArgumentsPropertyListOwner listOwner = new JvmArgumentsPropertyListOwner();
   private final StringListHolder stringListHolder = new StringListHolder(this.listOwner.getPatternContainer());
   private final JvmArgumentsPropertyCheck jvmArgumentsPropertyCheck = new JvmArgumentsPropertyCheck(this.stringListHolder);

   public StringListHolder getStringListHolder() {
      return this.stringListHolder;
   }

   public JvmArgumentsPropertyCheck getJvmArgumentsPropertyCheck() {
      return this.jvmArgumentsPropertyCheck;
   }
}
