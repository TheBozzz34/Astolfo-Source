package net.minecraft.command.server;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CommandTeleport extends CommandBase {
   private static final long b = 31057643781133L;
   private static final long j = 1341553564849274886L;

   @Override
   public String getCommandName() {
      return "tp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.tp.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.tp.usage");
      }

      byte var6 = 0;
      Entity var7;
      if (var2.length != 2 && var2.length != 4 && var2.length != (int)j) {
         var7 = getCommandSenderAsPlayer(var1);
      } else {
         var7 = getEntity(var1, var2[0]);
         var6 = 1;
      }

      if (var2.length != 1 && var2.length != 2) {
         if (var2.length < var6 + 3) {
            throw new WrongUsageException("commands.tp.usage");
         }

         if (var7.worldObj != null) {
            int var17 = var6 + 1;
            CommandBase.CoordinateArg var9 = parseCoordinate(var7.posX, var2[var6], true);
            CommandBase.CoordinateArg var10 = parseCoordinate(var7.posY, var2[var17++], 0, 0, false);
            CommandBase.CoordinateArg var11 = parseCoordinate(var7.posZ, var2[var17++], true);
            CommandBase.CoordinateArg var12 = parseCoordinate(var7.rotationYaw, var2.length > var17 ? var2[var17++] : "~", false);
            CommandBase.CoordinateArg var13 = parseCoordinate(var7.rotationPitch, var2.length > var17 ? var2[var17] : "~", false);
            if (var7 instanceof EntityPlayerMP) {
               EnumSet var20 = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);
               if (var9.func_179630_c()) {
                  var20.add(S08PacketPlayerPosLook.EnumFlags.X);
               }

               if (var10.func_179630_c()) {
                  var20.add(S08PacketPlayerPosLook.EnumFlags.Y);
               }

               if (var11.func_179630_c()) {
                  var20.add(S08PacketPlayerPosLook.EnumFlags.Z);
               }

               if (var13.func_179630_c()) {
                  var20.add(S08PacketPlayerPosLook.EnumFlags.X_ROT);
               }

               if (var12.func_179630_c()) {
                  var20.add(S08PacketPlayerPosLook.EnumFlags.Y_ROT);
               }

               float var21 = (float)var12.v();
               if (!var12.func_179630_c()) {
                  var21 = MathHelper.wrapAngleTo180_float(var21);
               }

               float var16 = (float)var13.v();
               if (!var13.func_179630_c()) {
                  var16 = MathHelper.wrapAngleTo180_float(var16);
               }

               if (var16 > 90.0F || var16 < -90.0F) {
                  var16 = MathHelper.wrapAngleTo180_float(180.0F - var16);
                  var21 = MathHelper.wrapAngleTo180_float(var21 + 180.0F);
               }

               var7.mountEntity((Entity)null);
               ((EntityPlayerMP)var7).playerNetServerHandler.setPlayerLocation(var9.v(), var10.v(), var11.v(), var21, var16, var20);
               var7.setRotationYawHead(var21);
            } else {
               float var14 = (float)MathHelper.wrapAngleTo180_double(var12.J());
               float var15 = (float)MathHelper.wrapAngleTo180_double(var13.J());
               if (var15 > 90.0F || var15 < -90.0F) {
                  var15 = MathHelper.wrapAngleTo180_float(180.0F - var15);
                  var14 = MathHelper.wrapAngleTo180_float(var14 + 180.0F);
               }

               var7.setLocationAndAngles(var9.J(), var10.J(), var11.J(), var14, var15);
               var7.setRotationYawHead(var14);
            }

            notifyOperators(var1, this, "commands.tp.success.coordinates", var7.getName(), var9.J(), var10.J(), var11.J());
         }
      } else {
         Entity var8 = getEntity(var1, var2[var2.length - 1]);
         if (var8.worldObj != var7.worldObj) {
            throw new CommandException("commands.tp.notSameDimension");
         }

         var7.mountEntity((Entity)null);
         if (var7 instanceof EntityPlayerMP) {
            ((EntityPlayerMP)var7).playerNetServerHandler.setPlayerLocation(var8.posX, var8.posY, var8.posZ, var8.rotationYaw, var8.rotationPitch);
         } else {
            var7.setLocationAndAngles(var8.posX, var8.posY, var8.posZ, var8.rotationYaw, var8.rotationPitch);
         }

         notifyOperators(var1, this, "commands.tp.success", var7.getName(), var8.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length != 1 && var2.length != 2 ? null : getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
