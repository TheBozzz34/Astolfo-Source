package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {
   @Override
   public String getCommandName() {
      return "help";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.help.usage";
   }

   @Override
   public List<String> getCommandAliases() {
      return Arrays.asList("?");
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      List list = this.getSortedPossibleCommands(sender);
      int i = (list.size() - 1) / 7;
      int j = 0;

      try {
         j = args.length == 0 ? 0 : parseInt(args[0], 1, i + 1) - 1;
      } catch (NumberInvalidException numberInvalidException) {
         Map map = this.getCommands();
         ICommand iCommand = (ICommand)map.get(args[0]);
         if (iCommand != null) {
            throw new WrongUsageException(iCommand.getCommandUsage(sender));
         }

         if (MathHelper.parseIntWithDefault(args[0], -1) != -1) {
            throw numberInvalidException;
         }

         throw new CommandNotFoundException();
      }

      int k = Math.min((j + 1) * 7, list.size());
      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.help.header", j + 1, i + 1);
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      sender.addChatMessage(chatComponentTranslation);

      for (int l = j * 7; l < k; l++) {
         ICommand iCommand2 = (ICommand)list.get(l);
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(iCommand2.getCommandUsage(sender));
         chatComponentTranslation2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + iCommand2.getCommandName() + " "));
         sender.addChatMessage(chatComponentTranslation2);
      }

      if (j == 0 && sender instanceof EntityPlayer) {
         ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("commands.help.footer");
         chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.GREEN);
         sender.addChatMessage(chatComponentTranslation3);
      }
   }

   public List<ICommand> getSortedPossibleCommands(ICommandSender iCommandSender) {
      List list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(iCommandSender);
      Collections.sort(list);
      return list;
   }

   public Map<String, ICommand> getCommands() {
      return MinecraftServer.getServer().getCommandManager().getCommands();
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         Set set = this.getCommands().keySet();
         return getListOfStringsMatchingLastWord(args, (java.lang.String[])(set.toArray(new String[set.size()])));
      } else {
         return null;
      }
   }
}
