package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {
   private static final long b = 521582521558L;

   @Override
   public String getCommandName() {
      return "stop";
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.stop.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (MinecraftServer.getServer().worldServers != null) {
         notifyOperators(var1, this, "commands.stop.start");
      }

      MinecraftServer.getServer().initiateShutdown();
   }
}
