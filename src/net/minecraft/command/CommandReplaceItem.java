package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem extends CommandBase {
   private static final Map<String, Integer> SHORTCUTS = Maps.newHashMap();
   private static final long b = 99219943723977L;

   @Override
   public String getCommandName() {
      return "replaceitem";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.replaceitem.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.replaceitem.usage");
      }

      boolean var6;
      if (var2[0].equals("entity")) {
         var6 = false;
      } else {
         if (!var2[0].equals("block")) {
            throw new WrongUsageException("commands.replaceitem.usage");
         }

         var6 = true;
      }

      int var7;
      if (var6) {
         if (var2.length < 6) {
            throw new WrongUsageException("commands.replaceitem.block.usage");
         }

         var7 = 4;
      } else {
         if (var2.length < 4) {
            throw new WrongUsageException("commands.replaceitem.entity.usage");
         }

         var7 = 2;
      }

      int var8 = this.getSlotForShortcut(var2[var7++]);

      Item var9;
      try {
         var9 = getItemByText(var1, var2[var7]);
      } catch (NumberInvalidException var18) {
         if (Block.getBlockFromName(var2[var7]) != Blocks.air) {
            throw var18;
         }

         var9 = null;
      }

      var7++;
      int var10 = var2.length > var7 ? parseInt(var2[var7++], 1, 64) : 1;
      int var11 = var2.length > var7 ? parseInt(var2[var7++]) : 0;
      ItemStack var12 = new ItemStack(var9, var10, var11);
      if (var2.length > var7) {
         String var13 = getChatComponentFromNthArg(var1, var2, var7).getUnformattedText();

         try {
            var12.setTagCompound(JsonToNBT.getTagFromJson(var13));
         } catch (NBTException var17) {
            throw new CommandException("commands.replaceitem.tagError", var17.getMessage());
         }
      }

      if (var12.getItem() == null) {
         var12 = null;
      }

      if (var6) {
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         BlockPos var21 = parseBlockPos(var1, var2, 1, false);
         World var14 = var1.getEntityWorld();
         TileEntity var15 = var14.getTileEntity(var21);
         if (var15 == null || !(var15 instanceof IInventory)) {
            throw new CommandException("commands.replaceitem.noContainer", var21.getX(), var21.getY(), var21.getZ());
         }

         IInventory var16 = (IInventory)var15;
         if (var8 >= 0 && var8 < var16.getSizeInventory()) {
            var16.setInventorySlotContents(var8, var12);
         }
      } else {
         Entity var22 = getEntity(var1, var2[1]);
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         if (var22 instanceof EntityPlayer) {
            ((EntityPlayer)var22).inventoryContainer.detectAndSendChanges();
         }

         if (!var22.replaceItemInInventory(var8, var12)) {
            throw new CommandException("commands.replaceitem.failed", var8, var10, var12 == null ? "Air" : var12.getChatComponent());
         }

         if (var22 instanceof EntityPlayer) {
            ((EntityPlayer)var22).inventoryContainer.detectAndSendChanges();
         }
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, var10);
      notifyOperators(var1, this, "commands.replaceitem.success", var8, var10, var12 == null ? "Air" : var12.getChatComponent());
   }

   private int getSlotForShortcut(String var1) throws CommandException {
      if (!SHORTCUTS.containsKey(var1)) {
         throw new CommandException("commands.generic.parameter.invalid", var1);
      } else {
         return SHORTCUTS.get(var1);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, "entity", "block")
         : (
            var2.length == 2 && var2[0].equals("entity")
               ? getListOfStringsMatchingLastWord(var2, this.getUsernames())
               : (
                  var2.length >= 2 && var2.length <= 4 && var2[0].equals("block")
                     ? func_175771_a(var2, 1, var3)
                     : (
                        (var2.length != 3 || !var2[0].equals("entity")) && (var2.length != 5 || !var2[0].equals("block"))
                           ? (
                              var2.length == 4 && var2[0].equals("entity") || var2.length == 6 && var2[0].equals("block")
                                 ? getListOfStringsMatchingLastWord(var2, Item.itemRegistry.getKeys())
                                 : null
                           )
                           : getListOfStringsMatchingLastWord(var2, SHORTCUTS.keySet())
                     )
               )
         );
   }

   public String[] getUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var1.length > 0 && var1[0].equals("entity") && var2 == 1;
   }

   static {
      for (int var22 = 0; var22 < 54; var22++) {
         SHORTCUTS.put("slot.container." + var22, var22);
      }

      for (int var23 = 0; var23 < 9; var23++) {
         SHORTCUTS.put("slot.hotbar." + var23, var23);
      }

      for (int var24 = 0; var24 < 27; var24++) {
         SHORTCUTS.put("slot.inventory." + var24, 9 + var24);
      }

      for (int var25 = 0; var25 < 27; var25++) {
         SHORTCUTS.put("slot.enderchest." + var25, 200 + var25);
      }

      for (int var26 = 0; var26 < 8; var26++) {
         SHORTCUTS.put("slot.villager." + var26, 300 + var26);
      }

      for (int var27 = 0; var27 < 15; var27++) {
         SHORTCUTS.put("slot.horse." + var27, 500 + var27);
      }

      SHORTCUTS.put("slot.weapon", 99);
      SHORTCUTS.put("slot.armor.head", 103);
      SHORTCUTS.put("slot.armor.chest", 102);
      SHORTCUTS.put("slot.armor.legs", 101);
      SHORTCUTS.put("slot.armor.feet", 100);
      SHORTCUTS.put("slot.horse.saddle", 400);
      SHORTCUTS.put("slot.horse.armor", 401);
      SHORTCUTS.put("slot.horse.chest", 499);
   }
}
