package bypass.loader.check;

public class ProcessCheckSetup {
   private final ProcessListOwner v = new ProcessListOwner();
   private final StringListHolder r = new StringListHolder(this.v.R());
   private final ProcessCheck T = new ProcessCheck(this.r);

   public StringListHolder U() {
      return this.r;
   }

   public ProcessCheck U_rss() {
      return this.T;
   }
}
