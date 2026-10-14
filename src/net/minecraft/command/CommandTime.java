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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.time.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length > 1) {
         if (args[0].equals("set")) {
            int i;
            if (args[1].equals("day")) {
               i = 1000;
            } else if (args[1].equals("night")) {
               i = 13000;
            } else {
               i = parseInt(args[1], 0);
            }

            this.setTime(sender, i);
            notifyOperators(sender, this, "commands.time.set", i);
            return;
         }

         if (args[0].equals("add")) {
            int j = parseInt(args[1], 0);
            this.addTime(sender, j);
            notifyOperators(sender, this, "commands.time.added", j);
            return;
         }

         if (args[0].equals("query")) {
            if (args[1].equals("daytime")) {
               int k = (int)(sender.getEntityWorld().getWorldTime() % 2147483647L);
               sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, k);
               notifyOperators(sender, this, "commands.time.query", k);
               return;
            }

            if (args[1].equals("gametime")) {
               int l = (int)(sender.getEntityWorld().getTotalWorldTime() % 2147483647L);
               sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, l);
               notifyOperators(sender, this, "commands.time.query", l);
               return;
            }
         }
      }

      throw new WrongUsageException("commands.time.usage");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "set", "add", "query")
         : (
            args.length == 2 && args[0].equals("set")
               ? getListOfStringsMatchingLastWord(args, "day", "night")
               : (args.length == 2 && args[0].equals("query") ? getListOfStringsMatchingLastWord(args, "daytime", "gametime") : null)
         );
   }

   public void setTime(ICommandSender sender, int time) {
      for (int i = 0; i < MinecraftServer.getServer().worldServers.length; i++) {
         MinecraftServer.getServer().worldServers[i].setWorldTime(time);
      }
   }

   public void addTime(ICommandSender sender, int time) {
      for (int i = 0; i < MinecraftServer.getServer().worldServers.length; i++) {
         WorldServer worldServer = MinecraftServer.getServer().worldServers[i];
         worldServer.setWorldTime(worldServer.getWorldTime() + time);
      }
   }
}
