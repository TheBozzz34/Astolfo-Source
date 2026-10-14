package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOn extends CommandBase {
   private static final long b = 9485189166587L;

   @Override
   public String getCommandName() {
      return "save-on";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.save-on.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      boolean bl = false;

      for (int i = 0; i < minecraftServer.worldServers.length; i++) {
         if (minecraftServer.worldServers[i] != null) {
            WorldServer worldServer = minecraftServer.worldServers[i];
            if (worldServer.disableLevelSaving) {
               worldServer.disableLevelSaving = false;
               bl = true;
            }
         }
      }

      if (bl) {
         notifyOperators(sender, this, "commands.save.enabled");
      } else {
         throw new CommandException("commands.save-on.alreadyOn");
      }
   }
}
