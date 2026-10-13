package net.minecraft.command;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandCompare extends CommandBase {
   private static final long b = 97244177443141L;

   @Override
   public String getCommandName() {
      return "testforblocks";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.compare.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 9) {
         throw new WrongUsageException("commands.compare.usage");
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos var6 = parseBlockPos(var1, var2, 0, false);
      BlockPos var7 = parseBlockPos(var1, var2, 3, false);
      BlockPos var8 = parseBlockPos(var1, var2, 6, false);
      StructureBoundingBox var9 = new StructureBoundingBox(var6, var7);
      StructureBoundingBox var10 = new StructureBoundingBox(var8, var8.add(var9.func_175896_b()));
      int var11 = var9.G() * var9.getYSize() * var9.S();
      if (var11 > 524288) {
         throw new CommandException("commands.compare.tooManyBlocks", var11, 524288);
      }

      if (var9.minY >= 0 && var9.maxY < 256 && var10.minY >= 0 && var10.maxY < 256) {
         World var12 = var1.getEntityWorld();
         if (var12.isAreaLoaded(var9) && var12.isAreaLoaded(var10)) {
            boolean var13 = false;
            if (var2.length > 9 && var2[9].equals("masked")) {
               var13 = true;
            }

            var11 = 0;
            BlockPos var14 = new BlockPos(var10.minX - var9.minX, var10.minY - var9.minY, var10.minZ - var9.minZ);
            BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();

            for (int var17 = var9.minZ; var17 <= var9.maxZ; var17++) {
               for (int var18 = var9.minY; var18 <= var9.maxY; var18++) {
                  for (int var19 = var9.minX; var19 <= var9.maxX; var19++) {
                     var15.set(var19, var18, var17);
                     var16.set(var19 + var14.getX(), var18 + var14.getY(), var17 + var14.getZ());
                     boolean var20 = false;
                     IBlockState var21 = var12.getBlockState(var15);
                     if (!var13 || var21.getBlock() != Blocks.air) {
                        if (var21 == var12.getBlockState(var16)) {
                           TileEntity var22 = var12.getTileEntity(var15);
                           TileEntity var23 = var12.getTileEntity(var16);
                           if (var22 != null && var23 != null) {
                              NBTTagCompound var24 = new NBTTagCompound();
                              var22.writeToNBT(var24);
                              var24.removeTag("x");
                              var24.removeTag("y");
                              var24.removeTag("z");
                              NBTTagCompound var25 = new NBTTagCompound();
                              var23.writeToNBT(var25);
                              var25.removeTag("x");
                              var25.removeTag("y");
                              var25.removeTag("z");
                              if (!var24.equals(var25)) {
                                 var20 = true;
                              }
                           } else if (var22 != null) {
                              var20 = true;
                           }
                        } else {
                           var20 = true;
                        }

                        var11++;
                        if (var20) {
                           throw new CommandException("commands.compare.failed");
                        }
                     }
                  }
               }
            }

            var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, var11);
            notifyOperators(var1, this, "commands.compare.success", var11);
         } else {
            throw new CommandException("commands.compare.outOfWorld");
         }
      } else {
         throw new CommandException("commands.compare.outOfWorld");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3
         ? func_175771_a(var2, 0, var3)
         : (
            var2.length > 3 && var2.length <= 6
               ? func_175771_a(var2, 3, var3)
               : (
                  var2.length > 6 && var2.length <= 9
                     ? func_175771_a(var2, 6, var3)
                     : (var2.length == 10 ? getListOfStringsMatchingLastWord(var2, "masked", "all") : null)
               )
         );
   }
}
