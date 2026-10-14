package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase {
   @Override
   public String getCommandName() {
      return "execute";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.execute.usage";
   }

   @Override
   public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 5) {
         throw new WrongUsageException("commands.execute.usage");
      }

      final Entity entity = getEntity(sender, args[0], Entity.class);
      final double d = parseDouble(entity.posX, args[1], false);
      final double e = parseDouble(entity.posY, args[2], false);
      final double d2 = parseDouble(entity.posZ, args[3], false);
      final BlockPos blockPos = new BlockPos(d, e, d2);
      byte b = 4;
      if ("detect".equals(args[4]) && args.length > 10) {
         World world = entity.getEntityWorld();
         double d3 = parseDouble(d, args[5], false);
         double d4 = parseDouble(e, args[6], false);
         double d5 = parseDouble(d2, args[7], false);
         Block block = getBlockByText(sender, args[8]);
         int i = parseInt(args[9], -1, 15);
         BlockPos blockPos2 = new BlockPos(d3, d4, d5);
         IBlockState iBlockState = world.getBlockState(blockPos2);
         if (iBlockState.getBlock() != block || i >= 0 && iBlockState.getBlock().getMetaFromState(iBlockState) != i) {
            throw new CommandException("commands.execute.failed", "detect", entity.getName());
         }

         b = 10;
      }

      String string = buildString(args, b);
      ICommandSender iCommandSender = new ICommandSender() {
         @Override
         public String getName() {
            return entity.getName();
         }

         @Override
         public IChatComponent getDisplayName() {
            return entity.getDisplayName();
         }

         @Override
         public void addChatMessage(IChatComponent component) {
            sender.addChatMessage(component);
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return sender.canCommandSenderUseCommand(permLevel, commandName);
         }

         @Override
         public BlockPos getPosition() {
            return blockPos;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(d, e, d2);
         }

         @Override
         public World getEntityWorld() {
            return entity.worldObj;
         }

         @Override
         public Entity getCommandSenderEntity() {
            return entity;
         }

         @Override
         public boolean sendCommandFeedback() {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            return minecraftServer == null || minecraftServer.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
         }

         @Override
         public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
            entity.setCommandStat(typeIn, amount);
         }
      };
      ICommandManager iCommandManager = MinecraftServer.getServer().getCommandManager();

      try {
         int j = iCommandManager.executeCommand(iCommandSender, string);
         if (j < 1) {
            throw new CommandException("commands.execute.allInvocationsFailed", string);
         }
      } catch (Throwable throwable) {
         throw new CommandException("commands.execute.failed", string, entity.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (
            args.length > 1 && args.length <= 4
               ? func_175771_a(args, 1, pos)
               : (
                  args.length > 5 && args.length <= 8 && "detect".equals(args[4])
                     ? func_175771_a(args, 5, pos)
                     : (args.length == 9 && "detect".equals(args[4]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
               )
         );
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
