package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase {
   private static final long b = 110570871182474L;

   @Override
   public String getCommandName() {
      return "particle";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.particle.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 8) {
         throw new WrongUsageException("commands.particle.usage");
      }

      boolean var6 = false;
      EnumParticleTypes var7 = null;

      for (EnumParticleTypes var11 : EnumParticleTypes.values()) {
         if (var11.hasArguments()) {
            if (var2[0].startsWith(var11.getParticleName())) {
               var6 = true;
               var7 = var11;
               break;
            }
         } else if (var2[0].equals(var11.getParticleName())) {
            var6 = true;
            var7 = var11;
            break;
         }
      }

      if (!var6) {
         throw new CommandException("commands.particle.notFound", var2[0]);
      }

      String var33 = var2[0];
      Vec3 var34 = var1.getPositionVector();
      double var35 = (float)parseDouble(var34.xCoord, var2[1], true);
      double var12 = (float)parseDouble(var34.yCoord, var2[2], true);
      double var14 = (float)parseDouble(var34.zCoord, var2[3], true);
      double var16 = (float)parseDouble(var2[4]);
      double var18 = (float)parseDouble(var2[5]);
      double var20 = (float)parseDouble(var2[6]);
      double var22 = (float)parseDouble(var2[7]);
      int var24 = 0;
      if (var2.length > 8) {
         var24 = parseInt(var2[8], 0);
      }

      boolean var25 = false;
      if (var2.length > 9 && "force".equals(var2[9])) {
         var25 = true;
      }

      World var26 = var1.getEntityWorld();
      if (var26 instanceof WorldServer) {
         WorldServer var27 = (WorldServer)var26;
         int[] var28 = new int[var7.getArgumentCount()];
         if (var7.hasArguments()) {
            String[] var29 = var2[0].split("_", 3);

            for (int var30 = 1; var30 < var29.length; var30++) {
               try {
                  var28[var30 - 1] = Integer.parseInt(var29[var30]);
               } catch (NumberFormatException var32) {
                  throw new CommandException("commands.particle.notFound", var2[0]);
               }
            }
         }

         var27.spawnParticle(var7, var25, var35, var12, var14, var24, var16, var18, var20, var22, var28);
         notifyOperators(var1, this, "commands.particle.success", var33, Math.max(var24, 1));
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, EnumParticleTypes.getParticleNames())
         : (
            var2.length > 1 && var2.length <= 4
               ? func_175771_a(var2, 1, var3)
               : (var2.length == 10 ? getListOfStringsMatchingLastWord(var2, "normal", "force") : null)
         );
   }
}
