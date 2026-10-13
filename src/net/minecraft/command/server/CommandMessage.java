package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandMessage extends CommandBase {
   private static final long b = 112663161126504L;

   @Override
   public List<String> getCommandAliases() {
      return Arrays.asList("w", "msg");
   }

   @Override
   public String getCommandName() {
      return "tell";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.message.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.message.usage");
      }

      EntityPlayerMP var6 = getPlayer(var1, var2[0]);
      if (var6 == var1) {
         throw new PlayerNotFoundException("commands.message.sameTarget");
      }

      IChatComponent var7 = getChatComponentFromNthArg(var1, var2, 1, !(var1 instanceof EntityPlayer));
      ChatComponentTranslation var8 = new ChatComponentTranslation("commands.message.display.incoming", var1.getDisplayName(), var7.createCopy());
      ChatComponentTranslation var9 = new ChatComponentTranslation("commands.message.display.outgoing", var6.getDisplayName(), var7.createCopy());
      var8.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
      var9.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
      var6.addChatMessage(var8);
      var1.addChatMessage(var9);
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
