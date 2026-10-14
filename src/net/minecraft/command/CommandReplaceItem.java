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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.replaceitem.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.replaceitem.usage");
      }

      boolean bl;
      if (args[0].equals("entity")) {
         bl = false;
      } else {
         if (!args[0].equals("block")) {
            throw new WrongUsageException("commands.replaceitem.usage");
         }

         bl = true;
      }

      int i;
      if (bl) {
         if (args.length < 6) {
            throw new WrongUsageException("commands.replaceitem.block.usage");
         }

         i = 4;
      } else {
         if (args.length < 4) {
            throw new WrongUsageException("commands.replaceitem.entity.usage");
         }

         i = 2;
      }

      int j = this.getSlotForShortcut(args[i++]);

      Item item;
      try {
         item = getItemByText(sender, args[i]);
      } catch (NumberInvalidException numberInvalidException) {
         if (Block.getBlockFromName(args[i]) != Blocks.air) {
            throw numberInvalidException;
         }

         item = null;
      }

      i++;
      int k = args.length > i ? parseInt(args[i++], 1, 64) : 1;
      int l = args.length > i ? parseInt(args[i++]) : 0;
      ItemStack itemStack = new ItemStack(item, k, l);
      if (args.length > i) {
         String string = getChatComponentFromNthArg(sender, args, i).getUnformattedText();

         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(string));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.replaceitem.tagError", nbtException.getMessage());
         }
      }

      if (itemStack.getItem() == null) {
         itemStack = null;
      }

      if (bl) {
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         BlockPos blockPos = parseBlockPos(sender, args, 1, false);
         World world = sender.getEntityWorld();
         TileEntity tileEntity = world.getTileEntity(blockPos);
         if (tileEntity == null || !(tileEntity instanceof IInventory)) {
            throw new CommandException("commands.replaceitem.noContainer", blockPos.getX(), blockPos.getY(), blockPos.getZ());
         }

         IInventory iInventory = (IInventory)tileEntity;
         if (j >= 0 && j < iInventory.getSizeInventory()) {
            iInventory.setInventorySlotContents(j, itemStack);
         }
      } else {
         Entity entity2 = getEntity(sender, args[1]);
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         if (entity2 instanceof EntityPlayer) {
            ((EntityPlayer)entity2).inventoryContainer.detectAndSendChanges();
         }

         if (!entity2.replaceItemInInventory(j, itemStack)) {
            throw new CommandException("commands.replaceitem.failed", j, k, itemStack == null ? "Air" : itemStack.getChatComponent());
         }

         if (entity2 instanceof EntityPlayer) {
            ((EntityPlayer)entity2).inventoryContainer.detectAndSendChanges();
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k);
      notifyOperators(sender, this, "commands.replaceitem.success", j, k, itemStack == null ? "Air" : itemStack.getChatComponent());
   }

   private int getSlotForShortcut(String shortcut) throws CommandException {
      if (!SHORTCUTS.containsKey(shortcut)) {
         throw new CommandException("commands.generic.parameter.invalid", shortcut);
      } else {
         return SHORTCUTS.get(shortcut);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "entity", "block")
         : (
            args.length == 2 && args[0].equals("entity")
               ? getListOfStringsMatchingLastWord(args, this.getUsernames())
               : (
                  args.length >= 2 && args.length <= 4 && args[0].equals("block")
                     ? func_175771_a(args, 1, pos)
                     : (
                        (args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))
                           ? (
                              args.length == 4 && args[0].equals("entity") || args.length == 6 && args[0].equals("block")
                                 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys())
                                 : null
                           )
                           : getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet())
                     )
               )
         );
   }

   public String[] getUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }

   static {
      for (int i = 0; i < 54; i++) {
         SHORTCUTS.put("slot.container." + i, i);
      }

      for (int j = 0; j < 9; j++) {
         SHORTCUTS.put("slot.hotbar." + j, j);
      }

      for (int k = 0; k < 27; k++) {
         SHORTCUTS.put("slot.inventory." + k, 9 + k);
      }

      for (int l = 0; l < 27; l++) {
         SHORTCUTS.put("slot.enderchest." + l, 200 + l);
      }

      for (int m = 0; m < 8; m++) {
         SHORTCUTS.put("slot.villager." + m, 300 + m);
      }

      for (int n = 0; n < 15; n++) {
         SHORTCUTS.put("slot.horse." + n, 500 + n);
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
