package bypass.command;

import bypass.relation.Relation;
import java.util.Arrays;

public final class EnemiesCommand extends Command {
   public EnemiesCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("e", "enemy", "target"));
   }

   @Override
   public void runCommand(String[] strings) {
      switch (strings.length) {
         case 2:
            if (strings[1].equalsIgnoreCase("clear")) {
               client.getRelationManager().n().values().removeIf(relation -> !relation.z());
               client.getRelationManager().Z().values().removeIf(relation2 -> !relation2.z());
               chat.I("Cleared enemy list");
            }
            break;
         case 3:
         case 4:
            this.h(strings);
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void h(String[] strings) {
      String string = strings[2];
      switch (strings.length) {
         case 3:
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string, false));
               chat.I(String.format("Added %s as enemy", string));
            } else if (strings[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().U(string)) {
                  client.getRelationManager().n().remove(string);
                  client.getRelationManager().Z().remove(string);
                  chat.I(String.format("%s is not an enemy anymore", string));
               } else {
                  chat.I(String.format("%s is not an enemy", string));
               }
            }
            break;
         case 4:
            String string2 = strings[3];
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string2, false));
               chat.I(String.format("Added %s as enemy with alias %s", string, string2));
            }
      }
   }
}
