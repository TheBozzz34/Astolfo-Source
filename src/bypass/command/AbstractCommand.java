package bypass.command;

import java.util.List;

public abstract class AbstractCommand implements CommandInfo {
   private String name;
   private String usage;
   private List<String> aliases;
   private static final long c = 77846584001266L;

   public AbstractCommand(String var1, String var2) {
      this.name = var1;
      this.usage = var2;
   }

   public abstract void runCommand(String[] var1);

   public void setAliases(List<String> var1) {
      this.aliases = var1;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getUsage() {
      return this.usage;
   }
}
