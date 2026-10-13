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
   private static final long b = 61160041745850L;

   @Override
   public String getCommandName() {
      return "help";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.help.usage";
   }

   @Override
   public List<String> getCommandAliases() {
      return Arrays.asList("?");
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      List var6 = this.getSortedPossibleCommands(var1);
      int var8 = (var6.size() - 1) / 7;
      int var9 = 0;

      try {
         var9 = var2.length == 0 ? 0 : parseInt(var2[0], 1, var8 + 1) - 1;
      } catch (NumberInvalidException var15) {
         Map var11 = this.getCommands();
         ICommand var12 = (ICommand)var11.get(var2[0]);
         if (var12 != null) {
            throw new WrongUsageException(var12.getCommandUsage(var1));
         }

         if (MathHelper.parseIntWithDefault(var2[0], -1) != -1) {
            throw var15;
         }

         throw new CommandNotFoundException();
      }

      int var10 = Math.min((var9 + 1) * 7, var6.size());
      ChatComponentTranslation var17 = new ChatComponentTranslation("commands.help.header", var9 + 1, var8 + 1);
      var17.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      var1.addChatMessage(var17);

      for (int var18 = var9 * 7; var18 < var10; var18++) {
         ICommand var13 = (ICommand)var6.get(var18);
         ChatComponentTranslation var14 = new ChatComponentTranslation(var13.getCommandUsage(var1));
         var14.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + var13.getCommandName() + " "));
         var1.addChatMessage(var14);
      }

      if (var9 == 0 && var1 instanceof EntityPlayer) {
         ChatComponentTranslation var19 = new ChatComponentTranslation("commands.help.footer");
         var19.getChatStyle().setColor(EnumChatFormatting.GREEN);
         var1.addChatMessage(var19);
      }
   }

   public List<ICommand> getSortedPossibleCommands(ICommandSender var1) {
      List var2 = MinecraftServer.getServer().getCommandManager().getPossibleCommands(var1);
      Collections.sort(var2);
      return var2;
   }

   public Map<String, ICommand> getCommands() {
      return MinecraftServer.getServer().getCommandManager().getCommands();
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         Set var6 = this.getCommands().keySet();
         return getListOfStringsMatchingLastWord(var2, (java.lang.String[])(var6.toArray(new String[var6.size()])));
      } else {
         return null;
      }
   }
}
