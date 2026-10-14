package bypass.command;

import bypass.relation.Relation;
import java.util.Arrays;

public final class FriendsCommand extends Command {
   private static final long d = 26043949585898L;

   public FriendsCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("f", "friend"));
   }

   @Override
   public void runCommand(String[] strings) {
      long l = d ^ 122763104063607L;
      long m = l ^ 10203745657024L;
      int i = (int)((l ^ 10203745657024L) >>> 56);
      int j = (int)((l ^ 10203745657024L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
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
      long l = d ^ 50703020371602L;
      long m = l ^ 80253780467237L;
      int i = (int)((l ^ 80253780467237L) >>> 56);
      int j = (int)((l ^ 80253780467237L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
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
