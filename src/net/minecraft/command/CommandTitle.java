package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long b = 15701526045409L;

   @Override
   public String getCommandName() {
      return "title";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.title.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.title.usage");
      }

      if (var2.length < 3) {
         if ("title".equals(var2[1]) || "subtitle".equals(var2[1])) {
            throw new WrongUsageException("commands.title.usage.title");
         }

         if ("times".equals(var2[1])) {
            throw new WrongUsageException("commands.title.usage.times");
         }
      }

      EntityPlayerMP var6 = getPlayer(var1, var2[0]);
      S45PacketTitle.Type var7 = S45PacketTitle.Type.byName(var2[1]);
      if (var7 == S45PacketTitle.Type.CLEAR || var7 == S45PacketTitle.Type.RESET) {
         if (var2.length != 2) {
            throw new WrongUsageException("commands.title.usage");
         }

         S45PacketTitle var14 = new S45PacketTitle(var7, (IChatComponent)null);
         var6.playerNetServerHandler.sendPacket(var14);
         notifyOperators(var1, this, "commands.title.success");
      } else if (var7 == S45PacketTitle.Type.TIMES) {
         if (var2.length != 5) {
            throw new WrongUsageException("commands.title.usage");
         }

         int var8 = parseInt(var2[2]);
         int var9 = parseInt(var2[3]);
         int var10 = parseInt(var2[4]);
         S45PacketTitle var11 = new S45PacketTitle(var8, var9, var10);
         var6.playerNetServerHandler.sendPacket(var11);
         notifyOperators(var1, this, "commands.title.success");
      } else {
         if (var2.length < 3) {
            throw new WrongUsageException("commands.title.usage");
         }

         String var13 = buildString(var2, 2);

         IChatComponent var15;
         try {
            var15 = IChatComponent.Serializer.jsonToComponent(var13);
         } catch (JsonParseException var12) {
            Throwable var17 = ExceptionUtils.getRootCause(var12);
            throw new SyntaxErrorException("commands.tellraw.jsonException", var17 == null ? "" : var17.getMessage());
         }

         S45PacketTitle var16 = new S45PacketTitle(var7, ChatComponentProcessor.processComponent(var1, var15, var6));
         var6.playerNetServerHandler.sendPacket(var16);
         notifyOperators(var1, this, "commands.title.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames())
         : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, S45PacketTitle.Type.getNames()) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
