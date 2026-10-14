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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.publish.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      String string = MinecraftServer.getServer().shareToLAN(WorldSettings.GameType.SURVIVAL, false);
      if (string != null) {
         notifyOperators(sender, this, "commands.publish.started", string);
      } else {
         notifyOperators(sender, this, "commands.publish.failed");
      }
   }
}
