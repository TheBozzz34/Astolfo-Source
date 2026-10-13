package bypass.command;

import bypass.relation.Relation;
import java.util.Arrays;

public final class FriendsCommand extends Command {
   private static final long d = 26043949585898L;

   public FriendsCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("f", "friend"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 122763104063607L;
      long var10001 = var2 ^ 10203745657024L;
      int var4 = (int)((var2 ^ 10203745657024L) >>> 56);
      int var5 = (int)((var2 ^ 10203745657024L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      switch (var1.length) {
         case 2:
            if (var1[1].equalsIgnoreCase("clear")) {
               client.getRelationManager().n().values().removeIf(Relation::z);
               client.getRelationManager().Z().values().removeIf(Relation::z);
               chat.I("Cleared friend list");
            }
            break;
         case 3:
         case 4:
            this.C(var1);
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void C(String[] var1) {
      long var2 = d ^ 50703020371602L;
      long var10001 = var2 ^ 80253780467237L;
      int var4 = (int)((var2 ^ 80253780467237L) >>> 56);
      int var5 = (int)((var2 ^ 80253780467237L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      String var8 = var1[2];
      switch (var1.length) {
         case 3:
            if (var1[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(var8, var8, true));
               chat.I(String.format("Added %s as friend", var8));
            } else if (var1[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().A(var8)) {
                  client.getRelationManager().n().remove(var8);
                  client.getRelationManager().Z().remove(var8);
                  chat.I(String.format("%s is not a friend anymore", var8));
               } else {
                  chat.I(String.format("%s is not a friend", var8));
               }
            }
            break;
         case 4:
            String var9 = var1[3];
            if (var1[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(var8, var9, true));
               chat.I(String.format("Added %s as friend with alias %s", var8, var9));
            }
      }
   }
}
