package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase {
   private static final long b = 44996384990703L;

   @Override
   public String getCommandName() {
      return "toggledownfall";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.downfall.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      this.toggleDownfall();
      notifyOperators(var1, this, "commands.downfall.success");
   }

   public void toggleDownfall() {
      WorldInfo var4 = MinecraftServer.getServer().worldServers[0].getWorldInfo();
      var4.setRaining(!var4.isRaining());
   }
}
