package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase {
   private static final long b = 11668053773556L;

   @Override
   public String getCommandName() {
      return "save-off";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.save-off.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      boolean bl = false;

      for (int i = 0; i < minecraftServer.worldServers.length; i++) {
         if (minecraftServer.worldServers[i] != null) {
            WorldServer worldServer = minecraftServer.worldServers[i];
            if (!worldServer.disableLevelSaving) {
               worldServer.disableLevelSaving = true;
               bl = true;
            }
         }
      }

      if (bl) {
         notifyOperators(sender, this, "commands.save.disabled");
      } else {
         throw new CommandException("commands.save-off.alreadyOff");
      }
   }
}
