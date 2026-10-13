package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandDefaultGameMode extends CommandGameMode {
   private static final long b = 90224620185621L;

   @Override
   public String getCommandName() {
      return "defaultgamemode";
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.defaultgamemode.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.defaultgamemode.usage");
      }

      WorldSettings.GameType var5 = this.getGameModeFromCommand(var1, var2[0]);
      this.setGameType(var5);
      notifyOperators(var1, this, "commands.defaultgamemode.success", new ChatComponentTranslation("gameMode." + var5.getName()));
   }

   public void setGameType(WorldSettings.GameType var1) {
      MinecraftServer var5 = MinecraftServer.getServer();
      var5.setGameType(var1);
      if (var5.getForceGamemode()) {
         for (EntityPlayerMP var7 : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            var7.setGameType(var1);
            var7.fallDistance = 0.0F;
         }
      }
   }
}
