package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   private static final Logger logger = LogManager.getLogger();
   private final Map<String, ICommand> commandMap = Maps.newHashMap();
   private final Set<ICommand> commandSet = Sets.newHashSet();

   @Override
   public int executeCommand(ICommandSender sender, String rawCommand) {
      rawCommand = rawCommand.trim();
      if (rawCommand.startsWith("/")) {
         rawCommand = rawCommand.substring(1);
      }

      String[] strings = rawCommand.split(" ");
      String string = strings[0];
      strings = dropFirstString(strings);
      ICommand iCommand = this.commandMap.get(string);
      int i = this.getUsernameIndex(iCommand, strings);
      int j = 0;
      if (iCommand == null) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.notFound");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } else if (iCommand.canCommandSenderUseCommand(sender)) {
         if (i > -1) {
            List list = PlayerSelector.matchEntities(sender, strings[i], Entity.class);
            String string2 = strings[i];
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());

            for (Object object : list) {
               Entity entity = (Entity)object;
               strings[i] = entity.getUniqueID().toString();
               if (this.tryExecute(sender, strings, iCommand, rawCommand)) {
                  j++;
               }
            }

            strings[i] = string2;
         } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
            if (this.tryExecute(sender, strings, iCommand, rawCommand)) {
               j++;
            }
         }
      } else {
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.generic.permission");
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation2);
      }

      sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, j);
      return j;
   }

   public boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input) {
      try {
         command.processCommand(sender, args);
         return true;
      } catch (WrongUsageException wrongUsageException) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
            "commands.generic.usage", new ChatComponentTranslation(wrongUsageException.getMessage(), wrongUsageException.getErrorObjects())
         );
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } catch (CommandException commandException) {
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(commandException.getMessage(), commandException.getErrorObjects());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation2);
      } catch (Throwable throwable) {
         ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("commands.generic.exception");
         chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation3);
         logger.warn("Couldn't process command: '" + input + "'");
      }

      return false;
   }

   public ICommand registerCommand(ICommand command) {
      this.commandMap.put(command.getCommandName(), command);
      this.commandSet.add(command);

      for (String string : command.getCommandAliases()) {
         ICommand iCommand = this.commandMap.get(string);
         if (iCommand == null || !iCommand.getCommandName().equals(string)) {
            this.commandMap.put(string, command);
         }
      }

      return command;
   }

   private static String[] dropFirstString(String[] input) {
      String[] strings = new String[input.length - 1];
      System.arraycopy(input, 1, strings, 0, input.length - 1);
      return strings;
   }

   @Override
   public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos) {
      String[] strings = input.split(" ", -1);
      String string = strings[0];
      if (strings.length == 1) {
         ArrayList arrayList = Lists.newArrayList();

         for (Entry entry : this.commandMap.entrySet()) {
            if (CommandBase.doesStringStartWith(string, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(sender)) {
               arrayList.add(entry.getKey());
            }
         }

         return arrayList;
      } else {
         if (strings.length > 1) {
            ICommand iCommand = this.commandMap.get(string);
            if (iCommand != null && iCommand.canCommandSenderUseCommand(sender)) {
               return iCommand.addTabCompletionOptions(sender, dropFirstString(strings), pos);
            }
         }

         return null;
      }
   }

   @Override
   public List<ICommand> getPossibleCommands(ICommandSender sender) {
      ArrayList arrayList = Lists.newArrayList();

      for (ICommand iCommand : this.commandSet) {
         if (iCommand.canCommandSenderUseCommand(sender)) {
            arrayList.add(iCommand);
         }
      }

      return arrayList;
   }

   @Override
   public Map<String, ICommand> getCommands() {
      return this.commandMap;
   }

   private int getUsernameIndex(ICommand command, String[] args) {
      if (command == null) {
         return -1;
      }

      for (int i = 0; i < args.length; i++) {
         if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])) {
            return i;
         }
      }

      return -1;
   }
}
