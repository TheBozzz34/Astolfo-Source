package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {
   private static final long b = 51248752171980L;

   @Override
   public String getCommandName() {
      return "testforblock";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.testforblock.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.testforblock.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      Block block = Block.getBlockFromName(args[3]);
      if (block == null) {
         throw new NumberInvalidException("commands.setblock.notFound", args[3]);
      }

      int i = -1;
      if (args.length >= 5) {
         i = parseInt(args[4], -1, 15);
      }

      World world = sender.getEntityWorld();
      if (!world.isBlockLoaded(blockPos)) {
         throw new CommandException("commands.testforblock.outOfWorld");
      }

      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      boolean bl = false;
      if (args.length >= 6 && block.hasTileEntity()) {
         String string = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

         try {
            nbtTagCompound = JsonToNBT.getTagFromJson(string);
            bl = true;
         } catch (NBTException nbtException) {
            throw new CommandException("commands.setblock.tagError", nbtException.getMessage());
         }
      }

      IBlockState iBlockState = world.getBlockState(blockPos);
      Block block2 = iBlockState.getBlock();
      if (block2 != block) {
         throw new CommandException(
            "commands.testforblock.failed.tile", blockPos.getX(), blockPos.getY(), blockPos.getZ(), block2.getLocalizedName(), block.getLocalizedName()
         );
      }

      if (i > -1) {
         int j = iBlockState.getBlock().getMetaFromState(iBlockState);
         if (j != i) {
            throw new CommandException("commands.testforblock.failed.data", blockPos.getX(), blockPos.getY(), blockPos.getZ(), j, i);
         }
      }

      if (bl) {
         TileEntity tileEntity2 = world.getTileEntity(blockPos);
         if (tileEntity2 == null) {
            throw new CommandException("commands.testforblock.failed.tileEntity", blockPos.getX(), blockPos.getY(), blockPos.getZ());
         }

         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         tileEntity2.writeToNBT(nbtTagCompound2);
         if (!NBTUtil.func_181123_a(nbtTagCompound, nbtTagCompound2, true)) {
            throw new CommandException("commands.testforblock.failed.nbt", blockPos.getX(), blockPos.getY(), blockPos.getZ());
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(sender, this, "commands.testforblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? func_175771_a(args, 0, pos)
         : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null);
   }
}
