package bypass.command;

import bypass.relation.Relation;
import java.util.Arrays;

public final class EnemiesCommand extends Command {
   private static final long d = 134687672370386L;

   public EnemiesCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("e", "enemy", "target"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 2713307218255L;
      long var10001 = var2 ^ 10203745657024L;
      int var4 = (int)((var2 ^ 10203745657024L) >>> 56);
      int var5 = (int)((var2 ^ 10203745657024L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      switch (var1.length) {
         case 2:
            if (var1[1].equalsIgnoreCase("clear")) {
               client.getRelationManager().n().values().removeIf(var0 -> !var0.z());
               client.getRelationManager().Z().values().removeIf(var0 -> !var0.z());
               chat.I("Cleared enemy list");
            }
            break;
         case 3:
         case 4:
            this.h(var1);
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void h(String[] var1) {
      long var2 = d ^ 129024580267991L;
      long var10001 = var2 ^ 138988955504216L;
      int var4 = (int)((var2 ^ 138988955504216L) >>> 56);
      int var5 = (int)((var2 ^ 138988955504216L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      String var8 = var1[2];
      switch (var1.length) {
         case 3:
            if (var1[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(var8, var8, false));
               chat.I(String.format("Added %s as enemy", var8));
            } else if (var1[1].equalsIgnoreCase("del")) {
               if (client.getRelationManager().U(var8)) {
                  client.getRelationManager().n().remove(var8);
                  client.getRelationManager().Z().remove(var8);
                  chat.I(String.format("%s is not an enemy anymore", var8));
               } else {
                  chat.I(String.format("%s is not an enemy", var8));
               }
            }
            break;
         case 4:
            String var9 = var1[3];
            if (var1[1].equalsIgnoreCase("add")) {
               client.getRelationManager().add(new Relation(var8, var9, false));
               chat.I(String.format("Added %s as enemy with alias %s", var8, var9));
            }
      }
   }
}
