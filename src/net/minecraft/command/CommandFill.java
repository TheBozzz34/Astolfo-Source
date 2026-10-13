package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFill extends CommandBase {
   private static final long b = 27384847051314L;

   @Override
   public String getCommandName() {
      return "fill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.fill.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 7) {
         throw new WrongUsageException("commands.fill.usage");
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos var6 = parseBlockPos(var1, var2, 0, false);
      BlockPos var7 = parseBlockPos(var1, var2, 3, false);
      Block var8 = CommandBase.getBlockByText(var1, var2[6]);
      int var9 = 0;
      if (var2.length >= 8) {
         var9 = parseInt(var2[7], 0, 15);
      }

      BlockPos var10 = new BlockPos(Math.min(var6.getX(), var7.getX()), Math.min(var6.getY(), var7.getY()), Math.min(var6.getZ(), var7.getZ()));
      BlockPos var11 = new BlockPos(Math.max(var6.getX(), var7.getX()), Math.max(var6.getY(), var7.getY()), Math.max(var6.getZ(), var7.getZ()));
      int var12 = (var11.getX() - var10.getX() + 1) * (var11.getY() - var10.getY() + 1) * (var11.getZ() - var10.getZ() + 1);
      if (var12 > 32768) {
         throw new CommandException("commands.fill.tooManyBlocks", var12, 32768);
      }

      if (var10.getY() >= 0 && var11.getY() < 256) {
         World var13 = var1.getEntityWorld();

         for (int var14 = var10.getZ(); var14 < var11.getZ() + 16; var14 += 16) {
            for (int var15 = var10.getX(); var15 < var11.getX() + 16; var15 += 16) {
               if (!var13.isBlockLoaded(new BlockPos(var15, var11.getY() - var10.getY(), var14))) {
                  throw new CommandException("commands.fill.outOfWorld");
               }
            }
         }

         NBTTagCompound var26 = new NBTTagCompound();
         boolean var27 = false;
         if (var2.length >= 10 && var8.hasTileEntity()) {
            String var16 = getChatComponentFromNthArg(var1, var2, 9).getUnformattedText();

            try {
               var26 = JsonToNBT.getTagFromJson(var16);
               var27 = true;
            } catch (NBTException var24) {
               throw new CommandException("commands.fill.tagError", var24.getMessage());
            }
         }

         ArrayList var28 = Lists.newArrayList();
         var12 = 0;

         for (int var17 = var10.getZ(); var17 <= var11.getZ(); var17++) {
            for (int var18 = var10.getY(); var18 <= var11.getY(); var18++) {
               for (int var19 = var10.getX(); var19 <= var11.getX(); var19++) {
                  BlockPos var20 = new BlockPos(var19, var18, var17);
                  if (var2.length >= 9) {
                     if (!var2[8].equals("outline") && !var2[8].equals("hollow")) {
                        if (var2[8].equals("destroy")) {
                           var13.destroyBlock(var20, true);
                        } else if (var2[8].equals("keep")) {
                           if (!var13.isAirBlock(var20)) {
                              continue;
                           }
                        } else if (var2[8].equals("replace") && !var8.hasTileEntity()) {
                           if (var2.length > 9) {
                              Block var21 = CommandBase.getBlockByText(var1, var2[9]);
                              if (var13.getBlockState(var20).getBlock() != var21) {
                                 continue;
                              }
                           }

                           if (var2.length > 10) {
                              int var32 = CommandBase.parseInt(var2[10]);
                              IBlockState var22 = var13.getBlockState(var20);
                              if (var22.getBlock().getMetaFromState(var22) != var32) {
                                 continue;
                              }
                           }
                        }
                     } else if (var19 != var10.getX()
                        && var19 != var11.getX()
                        && var18 != var10.getY()
                        && var18 != var11.getY()
                        && var17 != var10.getZ()
                        && var17 != var11.getZ()) {
                        if (var2[8].equals("hollow")) {
                           var13.setBlockState(var20, Blocks.air.getDefaultState(), 2);
                           var28.add(var20);
                        }
                        continue;
                     }
                  }

                  TileEntity var33 = var13.getTileEntity(var20);
                  if (var33 != null) {
                     if (var33 instanceof IInventory) {
                        ((IInventory)var33).clear();
                     }

                     var13.setBlockState(var20, Blocks.barrier.getDefaultState(), var8 == Blocks.barrier ? 2 : 4);
                  }

                  IBlockState var34 = var8.getStateFromMeta(var9);
                  if (var13.setBlockState(var20, var34, 2)) {
                     var28.add(var20);
                     var12++;
                     if (var27) {
                        TileEntity var23 = var13.getTileEntity(var20);
                        if (var23 != null) {
                           var26.setInteger("x", var20.getX());
                           var26.setInteger("y", var20.getY());
                           var26.setInteger("z", var20.getZ());
                           var23.readFromNBT(var26);
                        }
                     }
                  }
               }
            }
         }

         for (Object var30$raw : var28) {
            BlockPos var30 = (BlockPos)var30$raw;
            Block var31 = var13.getBlockState(var30).getBlock();
            var13.notifyNeighborsRespectDebug(var30, var31);
         }

         if (var12 <= 0) {
            throw new CommandException("commands.fill.failed");
         }

         var1.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, var12);
         notifyOperators(var1, this, "commands.fill.success", var12);
      } else {
         throw new CommandException("commands.fill.outOfWorld");
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
                  var2.length == 7
                     ? getListOfStringsMatchingLastWord(var2, Block.blockRegistry.getKeys())
                     : (
                        var2.length == 9
                           ? getListOfStringsMatchingLastWord(var2, "replace", "destroy", "keep", "hollow", "outline")
                           : (var2.length == 10 && "replace".equals(var2[8]) ? getListOfStringsMatchingLastWord(var2, Block.blockRegistry.getKeys()) : null)
                     )
               )
         );
   }
}
