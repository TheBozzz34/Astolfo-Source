package net.minecraft.command.server;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase {
   private static final long b = 11199117283487L;

   @Override
   public String getCommandName() {
      return "tellraw";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.tellraw.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.tellraw.usage");
      }

      EntityPlayerMP var5 = getPlayer(var1, var2[0]);
      String var6 = buildString(var2, 1);

      try {
         IChatComponent var7 = IChatComponent.Serializer.jsonToComponent(var6);
         var5.addChatMessage(ChatComponentProcessor.processComponent(var1, var7, var5));
      } catch (JsonParseException var9) {
         Throwable var8 = ExceptionUtils.getRootCause(var9);
         throw new SyntaxErrorException("commands.tellraw.jsonException", var8 == null ? "" : var8.getMessage());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
