package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase {
   private static final long b = 38201229033972L;

   @Override
   public String getCommandName() {
      return "time";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.time.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 1) {
         if (var2[0].equals("set")) {
            int var9;
            if (var2[1].equals("day")) {
               var9 = 1000;
            } else if (var2[1].equals("night")) {
               var9 = 13000;
            } else {
               var9 = parseInt(var2[1], 0);
            }

            this.setTime(var1, var9);
            notifyOperators(var1, this, "commands.time.set", var9);
            return;
         }

         if (var2[0].equals("add")) {
            int var8 = parseInt(var2[1], 0);
            this.addTime(var1, var8);
            notifyOperators(var1, this, "commands.time.added", var8);
            return;
         }

         if (var2[0].equals("query")) {
            if (var2[1].equals("daytime")) {
               int var7 = (int)(var1.getEntityWorld().getWorldTime() % 2147483647L);
               var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var7);
               notifyOperators(var1, this, "commands.time.query", var7);
               return;
            }

            if (var2[1].equals("gametime")) {
               int var6 = (int)(var1.getEntityWorld().getTotalWorldTime() % 2147483647L);
               var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var6);
               notifyOperators(var1, this, "commands.time.query", var6);
               return;
            }
         }
      }

      throw new WrongUsageException("commands.time.usage");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, "set", "add", "query")
         : (
            var2.length == 2 && var2[0].equals("set")
               ? getListOfStringsMatchingLastWord(var2, "day", "night")
               : (var2.length == 2 && var2[0].equals("query") ? getListOfStringsMatchingLastWord(var2, "daytime", "gametime") : null)
         );
   }

   public void setTime(ICommandSender var1, int var2) {
      for (int var6 = 0; var6 < MinecraftServer.getServer().worldServers.length; var6++) {
         MinecraftServer.getServer().worldServers[var6].setWorldTime(var2);
      }
   }

   public void addTime(ICommandSender var1, int var2) {
      for (int var6 = 0; var6 < MinecraftServer.getServer().worldServers.length; var6++) {
         WorldServer var7 = MinecraftServer.getServer().worldServers[var6];
         var7.setWorldTime(var7.getWorldTime() + var2);
      }
   }
}
