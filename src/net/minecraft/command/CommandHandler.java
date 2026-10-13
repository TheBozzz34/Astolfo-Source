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
   private static final long a = 48821789413626L;

   @Override
   public int executeCommand(ICommandSender var1, String var2) {
      var2 = var2.trim();
      if (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      String[] var6 = var2.split(" ");
      String var7 = var6[0];
      var6 = dropFirstString(var6);
      ICommand var8 = this.commandMap.get(var7);
      int var9 = this.getUsernameIndex(var8, var6);
      int var10 = 0;
      if (var8 == null) {
         ChatComponentTranslation var11 = new ChatComponentTranslation("commands.generic.notFound");
         var11.getChatStyle().setColor(EnumChatFormatting.RED);
         var1.addChatMessage(var11);
      } else if (var8.canCommandSenderUseCommand(var1)) {
         if (var9 > -1) {
            List var17 = PlayerSelector.matchEntities(var1, var6[var9], Entity.class);
            String var12 = var6[var9];
            var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var17.size());

            for (Object var14$raw : var17) {
               Entity var14 = (Entity)var14$raw;
               var6[var9] = var14.getUniqueID().toString();
               if (this.tryExecute(var1, var6, var8, var2)) {
                  var10++;
               }
            }

            var6[var9] = var12;
         } else {
            var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
            if (this.tryExecute(var1, var6, var8, var2)) {
               var10++;
            }
         }
      } else {
         ChatComponentTranslation var18 = new ChatComponentTranslation("commands.generic.permission");
         var18.getChatStyle().setColor(EnumChatFormatting.RED);
         var1.addChatMessage(var18);
      }

      var1.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, var10);
      return var10;
   }

   public boolean tryExecute(ICommandSender var1, String[] var2, ICommand var3, String var4) {
      try {
         var3.processCommand(var1, var2);
         return true;
      } catch (WrongUsageException var9) {
         ChatComponentTranslation var13 = new ChatComponentTranslation(
            "commands.generic.usage", new ChatComponentTranslation(var9.getMessage(), var9.getErrorObjects())
         );
         var13.getChatStyle().setColor(EnumChatFormatting.RED);
         var1.addChatMessage(var13);
      } catch (CommandException var10) {
         ChatComponentTranslation var12 = new ChatComponentTranslation(var10.getMessage(), var10.getErrorObjects());
         var12.getChatStyle().setColor(EnumChatFormatting.RED);
         var1.addChatMessage(var12);
      } catch (Throwable var11) {
         ChatComponentTranslation var8 = new ChatComponentTranslation("commands.generic.exception");
         var8.getChatStyle().setColor(EnumChatFormatting.RED);
         var1.addChatMessage(var8);
         logger.warn("Couldn't process command: '" + var4 + "'");
      }

      return false;
   }

   public ICommand registerCommand(ICommand var1) {
      this.commandMap.put(var1.getCommandName(), var1);
      this.commandSet.add(var1);

      for (String var6 : var1.getCommandAliases()) {
         ICommand var7 = this.commandMap.get(var6);
         if (var7 == null || !var7.getCommandName().equals(var6)) {
            this.commandMap.put(var6, var1);
         }
      }

      return var1;
   }

   private static String[] dropFirstString(String[] var0) {
      String[] var1 = new String[var0.length - 1];
      System.arraycopy(var0, 1, var1, 0, var0.length - 1);
      return var1;
   }

   @Override
   public List<String> getTabCompletionOptions(ICommandSender var1, String var2, BlockPos var3) {
      String[] var7 = var2.split(" ", -1);
      String var8 = var7[0];
      if (var7.length == 1) {
         ArrayList var12 = Lists.newArrayList();

         for (Entry var11 : this.commandMap.entrySet()) {
            if (CommandBase.doesStringStartWith(var8, (String)var11.getKey()) && ((ICommand)var11.getValue()).canCommandSenderUseCommand(var1)) {
               var12.add(var11.getKey());
            }
         }

         return var12;
      } else {
         if (var7.length > 1) {
            ICommand var9 = this.commandMap.get(var8);
            if (var9 != null && var9.canCommandSenderUseCommand(var1)) {
               return var9.addTabCompletionOptions(var1, dropFirstString(var7), var3);
            }
         }

         return null;
      }
   }

   @Override
   public List<ICommand> getPossibleCommands(ICommandSender var1) {
      ArrayList var5 = Lists.newArrayList();

      for (ICommand var7 : this.commandSet) {
         if (var7.canCommandSenderUseCommand(var1)) {
            var5.add(var7);
         }
      }

      return var5;
   }

   @Override
   public Map<String, ICommand> getCommands() {
      return this.commandMap;
   }

   private int getUsernameIndex(ICommand var1, String[] var2) {
      if (var1 == null) {
         return -1;
      }

      for (int var6 = 0; var6 < var2.length; var6++) {
         if (var1.isUsernameIndex(var2, var6) && PlayerSelector.matchesMultiplePlayers(var2[var6])) {
            return var6;
         }
      }

      return -1;
   }
}
