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
               client.getRelationManager().n().values().removeIf(Relation::z);
               client.getRelationManager().Z().values().removeIf(Relation::z);
               chat.I("Cleared friend list");
            }
            break;
         case 3:
         case 4:
            this.C(strings);
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void C(String[] strings) {
      String string = strings[2];
      switch (strings.length) {
         case 3:
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string, true));
               chat.I(String.format("Added %s as friend", string));
            } else if (strings[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().A(string)) {
                  client.getRelationManager().n().remove(string);
                  client.getRelationManager().Z().remove(string);
                  chat.I(String.format("%s is not a friend anymore", string));
               } else {
                  chat.I(String.format("%s is not a friend", string));
               }
            }
            break;
         case 4:
            String string2 = strings[3];
            if (strings[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(string, string2, true));
               chat.I(String.format("Added %s as friend with alias %s", string, string2));
            }
      }
   }
}
