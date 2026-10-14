package bypass.command;

import java.util.List;

public abstract class AbstractCommand implements CommandInfo {
   private String name;
   private String usage;
   private List<String> aliases;
   private static final long c = 77846584001266L;

   public AbstractCommand(String string, String string2) {
      this.name = string;
      this.usage = string2;
   }

   public abstract void runCommand(String[] strings);

   public void setAliases(List<String> list) {
      this.aliases = list;
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
