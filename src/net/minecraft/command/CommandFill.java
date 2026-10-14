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
   @Override
   public String getCommandName() {
      return "fill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.fill.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 7) {
         throw new WrongUsageException("commands.fill.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      Block block = CommandBase.getBlockByText(sender, args[6]);
      int i = 0;
      if (args.length >= 8) {
         i = parseInt(args[7], 0, 15);
      }

      BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()));
      BlockPos blockPos4 = new BlockPos(Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
      int j = (blockPos4.getX() - blockPos3.getX() + 1) * (blockPos4.getY() - blockPos3.getY() + 1) * (blockPos4.getZ() - blockPos3.getZ() + 1);
      if (j > 32768) {
         throw new CommandException("commands.fill.tooManyBlocks", j, 32768);
      }

      if (blockPos3.getY() >= 0 && blockPos4.getY() < 256) {
         World world = sender.getEntityWorld();

         for (int k = blockPos3.getZ(); k < blockPos4.getZ() + 16; k += 16) {
            for (int l = blockPos3.getX(); l < blockPos4.getX() + 16; l += 16) {
               if (!world.isBlockLoaded(new BlockPos(l, blockPos4.getY() - blockPos3.getY(), k))) {
                  throw new CommandException("commands.fill.outOfWorld");
               }
            }
         }

         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         boolean bl = false;
         if (args.length >= 10 && block.hasTileEntity()) {
            String string = getChatComponentFromNthArg(sender, args, 9).getUnformattedText();

            try {
               nbtTagCompound = JsonToNBT.getTagFromJson(string);
               bl = true;
            } catch (NBTException nbtException) {
               throw new CommandException("commands.fill.tagError", nbtException.getMessage());
            }
         }

         ArrayList arrayList = Lists.newArrayList();
         j = 0;

         for (int m = blockPos3.getZ(); m <= blockPos4.getZ(); m++) {
            for (int n = blockPos3.getY(); n <= blockPos4.getY(); n++) {
               for (int i2 = blockPos3.getX(); i2 <= blockPos4.getX(); i2++) {
                  BlockPos blockPos5 = new BlockPos(i2, n, m);
                  if (args.length >= 9) {
                     if (!args[8].equals("outline") && !args[8].equals("hollow")) {
                        if (args[8].equals("destroy")) {
                           world.destroyBlock(blockPos5, true);
                        } else if (args[8].equals("keep")) {
                           if (!world.isAirBlock(blockPos5)) {
                              continue;
                           }
                        } else if (args[8].equals("replace") && !block.hasTileEntity()) {
                           if (args.length > 9) {
                              Block block2 = CommandBase.getBlockByText(sender, args[9]);
                              if (world.getBlockState(blockPos5).getBlock() != block2) {
                                 continue;
                              }
                           }

                           if (args.length > 10) {
                              int i3 = CommandBase.parseInt(args[10]);
                              IBlockState iBlockState = world.getBlockState(blockPos5);
                              if (iBlockState.getBlock().getMetaFromState(iBlockState) != i3) {
                                 continue;
                              }
                           }
                        }
                     } else if (i2 != blockPos3.getX()
                        && i2 != blockPos4.getX()
                        && n != blockPos3.getY()
                        && n != blockPos4.getY()
                        && m != blockPos3.getZ()
                        && m != blockPos4.getZ()) {
                        if (args[8].equals("hollow")) {
                           world.setBlockState(blockPos5, Blocks.air.getDefaultState(), 2);
                           arrayList.add(blockPos5);
                        }
                        continue;
                     }
                  }

                  TileEntity tileEntity = world.getTileEntity(blockPos5);
                  if (tileEntity != null) {
                     if (tileEntity instanceof IInventory) {
                        ((IInventory)tileEntity).clear();
                     }

                     world.setBlockState(blockPos5, Blocks.barrier.getDefaultState(), block == Blocks.barrier ? 2 : 4);
                  }

                  IBlockState iBlockState2 = block.getStateFromMeta(i);
                  if (world.setBlockState(blockPos5, iBlockState2, 2)) {
                     arrayList.add(blockPos5);
                     j++;
                     if (bl) {
                        TileEntity tileEntity2 = world.getTileEntity(blockPos5);
                        if (tileEntity2 != null) {
                           nbtTagCompound.setInteger("x", blockPos5.getX());
                           nbtTagCompound.setInteger("y", blockPos5.getY());
                           nbtTagCompound.setInteger("z", blockPos5.getZ());
                           tileEntity2.readFromNBT(nbtTagCompound);
                        }
                     }
                  }
               }
            }
         }

         for (Object object : arrayList) {
            BlockPos blockPos6 = (BlockPos)object;
            Block block3 = world.getBlockState(blockPos6).getBlock();
            world.notifyNeighborsRespectDebug(blockPos6, block3);
         }

         if (j <= 0) {
            throw new CommandException("commands.fill.failed");
         }

         sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, j);
         notifyOperators(sender, this, "commands.fill.success", j);
      } else {
         throw new CommandException("commands.fill.outOfWorld");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length > 0 && args.length <= 3
         ? func_175771_a(args, 0, pos)
         : (
            args.length > 3 && args.length <= 6
               ? func_175771_a(args, 3, pos)
               : (
                  args.length == 7
                     ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                     : (
                        args.length == 9
                           ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep", "hollow", "outline")
                           : (args.length == 10 && "replace".equals(args[8]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
                     )
               )
         );
   }
}
