package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CommandPlaySound extends CommandBase {
   private static final long b = 31381084915842L;

   @Override
   public String getCommandName() {
      return "playsound";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.playsound.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException(this.getCommandUsage(var1));
      }

      int var6 = 0;
      String var7 = var2[var6++];
      EntityPlayerMP var8 = getPlayer(var1, var2[var6++]);
      Vec3 var9 = var1.getPositionVector();
      double var10 = var9.xCoord;
      if (var2.length > var6) {
         var10 = parseDouble(var10, var2[var6++], true);
      }

      double var12 = var9.yCoord;
      if (var2.length > var6) {
         var12 = parseDouble(var12, var2[var6++], 0, 0, false);
      }

      double var14 = var9.zCoord;
      if (var2.length > var6) {
         var14 = parseDouble(var14, var2[var6++], true);
      }

      double var16 = 1.0;
      if (var2.length > var6) {
         var16 = parseDouble(var2[var6++], 0.0, Float.MAX_VALUE);
      }

      double var18 = 1.0;
      if (var2.length > var6) {
         var18 = parseDouble(var2[var6++], 0.0, 2.0);
      }

      double var20 = 0.0;
      if (var2.length > var6) {
         var20 = parseDouble(var2[var6], 0.0, 1.0);
      }

      double var22 = var16 > 1.0 ? var16 * 16.0 : 16.0;
      double var24 = var8.getDistance(var10, var12, var14);
      if (var24 > var22) {
         if (var20 <= 0.0) {
            throw new CommandException("commands.playsound.playerTooFar", var8.getName());
         }

         double var26 = var10 - var8.posX;
         double var28 = var12 - var8.posY;
         double var30 = var14 - var8.posZ;
         double var32 = Math.sqrt(var26 * var26 + var28 * var28 + var30 * var30);
         if (var32 > 0.0) {
            var10 = var8.posX + var26 / var32 * 2.0;
            var12 = var8.posY + var28 / var32 * 2.0;
            var14 = var8.posZ + var30 / var32 * 2.0;
         }

         var16 = var20;
      }

      var8.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(var7, var10, var12, var14, (float)var16, (float)var18));
      notifyOperators(var1, this, "commands.playsound.success", var7, var8.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 2
         ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames())
         : (var2.length > 2 && var2.length <= 5 ? func_175771_a(var2, 2, var3) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 1;
   }
}
