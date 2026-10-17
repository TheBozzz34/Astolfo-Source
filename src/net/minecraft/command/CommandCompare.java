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
   @Override
   public String getCommandName() {
      return "testforblocks";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.compare.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 9) {
         throw new WrongUsageException("commands.compare.usage");
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
      BlockPos blockPos = parseBlockPos(sender, args, 0, false);
      BlockPos blockPos2 = parseBlockPos(sender, args, 3, false);
      BlockPos blockPos3 = parseBlockPos(sender, args, 6, false);
      StructureBoundingBox structureBoundingBox = new StructureBoundingBox(blockPos, blockPos2);
      StructureBoundingBox structureBoundingBox2 = new StructureBoundingBox(blockPos3, blockPos3.add(structureBoundingBox.func_175896_b()));
      int i = structureBoundingBox.getXSize() * structureBoundingBox.getYSize() * structureBoundingBox.getZSize();
      if (i > 524288) {
         throw new CommandException("commands.compare.tooManyBlocks", i, 524288);
      }

      if (structureBoundingBox.minY >= 0 && structureBoundingBox.maxY < 256 && structureBoundingBox2.minY >= 0 && structureBoundingBox2.maxY < 256) {
         World world = sender.getEntityWorld();
         if (world.isAreaLoaded(structureBoundingBox) && world.isAreaLoaded(structureBoundingBox2)) {
            boolean bl = false;
            if (args.length > 9 && args[9].equals("masked")) {
               bl = true;
            }

            i = 0;
            BlockPos blockPos4 = new BlockPos(structureBoundingBox2.minX - structureBoundingBox.minX, structureBoundingBox2.minY - structureBoundingBox.minY, structureBoundingBox2.minZ - structureBoundingBox.minZ);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();

            for (int j = structureBoundingBox.minZ; j <= structureBoundingBox.maxZ; j++) {
               for (int k = structureBoundingBox.minY; k <= structureBoundingBox.maxY; k++) {
                  for (int l = structureBoundingBox.minX; l <= structureBoundingBox.maxX; l++) {
                     mutableBlockPos.set(l, k, j);
                     mutableBlockPos2.set(l + blockPos4.getX(), k + blockPos4.getY(), j + blockPos4.getZ());
                     boolean bl2 = false;
                     IBlockState iBlockState = world.getBlockState(mutableBlockPos);
                     if (!bl || iBlockState.getBlock() != Blocks.air) {
                        if (iBlockState == world.getBlockState(mutableBlockPos2)) {
                           TileEntity tileEntity = world.getTileEntity(mutableBlockPos);
                           TileEntity tileEntity2 = world.getTileEntity(mutableBlockPos2);
                           if (tileEntity != null && tileEntity2 != null) {
                              NBTTagCompound nbtTagCompound = new NBTTagCompound();
                              tileEntity.writeToNBT(nbtTagCompound);
                              nbtTagCompound.removeTag("x");
                              nbtTagCompound.removeTag("y");
                              nbtTagCompound.removeTag("z");
                              NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                              tileEntity2.writeToNBT(nbtTagCompound2);
                              nbtTagCompound2.removeTag("x");
                              nbtTagCompound2.removeTag("y");
                              nbtTagCompound2.removeTag("z");
                              if (!nbtTagCompound.equals(nbtTagCompound2)) {
                                 bl2 = true;
                              }
                           } else if (tileEntity != null) {
                              bl2 = true;
                           }
                        } else {
                           bl2 = true;
                        }

                        i++;
                        if (bl2) {
                           throw new CommandException("commands.compare.failed");
                        }
                     }
                  }
               }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i);
            notifyOperators(sender, this, "commands.compare.success", i);
         } else {
            throw new CommandException("commands.compare.outOfWorld");
         }
      } else {
         throw new CommandException("commands.compare.outOfWorld");
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
                     : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "masked", "all") : null)
               )
         );
   }
}
