package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone extends CommandBase {
   @Override
   public String getCommandName() {
      return "clone";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clone.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.clone.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      BlockPos blockPos3 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(blockPos, blockPos2);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(blockPos3, blockPos3.add(structureBoundingBox.func_175896_b()));
      int i = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (i > 32768) {
         throw new CommandException("commands.clone.tooManyBlocks", i, 32768);
      }

      boolean bl = false;
      Block block = null;
      int j = -1;
      if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && structureBoundingBox.intersectsWith(structureBoundingBox2)) {
         throw new CommandException("commands.clone.noOverlap");
      }

      if (args.length >= 11 && args[10].equals("move")) {
         bl = true;
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World world = sender.getEntityWorld();
         if (world.isAreaLoaded(structureBoundingBox) && world.isAreaLoaded(structureBoundingBox2)) {
            boolean bl2 = false;
            if (args.length >= 10) {
               if (args[9].equals("masked")) {
                  bl2 = true;
               } else if (args[9].equals("filtered")) {
                  if (args.length < 12) {
                     throw new WrongUsageException("commands.clone.usage");
                  }

                  block = getBlockByText(sender, args[11]);
                  if (args.length >= 13) {
                     j = parseInt(args[12], 0, 15);
                  }
               }
            }

            ArrayList arrayList = Lists.newArrayList();
            ArrayList arrayList2 = Lists.newArrayList();
            ArrayList arrayList3 = Lists.newArrayList();
            LinkedList linkedList = Lists.newLinkedList();
            BlockPos blockPos4 = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);

            for (int k = structureBoundingBox.minZ; k <= structureBoundingBox.maxZ; k++) {
               for (int l = structureBoundingBox.minY; l <= structureBoundingBox.maxY; l++) {
                  for (int m = structureBoundingBox.minX; m <= structureBoundingBox.maxX; m++) {
                     BlockPos blockPos5 = new BlockPos(m, l, k);
                     BlockPos blockPos6 = blockPos5.add(blockPos4);
                     IBlockState iBlockState = world.getBlockState(blockPos5);
                     if ((!bl2 || iBlockState.getBlock() != Blocks.air)
                        && (block == null || iBlockState.getBlock() == block && (j < 0 || iBlockState.getBlock().getMetaFromState(iBlockState) == j))) {
                        TileEntity tileEntity = world.getTileEntity(blockPos5);
                        if (tileEntity != null) {
                           NBTTagCompound nbtTagCompound = new NBTTagCompound();
                           tileEntity.writeToNBT(nbtTagCompound);
                           arrayList2.add(new CommandClone.StaticCloneData(blockPos6, iBlockState, nbtTagCompound));
                           linkedList.addLast(blockPos5);
                        } else if (!iBlockState.getBlock().isFullBlock() && !iBlockState.getBlock().isFullCube()) {
                           arrayList3.add(new CommandClone.StaticCloneData(blockPos6, iBlockState, (NBTTagCompound)null));
                           linkedList.addFirst(blockPos5);
                        } else {
                           arrayList.add(new CommandClone.StaticCloneData(blockPos6, iBlockState, (NBTTagCompound)null));
                           linkedList.addLast(blockPos5);
                        }
                     }
                  }
               }
            }

            if (bl) {
               for (Object object : linkedList) {
                  BlockPos blockPos7 = (BlockPos)object;
                  TileEntity tileEntity2 = world.getTileEntity(blockPos7);
                  if (tileEntity2 instanceof IInventory) {
                     ((IInventory)tileEntity2).clear();
                  }

                  world.setBlockState(blockPos7, Blocks.barrier.getDefaultState(), 2);
               }

               for (Object object2 : linkedList) {
                  BlockPos blockPos8 = (BlockPos)object2;
                  world.setBlockState(blockPos8, Blocks.air.getDefaultState(), 3);
               }
            }

            ArrayList arrayList4 = Lists.newArrayList();
            arrayList4.addAll(arrayList);
            arrayList4.addAll(arrayList2);
            arrayList4.addAll(arrayList3);
            List list = Lists.reverse(arrayList4);

            for (Object object3 : list) {
               CommandClone.StaticCloneData staticCloneData = (CommandClone.StaticCloneData)object3;
               TileEntity tileEntity3 = world.getTileEntity(staticCloneData.pos);
               if (tileEntity3 instanceof IInventory) {
                  ((IInventory)tileEntity3).clear();
               }

               world.setBlockState(staticCloneData.pos, Blocks.barrier.getDefaultState(), 2);
            }

            i = 0;

            for (Object object4 : arrayList4) {
               CommandClone.StaticCloneData staticCloneData2 = (CommandClone.StaticCloneData)object4;
               if (world.setBlockState(staticCloneData2.pos, staticCloneData2.blockState, 2)) {
                  i++;
               }
            }

            for (Object object5 : arrayList2) {
               CommandClone.StaticCloneData staticCloneData3 = (CommandClone.StaticCloneData)object5;
               TileEntity tileEntity4 = world.getTileEntity(staticCloneData3.pos);
               if (staticCloneData3.compound != null && tileEntity4 != null) {
                  staticCloneData3.compound.setInteger("x", staticCloneData3.pos.getX());
                  staticCloneData3.compound.setInteger("y", staticCloneData3.pos.getY());
                  staticCloneData3.compound.setInteger("z", staticCloneData3.pos.getZ());
                  tileEntity4.readFromNBT(staticCloneData3.compound);
                  tileEntity4.markDirty();
               }

               world.setBlockState(staticCloneData3.pos, staticCloneData3.blockState, 2);
            }

            for (Object object6 : list) {
               CommandClone.StaticCloneData staticCloneData4 = (CommandClone.StaticCloneData)object6;
               world.notifyNeighborsRespectDebug(staticCloneData4.pos, staticCloneData4.blockState.getBlock());
            }

            List list2 = world.func_175712_a(structureBoundingBox, false);
            if (list2 != null) {
               for (Object object7 : list2) {
                  NextTickListEntry nextTickListEntry = (NextTickListEntry)object7;
                  if (structureBoundingBox.isVecInside(nextTickListEntry.position)) {
                     BlockPos blockPos9 = nextTickListEntry.position.add(blockPos4);
                     world.scheduleBlockUpdate(blockPos9, nextTickListEntry.getBlock(), (int)(nextTickListEntry.scheduledTime - world.getWorldInfo().getWorldTotalTime()), nextTickListEntry.priority);
                  }
               }
            }

            if (i <= 0) {
               throw new CommandException("commands.clone.failed");
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i);
            notifyOperators(sender, this, "commands.clone.success", i);
         } else {
            throw new CommandException("commands.clone.outOfWorld");
         }
      } else {
         throw new CommandException("commands.clone.outOfWorld");
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
                  args.length > 6 && args.length <= 9
                     ? func_175771_a(args, 6, pos)
                     : (
                        args.length == 10
                           ? getListOfStringsMatchingLastWord(args, "replace", "masked", "filtered")
                           : (
                              args.length == 11
                                 ? getListOfStringsMatchingLastWord(args, "normal", "force", "move")
                                 : (
                                    args.length == 12 && "filtered".equals(args[9])
                                       ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys())
                                       : null
                                 )
                           )
                     )
               )
         );
   }

   public static class StaticCloneData {
      public final BlockPos pos;
      public final IBlockState blockState;
      public final NBTTagCompound compound;

      public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn) {
         this.pos = posIn;
         this.blockState = stateIn;
         this.compound = compoundIn;
      }
   }
}
