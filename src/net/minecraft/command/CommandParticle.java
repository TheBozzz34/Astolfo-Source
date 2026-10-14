package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase {
   @Override
   public String getCommandName() {
      return "particle";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.particle.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 8) {
         throw new WrongUsageException("commands.particle.usage");
      }

      boolean bl = false;
      EnumParticleTypes enumParticleTypes = null;

      for (EnumParticleTypes enumParticleTypes2 : EnumParticleTypes.values()) {
         if (enumParticleTypes2.hasArguments()) {
            if (args[0].startsWith(enumParticleTypes2.getParticleName())) {
               bl = true;
               enumParticleTypes = enumParticleTypes2;
               break;
            }
         } else if (args[0].equals(enumParticleTypes2.getParticleName())) {
            bl = true;
            enumParticleTypes = enumParticleTypes2;
            break;
         }
      }

      if (!bl) {
         throw new CommandException("commands.particle.notFound", args[0]);
      }

      String string = args[0];
      Vec3 vec3 = sender.getPositionVector();
      double d = (float)parseDouble(vec3.xCoord, args[1], true);
      double e = (float)parseDouble(vec3.yCoord, args[2], true);
      double d2 = (float)parseDouble(vec3.zCoord, args[3], true);
      double d3 = (float)parseDouble(args[4]);
      double d4 = (float)parseDouble(args[5]);
      double d5 = (float)parseDouble(args[6]);
      double d6 = (float)parseDouble(args[7]);
      int i = 0;
      if (args.length > 8) {
         i = parseInt(args[8], 0);
      }

      boolean bl2 = false;
      if (args.length > 9 && "force".equals(args[9])) {
         bl2 = true;
      }

      World world = sender.getEntityWorld();
      if (world instanceof WorldServer) {
         WorldServer worldServer = (WorldServer)world;
         int[] ints = new int[enumParticleTypes.getArgumentCount()];
         if (enumParticleTypes.hasArguments()) {
            String[] strings = args[0].split("_", 3);

            for (int j = 1; j < strings.length; j++) {
               try {
                  ints[j - 1] = Integer.parseInt(strings[j]);
               } catch (NumberFormatException numberFormatException) {
                  throw new CommandException("commands.particle.notFound", args[0]);
               }
            }
         }

         worldServer.spawnParticle(enumParticleTypes, bl2, d, e, d2, i, d3, d4, d5, d6, ints);
         notifyOperators(sender, this, "commands.particle.success", string, Math.max(i, 1));
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames())
         : (
            args.length > 1 && args.length <= 4
               ? func_175771_a(args, 1, pos)
               : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "normal", "force") : null)
         );
   }
}
