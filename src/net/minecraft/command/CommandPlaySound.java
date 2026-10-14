package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CommandPlaySound extends CommandBase {
   @Override
   public String getCommandName() {
      return "playsound";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.playsound.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException(this.getCommandUsage(sender));
      }

      int i = 0;
      String string = args[i++];
      EntityPlayerMP entityPlayerMP = getPlayer(sender, args[i++]);
      Vec3 vec3 = sender.getPositionVector();
      double d = vec3.xCoord;
      if (args.length > i) {
         d = parseDouble(d, args[i++], true);
      }

      double e = vec3.yCoord;
      if (args.length > i) {
         e = parseDouble(e, args[i++], 0, 0, false);
      }

      double d2 = vec3.zCoord;
      if (args.length > i) {
         d2 = parseDouble(d2, args[i++], true);
      }

      double d3 = 1.0;
      if (args.length > i) {
         d3 = parseDouble(args[i++], 0.0, Float.MAX_VALUE);
      }

      double d4 = 1.0;
      if (args.length > i) {
         d4 = parseDouble(args[i++], 0.0, 2.0);
      }

      double d5 = 0.0;
      if (args.length > i) {
         d5 = parseDouble(args[i], 0.0, 1.0);
      }

      double d6 = d3 > 1.0 ? d3 * 16.0 : 16.0;
      double d7 = entityPlayerMP.getDistance(d, e, d2);
      if (d7 > d6) {
         if (d5 <= 0.0) {
            throw new CommandException("commands.playsound.playerTooFar", entityPlayerMP.getName());
         }

         double d8 = d - entityPlayerMP.posX;
         double d9 = e - entityPlayerMP.posY;
         double d10 = d2 - entityPlayerMP.posZ;
         double d11 = Math.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
         if (d11 > 0.0) {
            d = entityPlayerMP.posX + d8 / d11 * 2.0;
            e = entityPlayerMP.posY + d9 / d11 * 2.0;
            d2 = entityPlayerMP.posZ + d10 / d11 * 2.0;
         }

         d3 = d5;
      }

      entityPlayerMP.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(string, d, e, d2, (float)d3, (float)d4));
      notifyOperators(sender, this, "commands.playsound.success", string, entityPlayerMP.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 2
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length > 2 && args.length <= 5 ? func_175771_a(args, 2, pos) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }
}
