package net.minecraft.command;

import java.util.List;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase {
   private static final long b = 117353590581572L;

   @Override
   public String getCommandName() {
      return "blockdata";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.blockdata.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.blockdata.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      World world = sender.getEntityWorld();
      if (!world.isBlockLoaded(blockPos)) {
         throw new CommandException("commands.blockdata.outOfWorld");
      }

      TileEntity tileEntity = world.getTileEntity(blockPos);
      if (tileEntity == null) {
         throw new CommandException("commands.blockdata.notValid");
      }

      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      tileEntity.writeToNBT(nbtTagCompound);
      NBTTagCompound nbtTagCompound2 = (NBTTagCompound)nbtTagCompound.copy();

      NBTTagCompound nbtTagCompound3;
      try {
         nbtTagCompound3 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 3).getUnformattedText());
      } catch (NBTException nbtException) {
         throw new CommandException("commands.blockdata.tagError", nbtException.getMessage());
      }

      nbtTagCompound.merge(nbtTagCompound3);
      nbtTagCompound.setInteger("x", blockPos.getX());
      nbtTagCompound.setInteger("y", blockPos.getY());
      nbtTagCompound.setInteger("z", blockPos.getZ());
      if (nbtTagCompound.equals(nbtTagCompound2)) {
         throw new CommandException("commands.blockdata.failed", nbtTagCompound.toString());
      }

      tileEntity.readFromNBT(nbtTagCompound);
      tileEntity.markDirty();
      world.markBlockForUpdate(blockPos);
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(sender, this, "commands.blockdata.success", nbtTagCompound.toString());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : null;
   }
}
