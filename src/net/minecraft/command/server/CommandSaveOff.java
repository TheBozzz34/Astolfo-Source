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
   public String getCommandUsage(ICommandSender var1) {
      return "commands.save-off.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      MinecraftServer var6 = MinecraftServer.getServer();
      boolean var7 = false;

      for (int var8 = 0; var8 < var6.worldServers.length; var8++) {
         if (var6.worldServers[var8] != null) {
            WorldServer var9 = var6.worldServers[var8];
            if (!var9.disableLevelSaving) {
               var9.disableLevelSaving = true;
               var7 = true;
            }
         }
      }

      if (var7) {
         notifyOperators(var1, this, "commands.save.disabled");
      } else {
         throw new CommandException("commands.save-off.alreadyOff");
      }
   }
}
