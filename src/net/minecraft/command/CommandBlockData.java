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
   public String getCommandUsage(ICommandSender var1) {
      return "commands.blockdata.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new WrongUsageException("commands.blockdata.usage");
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos var6 = parseBlockPos(var1, var2, 0, false);
      World var7 = var1.getEntityWorld();
      if (!var7.isBlockLoaded(var6)) {
         throw new CommandException("commands.blockdata.outOfWorld");
      }

      TileEntity var8 = var7.getTileEntity(var6);
      if (var8 == null) {
         throw new CommandException("commands.blockdata.notValid");
      }

      NBTTagCompound var9 = new NBTTagCompound();
      var8.writeToNBT(var9);
      NBTTagCompound var10 = (NBTTagCompound)var9.copy();

      NBTTagCompound var11;
      try {
         var11 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(var1, var2, 3).getUnformattedText());
      } catch (NBTException var13) {
         throw new CommandException("commands.blockdata.tagError", var13.getMessage());
      }

      var9.merge(var11);
      var9.setInteger("x", var6.getX());
      var9.setInteger("y", var6.getY());
      var9.setInteger("z", var6.getZ());
      if (var9.equals(var10)) {
         throw new CommandException("commands.blockdata.failed", var9.toString());
      }

      var8.readFromNBT(var9);
      var8.markDirty();
      var7.markBlockForUpdate(var6);
      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(var1, this, "commands.blockdata.success", var9.toString());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3 ? func_175771_a(var2, 0, var3) : null;
   }
}
