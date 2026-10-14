package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   @Override
   public String getCommandName() {
      return "gamerule";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.gamerule.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      GameRules gameRules = this.getGameRules();
      String string = args.length > 0 ? args[0] : "";
      String string2 = args.length > 1 ? buildString(args, 1) : "";
      switch (args.length) {
         case 0:
            sender.addChatMessage(new ChatComponentText(joinNiceString(gameRules.getRules())));
            break;
         case 1:
            if (!gameRules.hasRule(string)) {
               throw new CommandException("commands.gamerule.norule", string);
            }

            String string3 = gameRules.getString(string);
            sender.addChatMessage(new ChatComponentText(string).appendText(" = ").appendText(string3));
            sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameRules.getInt(string));
            break;
         default:
            if (gameRules.areSameType(string, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(string2) && !"false".equals(string2)) {
               throw new CommandException("commands.generic.boolean.invalid", string2);
            }

            gameRules.setOrCreateGameRule(string, string2);
            func_175773_a(gameRules, string);
            notifyOperators(sender, this, "commands.gamerule.success");
      }
   }

   public static void func_175773_a(GameRules rules, String string) {
      if ("reducedDebugInfo".equals(string)) {
         byte b = (byte)(rules.getBoolean(string) ? 22 : 23);

         for (EntityPlayerMP entityPlayerMP : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            entityPlayerMP.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(entityPlayerMP, b));
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
      }

      if (args.length == 2) {
         GameRules gameRules = this.getGameRules();
         if (gameRules.areSameType(args[0], GameRules.ValueType.BOOLEAN_VALUE)) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
         }
      }

      return null;
   }

   private GameRules getGameRules() {
      return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
   }
}
