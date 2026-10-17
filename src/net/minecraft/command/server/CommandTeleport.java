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
   @Override
   public String getCommandName() {
      return "tp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.tp.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.tp.usage");
      }

      byte b = 0;
      Entity entity;
      if (args.length != 2 && args.length != 4 && args.length != 6) {
         entity = getCommandSenderAsPlayer(sender);
      } else {
         entity = getEntity(sender, args[0]);
         b = 1;
      }

      if (args.length != 1 && args.length != 2) {
         if (args.length < b + 3) {
            throw new WrongUsageException("commands.tp.usage");
         }

         if (entity.worldObj != null) {
            int i = b + 1;
            CommandBase.CoordinateArg coordinateArg = parseCoordinate(entity.posX, args[b], true);
            CommandBase.CoordinateArg coordinateArg2 = parseCoordinate(entity.posY, args[i++], 0, 0, false);
            CommandBase.CoordinateArg coordinateArg3 = parseCoordinate(entity.posZ, args[i++], true);
            CommandBase.CoordinateArg coordinateArg4 = parseCoordinate(entity.rotationYaw, args.length > i ? args[i++] : "~", false);
            CommandBase.CoordinateArg coordinateArg5 = parseCoordinate(entity.rotationPitch, args.length > i ? args[i] : "~", false);
            if (entity instanceof EntityPlayerMP) {
               EnumSet enumSet = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);
               if (coordinateArg.func_179630_c()) {
                  enumSet.add(S08PacketPlayerPosLook.EnumFlags.X);
               }

               if (coordinateArg2.func_179630_c()) {
                  enumSet.add(S08PacketPlayerPosLook.EnumFlags.Y);
               }

               if (coordinateArg3.func_179630_c()) {
                  enumSet.add(S08PacketPlayerPosLook.EnumFlags.Z);
               }

               if (coordinateArg5.func_179630_c()) {
                  enumSet.add(S08PacketPlayerPosLook.EnumFlags.X_ROT);
               }

               if (coordinateArg4.func_179630_c()) {
                  enumSet.add(S08PacketPlayerPosLook.EnumFlags.Y_ROT);
               }

               float f = (float)coordinateArg4.getAmount();
               if (!coordinateArg4.func_179630_c()) {
                  f = MathHelper.wrapAngleTo180_float(f);
               }

               float g = (float)coordinateArg5.getAmount();
               if (!coordinateArg5.func_179630_c()) {
                  g = MathHelper.wrapAngleTo180_float(g);
               }

               if (g > 90.0F || g < -90.0F) {
                  g = MathHelper.wrapAngleTo180_float(180.0F - g);
                  f = MathHelper.wrapAngleTo180_float(f + 180.0F);
               }

               entity.mountEntity((Entity)null);
               ((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(coordinateArg.getAmount(), coordinateArg2.getAmount(), coordinateArg3.getAmount(), f, g, enumSet);
               entity.setRotationYawHead(f);
            } else {
               float h = (float)MathHelper.wrapAngleTo180_double(coordinateArg4.getResult());
               float f2 = (float)MathHelper.wrapAngleTo180_double(coordinateArg5.getResult());
               if (f2 > 90.0F || f2 < -90.0F) {
                  f2 = MathHelper.wrapAngleTo180_float(180.0F - f2);
                  h = MathHelper.wrapAngleTo180_float(h + 180.0F);
               }

               entity.setLocationAndAngles(coordinateArg.getResult(), coordinateArg2.getResult(), coordinateArg3.getResult(), h, f2);
               entity.setRotationYawHead(h);
            }

            notifyOperators(sender, this, "commands.tp.success.coordinates", entity.getName(), coordinateArg.getResult(), coordinateArg2.getResult(), coordinateArg3.getResult());
         }
      } else {
         Entity entity2 = getEntity(sender, args[args.length - 1]);
         if (entity2.worldObj != entity.worldObj) {
            throw new CommandException("commands.tp.notSameDimension");
         }

         entity.mountEntity((Entity)null);
         if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
         } else {
            entity.setLocationAndAngles(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
         }

         notifyOperators(sender, this, "commands.tp.success", entity.getName(), entity2.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
