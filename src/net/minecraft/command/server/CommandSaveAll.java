package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
   @Override
   public String getCommandName() {
      return "save-all";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.save.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      sender.addChatMessage(new ChatComponentTranslation("commands.save.start"));
      if (minecraftServer.getConfigurationManager() != null) {
         minecraftServer.getConfigurationManager().saveAllPlayerData();
      }

      try {
         for (int i = 0; i < minecraftServer.worldServers.length; i++) {
            if (minecraftServer.worldServers[i] != null) {
               WorldServer worldServer = minecraftServer.worldServers[i];
               boolean bl = worldServer.disableLevelSaving;
               worldServer.disableLevelSaving = false;
               worldServer.saveAllChunks(true, (IProgressUpdate)null);
               worldServer.disableLevelSaving = bl;
            }
         }

         if (args.length > 0 && "flush".equals(args[0])) {
            sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart"));

            for (int j = 0; j < minecraftServer.worldServers.length; j++) {
               if (minecraftServer.worldServers[j] != null) {
                  WorldServer worldServer2 = minecraftServer.worldServers[j];
                  boolean bl2 = worldServer2.disableLevelSaving;
                  worldServer2.disableLevelSaving = false;
                  worldServer2.saveChunkData();
                  worldServer2.disableLevelSaving = bl2;
               }
            }

            sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd"));
         }
      } catch (MinecraftException minecraftException) {
         notifyOperators(sender, this, "commands.save.failed", minecraftException.getMessage());
         return;
      }

      notifyOperators(sender, this, "commands.save.success");
   }
}
