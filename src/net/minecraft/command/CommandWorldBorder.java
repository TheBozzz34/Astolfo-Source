package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class CommandWorldBorder extends CommandBase {
   private static final long b = 9339809621220L;
   private static final long j = -542043311049801722L;

   @Override
   public String getCommandName() {
      return "worldborder";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.worldborder.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.worldborder.usage");
      }

      WorldBorder var6 = this.getWorldBorder();
      if (var2[0].equals("set")) {
         if (var2.length != 2 && var2.length != 3) {
            throw new WrongUsageException("commands.worldborder.set.usage");
         }

         double var7 = var6.getTargetSize();
         double var9 = parseDouble(var2[1], 1.0, 6.0E7);
         long var11 = var2.length > 2 ? parseLong(var2[2], 0L, 9223372036854775L) * 1000L : 0L;
         if (var11 > 0L) {
            var6.setTransition(var7, var9, var11);
            if (var7 > var9) {
               notifyOperators(
                  var1,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", var9),
                  String.format("%.1f", var7),
                  Long.toString(var11 / 1000L)
               );
            } else {
               notifyOperators(
                  var1,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", var9),
                  String.format("%.1f", var7),
                  Long.toString(var11 / 1000L)
               );
            }
         } else {
            var6.setTransition(var9);
            notifyOperators(var1, this, "commands.worldborder.set.success", String.format("%.1f", var9), String.format("%.1f", var7));
         }
      } else if (var2[0].equals("add")) {
         if (var2.length != 2 && var2.length != 3) {
            throw new WrongUsageException("commands.worldborder.add.usage");
         }

         double var13 = var6.getDiameter();
         double var21 = var13 + parseDouble(var2[1], -var13, 6.0E7 - var13);
         long var24 = var6.getTimeUntilTarget() + (var2.length > 2 ? parseLong(var2[2], 0L, 9223372036854775L) * 1000L : 0L);
         if (var24 > 0L) {
            var6.setTransition(var13, var21, var24);
            if (var13 > var21) {
               notifyOperators(
                  var1,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", var21),
                  String.format("%.1f", var13),
                  Long.toString(var24 / 1000L)
               );
            } else {
               notifyOperators(
                  var1,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", var21),
                  String.format("%.1f", var13),
                  Long.toString(var24 / 1000L)
               );
            }
         } else {
            var6.setTransition(var21);
            notifyOperators(var1, this, "commands.worldborder.set.success", String.format("%.1f", var21), String.format("%.1f", var13));
         }
      } else if (var2[0].equals("center")) {
         if (var2.length != 3) {
            throw new WrongUsageException("commands.worldborder.center.usage");
         }

         BlockPos var14 = var1.getPosition();
         double var8 = parseDouble(var14.getX() + 0.5, var2[1], true);
         double var10 = parseDouble(var14.getZ() + 0.5, var2[2], true);
         var6.setCenter(var8, var10);
         notifyOperators(var1, this, "commands.worldborder.center.success", var8, var10);
      } else if (var2[0].equals("damage")) {
         if (var2.length < 2) {
            throw new WrongUsageException("commands.worldborder.damage.usage");
         }

         if (var2[1].equals("buffer")) {
            if (var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.buffer.usage");
            }

            double var15 = parseDouble(var2[2], 0.0);
            double var22 = var6.getDamageBuffer();
            var6.setDamageBuffer(var15);
            notifyOperators(var1, this, "commands.worldborder.damage.buffer.success", String.format("%.1f", var15), String.format("%.1f", var22));
         } else if (var2[1].equals("amount")) {
            if (var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.amount.usage");
            }

            double var16 = parseDouble(var2[2], 0.0);
            double var23 = var6.getDamageAmount();
            var6.setDamageAmount(var16);
            notifyOperators(var1, this, "commands.worldborder.damage.amount.success", String.format("%.2f", var16), String.format("%.2f", var23));
         }
      } else if (var2[0].equals("warning")) {
         if (var2.length < 2) {
            throw new WrongUsageException("commands.worldborder.warning.usage");
         }

         int var17 = parseInt(var2[2], 0);
         if (var2[1].equals("time")) {
            if (var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.time.usage");
            }

            int var19 = var6.getWarningTime();
            var6.setWarningTime(var17);
            notifyOperators(var1, this, "commands.worldborder.warning.time.success", var17, var19);
         } else if (var2[1].equals("distance")) {
            if (var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.distance.usage");
            }

            int var20 = var6.getWarningDistance();
            var6.setWarningDistance(var17);
            notifyOperators(var1, this, "commands.worldborder.warning.distance.success", var17, var20);
         }
      } else {
         if (!var2[0].equals("get")) {
            throw new WrongUsageException("commands.worldborder.usage");
         }

         double var18 = var6.getDiameter();
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, MathHelper.floor_double(var18 + 0.5));
         var1.addChatMessage(new ChatComponentTranslation("commands.worldborder.get.success", String.format("%.0f", var18)));
      }
   }

   public WorldBorder getWorldBorder() {
      return MinecraftServer.getServer().worldServers[0].getWorldBorder();
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      List var10000;
      if (var2.length == 1) {
         String[] var10001 = new String[(int)j];
         var10001[0] = "set";
         var10001[1] = "center";
         var10001[2] = "damage";
         var10001[3] = "warning";
         var10001[4] = "add";
         var10001[5] = "get";
         var10000 = getListOfStringsMatchingLastWord(var2, var10001);
      } else {
         var10000 = var2.length == 2 && var2[0].equals("damage")
            ? getListOfStringsMatchingLastWord(var2, "buffer", "amount")
            : (
               var2.length >= 2 && var2.length <= 3 && var2[0].equals("center")
                  ? func_181043_b(var2, 1, var3)
                  : (var2.length == 2 && var2[0].equals("warning") ? getListOfStringsMatchingLastWord(var2, "time", "distance") : null)
            );
      }

      return var10000;
   }
}
