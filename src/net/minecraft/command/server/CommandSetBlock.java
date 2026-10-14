package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase {
   @Override
   public String getCommandName() {
      return "setblock";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.setblock.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.setblock.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      Block block = CommandBase.getBlockByText(sender, args[3]);
      int i = 0;
      if (args.length >= 5) {
         i = parseInt(args[4], 0, 15);
      }

      World world = sender.getEntityWorld();
      if (!world.isBlockLoaded(blockPos)) {
         throw new CommandException("commands.setblock.outOfWorld");
      }

      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      boolean bl = false;
      if (args.length >= 7 && block.hasTileEntity()) {
         String string = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();

         try {
            nbtTagCompound = JsonToNBT.getTagFromJson(string);
            bl = true;
         } catch (NBTException nbtException) {
            throw new CommandException("commands.setblock.tagError", nbtException.getMessage());
         }
      }

      if (args.length >= 6) {
         if (args[5].equals("destroy")) {
            world.destroyBlock(blockPos, true);
            if (block == Blocks.air) {
               notifyOperators(sender, this, "commands.setblock.success");
               return;
            }
         } else if (args[5].equals("keep") && !world.isAirBlock(blockPos)) {
            throw new CommandException("commands.setblock.noChange");
         }
      }

      TileEntity tileEntity = world.getTileEntity(blockPos);
      if (tileEntity != null) {
         if (tileEntity instanceof IInventory) {
            ((IInventory)tileEntity).clear();
         }

         world.setBlockState(blockPos, Blocks.air.getDefaultState(), block == Blocks.air ? 2 : 4);
      }

      IBlockState iBlockState = block.getStateFromMeta(i);
      if (!world.setBlockState(blockPos, iBlockState, 2)) {
         throw new CommandException("commands.setblock.noChange");
      }

      if (bl) {
         TileEntity tileEntity2 = world.getTileEntity(blockPos);
         if (tileEntity2 != null) {
            nbtTagCompound.setInteger("x", blockPos.getX());
            nbtTagCompound.setInteger("y", blockPos.getY());
            nbtTagCompound.setInteger("z", blockPos.getZ());
            tileEntity2.readFromNBT(nbtTagCompound);
         }
      }

      world.notifyNeighborsRespectDebug(blockPos, iBlockState.getBlock());
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(sender, this, "commands.setblock.success");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? func_175771_a(args, 0, pos)
         : (
            args.length == 4
               ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
               : (args.length == 6 ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep") : null)
         );
   }
}
