package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;

public class CommandPublishLocalServer extends CommandBase {
   private static final long b = 99805926635730L;

   @Override
   public String getCommandName() {
      return "publish";
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.publish.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      String var6 = MinecraftServer.getServer().shareToLAN(WorldSettings.GameType.SURVIVAL, false);
      if (var6 != null) {
         notifyOperators(var1, this, "commands.publish.started", var6);
      } else {
         notifyOperators(var1, this, "commands.publish.failed");
      }
   }
}
