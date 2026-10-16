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
               client.getRelationManager().getRelations().values().removeIf(relation -> !relation.isFriend());
               client.getRelationManager().getRelationMap().values().removeIf(relation2 -> !relation2.isFriend());
               chat.print("Cleared enemy list");
            }
            break;
         case 3:
         case 4:
            this.addOrRemoveEnemy(strings);
            break;
         default:
            chat.print(this.getName() + ": " + this.getUsage());
      }
   }

   private void addOrRemoveEnemy(String[] strings) {
      String string = strings[2];
      switch (strings.length) {
         case 3:
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string, false));
               chat.print(String.format("Added %s as enemy", string));
            } else if (strings[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().isEnemyName(string)) {
                  client.getRelationManager().getRelations().remove(string);
                  client.getRelationManager().getRelationMap().remove(string);
                  chat.print(String.format("%s is not an enemy anymore", string));
               } else {
                  chat.print(String.format("%s is not an enemy", string));
               }
            }
            break;
         case 4:
            String string2 = strings[3];
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string2, false));
               chat.print(String.format("Added %s as enemy with alias %s", string, string2));
            }
      }
   }
}
