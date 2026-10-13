package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase {
   private static final long b = 130074280573051L;

   @Override
   public String getCommandName() {
      return "me";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.me.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.me.usage");
      }

      IChatComponent var6 = getChatComponentFromNthArg(var1, var2, 0, !(var1 instanceof EntityPlayer));
      MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.emote", var1.getDisplayName(), var6));
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
   }
}
