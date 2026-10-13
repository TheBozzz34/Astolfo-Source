package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandBroadcast extends CommandBase {
   private static final long b = 72447635736398L;

   @Override
   public String getCommandName() {
      return "say";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 1;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.say.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 0 && var2[0].length() > 0) {
         IChatComponent var6 = getChatComponentFromNthArg(var1, var2, 0, true);
         MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.announcement", var1.getDisplayName(), var6));
      } else {
         throw new WrongUsageException("commands.say.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}
