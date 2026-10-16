package bypass.command;

import bypass.relation.Relation;
import java.util.Arrays;

public final class FriendsCommand extends Command {
   public FriendsCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("f", "friend"));
   }

   @Override
   public void runCommand(String[] strings) {
      switch (strings.length) {
         case 2:
            if (strings[1].equalsIgnoreCase("clear")) {
               client.getRelationManager().getRelations().values().removeIf(Relation::isFriend);
               client.getRelationManager().getRelationMap().values().removeIf(Relation::isFriend);
               chat.print("Cleared friend list");
            }
            break;
         case 3:
         case 4:
            this.handleAddOrRemove(strings);
            break;
         default:
            chat.print(this.getName() + ": " + this.getUsage());
      }
   }

   private void handleAddOrRemove(String[] strings) {
      String string = strings[2];
      switch (strings.length) {
         case 3:
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string, true));
               chat.print(String.format("Added %s as friend", string));
            } else if (strings[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().isFriendName(string)) {
                  client.getRelationManager().getRelations().remove(string);
                  client.getRelationManager().getRelationMap().remove(string);
                  chat.print(String.format("%s is not a friend anymore", string));
               } else {
                  chat.print(String.format("%s is not a friend", string));
               }
            }
            break;
         case 4:
            String string2 = strings[3];
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string2, true));
               chat.print(String.format("Added %s as friend with alias %s", string, string2));
            }
      }
   }
}
