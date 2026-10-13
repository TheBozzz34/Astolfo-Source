package bypass.loader.check;

public class JvmArgumentsPropertyCheckSetup {
   private final JvmArgumentsPropertyListOwner z = new JvmArgumentsPropertyListOwner();
   private final StringListHolder d = new StringListHolder(this.z.k());
   private final JvmArgumentsPropertyCheck c = new JvmArgumentsPropertyCheck(this.d);

   public StringListHolder I() {
      return this.d;
   }

   public JvmArgumentsPropertyCheck b() {
      return this.c;
   }
}
