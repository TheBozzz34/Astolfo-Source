package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   private static final long b = 75902399097173L;

   @Override
   public String getCommandName() {
      return "gamerule";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.gamerule.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      GameRules var6 = this.getGameRules();
      String var7 = var2.length > 0 ? var2[0] : "";
      String var8 = var2.length > 1 ? buildString(var2, 1) : "";
      switch (var2.length) {
         case 0:
            var1.addChatMessage(new ChatComponentText(joinNiceString(var6.getRules())));
            break;
         case 1:
            if (!var6.hasRule(var7)) {
               throw new CommandException("commands.gamerule.norule", var7);
            }

            String var9 = var6.getString(var7);
            var1.addChatMessage(new ChatComponentText(var7).appendText(" = ").appendText(var9));
            var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var6.getInt(var7));
            break;
         default:
            if (var6.areSameType(var7, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(var8) && !"false".equals(var8)) {
               throw new CommandException("commands.generic.boolean.invalid", var8);
            }

            var6.setOrCreateGameRule(var7, var8);
            func_175773_a(var6, var7);
            notifyOperators(var1, this, "commands.gamerule.success");
      }
   }

   public static void func_175773_a(GameRules var0, String var1) {
      if ("reducedDebugInfo".equals(var1)) {
         byte var5 = (byte)(var0.getBoolean(var1) ? 22 : 23);

         for (EntityPlayerMP var7 : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            var7.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(var7, var5));
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return getListOfStringsMatchingLastWord(var2, this.getGameRules().getRules());
      }

      if (var2.length == 2) {
         GameRules var7 = this.getGameRules();
         if (var7.areSameType(var2[0], GameRules.ValueType.BOOLEAN_VALUE)) {
            return getListOfStringsMatchingLastWord(var2, "true", "false");
         }
      }

      return null;
   }

   private GameRules getGameRules() {
      return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
   }
}
