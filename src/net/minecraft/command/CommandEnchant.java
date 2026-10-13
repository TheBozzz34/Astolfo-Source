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
   public String getCommandUsage(ICommandSender var1) {
      return "commands.enchant.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.enchant.usage");
      }

      EntityPlayerMP var6 = getPlayer(var1, var2[0]);
      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

      int var7;
      try {
         var7 = parseInt(var2[1], 0);
      } catch (NumberInvalidException var15) {
         Enchantment var9 = Enchantment.getEnchantmentByLocation(var2[1]);
         if (var9 == null) {
            throw var15;
         }

         var7 = var9.effectId;
      }

      int var8 = 1;
      ItemStack var16 = var6.getCurrentEquippedItem();
      if (var16 == null) {
         throw new CommandException("commands.enchant.noItem");
      }

      Enchantment var10 = Enchantment.getEnchantmentById(var7);
      if (var10 == null) {
         throw new NumberInvalidException("commands.enchant.notFound", var7);
      }

      if (!var10.canApply(var16)) {
         throw new CommandException("commands.enchant.cantEnchant");
      }

      if (var2.length >= 3) {
         var8 = parseInt(var2[2], var10.getMinLevel(), var10.getMaxLevel());
      }

      if (var16.hasTagCompound()) {
         NBTTagList var11 = var16.getEnchantmentTagList();
         if (var11 != null) {
            for (int var12 = 0; var12 < var11.tagCount(); var12++) {
               short var13 = var11.getCompoundTagAt(var12).getShort("id");
               if (Enchantment.getEnchantmentById(var13) != null) {
                  Enchantment var14 = Enchantment.getEnchantmentById(var13);
                  if (!var14.canApplyTogether(var10)) {
                     throw new CommandException(
                        "commands.enchant.cantCombine", var10.getTranslatedName(var8), var14.getTranslatedName(var11.getCompoundTagAt(var12).getShort("lvl"))
                     );
                  }
               }
            }
         }
      }

      var16.addEnchantment(var10, var8);
      notifyOperators(var1, this, "commands.enchant.success");
      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, this.getListOfPlayers())
         : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, Enchantment.func_181077_c()) : null);
   }

   public String[] getListOfPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
