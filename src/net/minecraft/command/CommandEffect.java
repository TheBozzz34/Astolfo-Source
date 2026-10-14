package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
   @Override
   public String getCommandName() {
      return "effect";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.effect.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.effect.usage");
      }

      EntityLivingBase entityLivingBase = getEntity(sender, args[0], EntityLivingBase.class);
      if (args[1].equals("clear")) {
         if (entityLivingBase.getActivePotionEffects().isEmpty()) {
            throw new CommandException("commands.effect.failure.notActive.all", entityLivingBase.getName());
         }

         entityLivingBase.clearActivePotions();
         notifyOperators(sender, this, "commands.effect.success.removed.all", entityLivingBase.getName());
      } else {
         int i;
         try {
            i = parseInt(args[1], 1);
         } catch (NumberInvalidException numberInvalidException) {
            Potion potion = Potion.getPotionFromResourceLocation(args[1]);
            if (potion == null) {
               throw numberInvalidException;
            }

            i = potion.id;
         }

         int j = 600;
         int k = 30;
         int l = 0;
         if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null) {
            throw new NumberInvalidException("commands.effect.notFound", i);
         }

         Potion potion2 = Potion.potionTypes[i];
         if (args.length >= 3) {
            k = parseInt(args[2], 0, 1000000);
            if (potion2.isInstant()) {
               j = k;
            } else {
               j = k * 20;
            }
         } else if (potion2.isInstant()) {
            j = 1;
         }

         if (args.length >= 4) {
            l = parseInt(args[3], 0, 255);
         }

         boolean bl = true;
         if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
            bl = false;
         }

         if (k > 0) {
            PotionEffect potionEffect = new PotionEffect(i, j, l, false, bl);
            entityLivingBase.addPotionEffect(potionEffect);
            notifyOperators(sender, this, "commands.effect.success", new ChatComponentTranslation(potionEffect.getEffectName()), i, l, entityLivingBase.getName(), k);
         } else {
            if (!entityLivingBase.isPotionActive(i)) {
               throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(potion2.getName()), entityLivingBase.getName());
            }

            entityLivingBase.removePotionEffect(i);
            notifyOperators(sender, this, "commands.effect.success.removed", new ChatComponentTranslation(potion2.getName()), entityLivingBase.getName());
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getAllUsernames())
         : (
            args.length == 2
               ? getListOfStringsMatchingLastWord(args, Potion.getPotionLocations())
               : (args.length == 5 ? getListOfStringsMatchingLastWord(args, "true", "false") : null)
         );
   }

   public String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
