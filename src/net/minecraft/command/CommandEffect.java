package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
   private static final long b = 53975674050309L;

   @Override
   public String getCommandName() {
      return "effect";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.effect.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.effect.usage");
      }

      EntityLivingBase var6 = getEntity(var1, var2[0], EntityLivingBase.class);
      if (var2[1].equals("clear")) {
         if (var6.getActivePotionEffects().isEmpty()) {
            throw new CommandException("commands.effect.failure.notActive.all", var6.getName());
         }

         var6.clearActivePotions();
         notifyOperators(var1, this, "commands.effect.success.removed.all", var6.getName());
      } else {
         int var7;
         try {
            var7 = parseInt(var2[1], 1);
         } catch (NumberInvalidException var14) {
            Potion var9 = Potion.getPotionFromResourceLocation(var2[1]);
            if (var9 == null) {
               throw var14;
            }

            var7 = var9.id;
         }

         int var8 = 600;
         int var15 = 30;
         int var10 = 0;
         if (var7 < 0 || var7 >= Potion.potionTypes.length || Potion.potionTypes[var7] == null) {
            throw new NumberInvalidException("commands.effect.notFound", var7);
         }

         Potion var11 = Potion.potionTypes[var7];
         if (var2.length >= 3) {
            var15 = parseInt(var2[2], 0, 1000000);
            if (var11.isInstant()) {
               var8 = var15;
            } else {
               var8 = var15 * 20;
            }
         } else if (var11.isInstant()) {
            var8 = 1;
         }

         if (var2.length >= 4) {
            var10 = parseInt(var2[3], 0, 255);
         }

         boolean var12 = true;
         if (var2.length >= 5 && "true".equalsIgnoreCase(var2[4])) {
            var12 = false;
         }

         if (var15 > 0) {
            PotionEffect var13 = new PotionEffect(var7, var8, var10, false, var12);
            var6.addPotionEffect(var13);
            notifyOperators(var1, this, "commands.effect.success", new ChatComponentTranslation(var13.getEffectName()), var7, var10, var6.getName(), var15);
         } else {
            if (!var6.isPotionActive(var7)) {
               throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(var11.getName()), var6.getName());
            }

            var6.removePotionEffect(var7);
            notifyOperators(var1, this, "commands.effect.success.removed", new ChatComponentTranslation(var11.getName()), var6.getName());
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, this.getAllUsernames())
         : (
            var2.length == 2
               ? getListOfStringsMatchingLastWord(var2, Potion.getPotionLocations())
               : (var2.length == 5 ? getListOfStringsMatchingLastWord(var2, "true", "false") : null)
         );
   }

   public String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
