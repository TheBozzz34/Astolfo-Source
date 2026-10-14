package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandGameMode extends CommandBase {
   @Override
   public String getCommandName() {
      return "gamemode";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.gamemode.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.gamemode.usage");
      }

      WorldSettings.GameType gameType = this.getGameModeFromCommand(sender, args[0]);
      EntityPlayerMP entityPlayerMP = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
      entityPlayerMP.setGameType(gameType);
      entityPlayerMP.fallDistance = 0.0F;
      if (sender.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback")) {
         entityPlayerMP.addChatMessage(new ChatComponentTranslation("gameMode.changed"));
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("gameMode." + gameType.getName());
      if (entityPlayerMP != sender) {
         notifyOperators(sender, this, 1, "commands.gamemode.success.other", entityPlayerMP.getName(), chatComponentTranslation);
      } else {
         notifyOperators(sender, this, 1, "commands.gamemode.success.self", chatComponentTranslation);
      }
   }

   public WorldSettings.GameType getGameModeFromCommand(ICommandSender iCommandSender, String string) throws CommandException, NumberInvalidException {
      return string.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.getName()) || string.equalsIgnoreCase("s")
         ? WorldSettings.GameType.SURVIVAL
         : (
            string.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.getName()) || string.equalsIgnoreCase("c")
               ? WorldSettings.GameType.CREATIVE
               : (
                  string.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.getName()) || string.equalsIgnoreCase("a")
                     ? WorldSettings.GameType.ADVENTURE
                     : (
                        !string.equalsIgnoreCase(WorldSettings.GameType.SPECTATOR.getName()) && !string.equalsIgnoreCase("sp")
                           ? WorldSettings.getGameTypeById(parseInt(string, 0, WorldSettings.GameType.values().length - 2))
                           : WorldSettings.GameType.SPECTATOR
                     )
               )
         );
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "survival", "creative", "adventure", "spectator")
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames()) : null);
   }

   public String[] getListOfPlayerUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }
}
