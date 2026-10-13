package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
   private static final long b = 104174024262782L;

   @Override
   public String getCommandName() {
      return "setidletimeout";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.setidletimeout.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length != 1) {
         throw new WrongUsageException("commands.setidletimeout.usage");
      }

      int var6 = parseInt(var2[0], 0);
      MinecraftServer.getServer().setPlayerIdleTimeout(var6);
      notifyOperators(var1, this, "commands.setidletimeout.success", var6);
   }
}
