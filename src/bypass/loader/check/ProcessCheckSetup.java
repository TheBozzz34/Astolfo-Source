package bypass.loader.check;

public class ProcessCheckSetup {
   private final ProcessListOwner processListOwner = new ProcessListOwner();
   private final StringListHolder stringListHolder = new StringListHolder(this.processListOwner.getProcessContainer());
   private final ProcessCheck processCheck = new ProcessCheck(this.stringListHolder);

   public StringListHolder getStringListHolder() {
      return this.stringListHolder;
   }

   public ProcessCheck getProcessCheck() {
      return this.processCheck;
   }
}
