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

   @Override
   public String getCommandName() {
      return "title";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.title.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.title.usage");
      }

      if (args.length < 3) {
         if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.title");
         }

         if ("times".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.times");
         }
      }

      EntityPlayerMP entityPlayerMP = getPlayer(sender, args[0]);
      S45PacketTitle.Type type = S45PacketTitle.Type.byName(args[1]);
      if (type == S45PacketTitle.Type.CLEAR || type == S45PacketTitle.Type.RESET) {
         if (args.length != 2) {
            throw new WrongUsageException("commands.title.usage");
         }

         S45PacketTitle s45PacketTitle = new S45PacketTitle(type, (IChatComponent)null);
         entityPlayerMP.playerNetServerHandler.sendPacket(s45PacketTitle);
         notifyOperators(sender, this, "commands.title.success");
      } else if (type == S45PacketTitle.Type.TIMES) {
         if (args.length != 5) {
            throw new WrongUsageException("commands.title.usage");
         }

         int i = parseInt(args[2]);
         int j = parseInt(args[3]);
         int k = parseInt(args[4]);
         S45PacketTitle s45PacketTitle2 = new S45PacketTitle(i, j, k);
         entityPlayerMP.playerNetServerHandler.sendPacket(s45PacketTitle2);
         notifyOperators(sender, this, "commands.title.success");
      } else {
         if (args.length < 3) {
            throw new WrongUsageException("commands.title.usage");
         }

         String string = buildString(args, 2);

         IChatComponent iChatComponent;
         try {
            iChatComponent = IChatComponent.Serializer.jsonToComponent(string);
         } catch (JsonParseException jsonParseException) {
            Throwable throwable = ExceptionUtils.getRootCause(jsonParseException);
            throw new SyntaxErrorException("commands.tellraw.jsonException", throwable == null ? "" : throwable.getMessage());
         }

         S45PacketTitle s45PacketTitle3 = new S45PacketTitle(type, ChatComponentProcessor.processComponent(sender, iChatComponent, entityPlayerMP));
         entityPlayerMP.playerNetServerHandler.sendPacket(s45PacketTitle3);
         notifyOperators(sender, this, "commands.title.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, S45PacketTitle.Type.getNames()) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
