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
   public String getCommandUsage(ICommandSender var1) {
      return "commands.testforblock.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new WrongUsageException("commands.testforblock.usage");
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos var7 = parseBlockPos(var1, var2, 0, false);
      Block var8 = Block.getBlockFromName(var2[3]);
      if (var8 == null) {
         throw new NumberInvalidException("commands.setblock.notFound", var2[3]);
      }

      int var9 = -1;
      if (var2.length >= 5) {
         var9 = parseInt(var2[4], -1, 15);
      }

      World var10 = var1.getEntityWorld();
      if (!var10.isBlockLoaded(var7)) {
         throw new CommandException("commands.testforblock.outOfWorld");
      }

      NBTTagCompound var11 = new NBTTagCompound();
      boolean var12 = false;
      if (var2.length >= 6 && var8.hasTileEntity()) {
         String var13 = getChatComponentFromNthArg(var1, var2, 5).getUnformattedText();

         try {
            var11 = JsonToNBT.getTagFromJson(var13);
            var12 = true;
         } catch (NBTException var17) {
            throw new CommandException("commands.setblock.tagError", var17.getMessage());
         }
      }

      IBlockState var18 = var10.getBlockState(var7);
      Block var14 = var18.getBlock();
      if (var14 != var8) {
         throw new CommandException(
            "commands.testforblock.failed.tile", var7.getX(), var7.getY(), var7.getZ(), var14.getLocalizedName(), var8.getLocalizedName()
         );
      }

      if (var9 > -1) {
         int var15 = var18.getBlock().getMetaFromState(var18);
         if (var15 != var9) {
            throw new CommandException("commands.testforblock.failed.data", var7.getX(), var7.getY(), var7.getZ(), var15, var9);
         }
      }

      if (var12) {
         TileEntity var19 = var10.getTileEntity(var7);
         if (var19 == null) {
            throw new CommandException("commands.testforblock.failed.tileEntity", var7.getX(), var7.getY(), var7.getZ());
         }

         NBTTagCompound var16 = new NBTTagCompound();
         var19.writeToNBT(var16);
         if (!NBTUtil.func_181123_a(var11, var16, true)) {
            throw new CommandException("commands.testforblock.failed.nbt", var7.getX(), var7.getY(), var7.getZ());
         }
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
      notifyOperators(var1, this, "commands.testforblock.success", var7.getX(), var7.getY(), var7.getZ());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3
         ? func_175771_a(var2, 0, var3)
         : (var2.length == 4 ? getListOfStringsMatchingLastWord(var2, Block.blockRegistry.getKeys()) : null);
   }
}
