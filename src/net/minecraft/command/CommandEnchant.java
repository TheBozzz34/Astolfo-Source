package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandEnchant extends CommandBase {
   private static final long b = 112281889923139L;

   @Override
   public String getCommandName() {
      return "enchant";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.enchant.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.enchant.usage");
      }

      EntityPlayerMP entityPlayerMP = getPlayer(sender, args[0]);
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

      int i;
      try {
         i = parseInt(args[1], 0);
      } catch (NumberInvalidException numberInvalidException) {
         Enchantment enchantment = Enchantment.getEnchantmentByLocation(args[1]);
         if (enchantment == null) {
            throw numberInvalidException;
         }

         i = enchantment.effectId;
      }

      int j = 1;
      ItemStack itemStack = entityPlayerMP.getCurrentEquippedItem();
      if (itemStack == null) {
         throw new CommandException("commands.enchant.noItem");
      }

      Enchantment enchantment2 = Enchantment.getEnchantmentById(i);
      if (enchantment2 == null) {
         throw new NumberInvalidException("commands.enchant.notFound", i);
      }

      if (!enchantment2.canApply(itemStack)) {
         throw new CommandException("commands.enchant.cantEnchant");
      }

      if (args.length >= 3) {
         j = parseInt(args[2], enchantment2.getMinLevel(), enchantment2.getMaxLevel());
      }

      if (itemStack.hasTagCompound()) {
         NBTTagList nbtTagList = itemStack.getEnchantmentTagList();
         if (nbtTagList != null) {
            for (int k = 0; k < nbtTagList.tagCount(); k++) {
               short s = nbtTagList.getCompoundTagAt(k).getShort("id");
               if (Enchantment.getEnchantmentById(s) != null) {
                  Enchantment enchantment3 = Enchantment.getEnchantmentById(s);
                  if (!enchantment3.canApplyTogether(enchantment2)) {
                     throw new CommandException(
                        "commands.enchant.cantCombine", enchantment2.getTranslatedName(j), enchantment3.getTranslatedName(nbtTagList.getCompoundTagAt(k).getShort("lvl"))
                     );
                  }
               }
            }
         }
      }

      itemStack.addEnchantment(enchantment2, j);
      notifyOperators(sender, this, "commands.enchant.success");
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.func_181077_c()) : null);
   }

   public String[] getListOfPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
