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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.worldborder.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.worldborder.usage");
      }

      WorldBorder worldBorder = this.getWorldBorder();
      if (args[0].equals("set")) {
         if (args.length != 2 && args.length != 3) {
            throw new WrongUsageException("commands.worldborder.set.usage");
         }

         double d = worldBorder.getTargetSize();
         double e = parseDouble(args[1], 1.0, 6.0E7);
         long l = args.length > 2 ? parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L;
         if (l > 0L) {
            worldBorder.setTransition(d, e, l);
            if (d > e) {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", e),
                  String.format("%.1f", d),
                  Long.toString(l / 1000L)
               );
            } else {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", e),
                  String.format("%.1f", d),
                  Long.toString(l / 1000L)
               );
            }
         } else {
            worldBorder.setTransition(e);
            notifyOperators(sender, this, "commands.worldborder.set.success", String.format("%.1f", e), String.format("%.1f", d));
         }
      } else if (args[0].equals("add")) {
         if (args.length != 2 && args.length != 3) {
            throw new WrongUsageException("commands.worldborder.add.usage");
         }

         double d2 = worldBorder.getDiameter();
         double d3 = d2 + parseDouble(args[1], -d2, 6.0E7 - d2);
         long m = worldBorder.getTimeUntilTarget() + (args.length > 2 ? parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L);
         if (m > 0L) {
            worldBorder.setTransition(d2, d3, m);
            if (d2 > d3) {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", d3),
                  String.format("%.1f", d2),
                  Long.toString(m / 1000L)
               );
            } else {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", d3),
                  String.format("%.1f", d2),
                  Long.toString(m / 1000L)
               );
            }
         } else {
            worldBorder.setTransition(d3);
            notifyOperators(sender, this, "commands.worldborder.set.success", String.format("%.1f", d3), String.format("%.1f", d2));
         }
      } else if (args[0].equals("center")) {
         if (args.length != 3) {
            throw new WrongUsageException("commands.worldborder.center.usage");
         }

         BlockPos blockPos = sender.getPosition();
         double d4 = parseDouble(blockPos.getX() + 0.5, args[1], true);
         double d5 = parseDouble(blockPos.getZ() + 0.5, args[2], true);
         worldBorder.setCenter(d4, d5);
         notifyOperators(sender, this, "commands.worldborder.center.success", d4, d5);
      } else if (args[0].equals("damage")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.worldborder.damage.usage");
         }

         if (args[1].equals("buffer")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.buffer.usage");
            }

            double d6 = parseDouble(args[2], 0.0);
            double d7 = worldBorder.getDamageBuffer();
            worldBorder.setDamageBuffer(d6);
            notifyOperators(sender, this, "commands.worldborder.damage.buffer.success", String.format("%.1f", d6), String.format("%.1f", d7));
         } else if (args[1].equals("amount")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.amount.usage");
            }

            double d8 = parseDouble(args[2], 0.0);
            double d9 = worldBorder.getDamageAmount();
            worldBorder.setDamageAmount(d8);
            notifyOperators(sender, this, "commands.worldborder.damage.amount.success", String.format("%.2f", d8), String.format("%.2f", d9));
         }
      } else if (args[0].equals("warning")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.worldborder.warning.usage");
         }

         int i = parseInt(args[2], 0);
         if (args[1].equals("time")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.time.usage");
            }

            int j = worldBorder.getWarningTime();
            worldBorder.setWarningTime(i);
            notifyOperators(sender, this, "commands.worldborder.warning.time.success", i, j);
         } else if (args[1].equals("distance")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.distance.usage");
            }

            int k = worldBorder.getWarningDistance();
            worldBorder.setWarningDistance(i);
            notifyOperators(sender, this, "commands.worldborder.warning.distance.success", i, k);
         }
      } else {
         if (!args[0].equals("get")) {
            throw new WrongUsageException("commands.worldborder.usage");
         }

         double d10 = worldBorder.getDiameter();
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, MathHelper.floor_double(d10 + 0.5));
         sender.addChatMessage(new ChatComponentTranslation("commands.worldborder.get.success", String.format("%.0f", d10)));
      }
   }

   public WorldBorder getWorldBorder() {
      return MinecraftServer.getServer().worldServers[0].getWorldBorder();
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      List list;
      if (args.length == 1) {
         String[] strings = new String[(int)j];
         strings[0] = "set";
         strings[1] = "center";
         strings[2] = "damage";
         strings[3] = "warning";
         strings[4] = "add";
         strings[5] = "get";
         list = getListOfStringsMatchingLastWord(args, strings);
      } else {
         list = args.length == 2 && args[0].equals("damage")
            ? getListOfStringsMatchingLastWord(args, "buffer", "amount")
            : (
               args.length >= 2 && args.length <= 3 && args[0].equals("center")
                  ? func_181043_b(args, 1, pos)
                  : (args.length == 2 && args[0].equals("warning") ? getListOfStringsMatchingLastWord(args, "time", "distance") : null)
            );
      }

      return list;
   }
}
