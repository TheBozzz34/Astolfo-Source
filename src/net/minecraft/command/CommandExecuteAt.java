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
   private static final long b = 28661466441616L;

   @Override
   public String getCommandName() {
      return "execute";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.execute.usage";
   }

   @Override
   public void processCommand(final ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 5) {
         throw new WrongUsageException("commands.execute.usage");
      }

      final Entity var6 = getEntity(var1, var2[0], Entity.class);
      final double var7 = parseDouble(var6.posX, var2[1], false);
      final double var9 = parseDouble(var6.posY, var2[2], false);
      final double var11 = parseDouble(var6.posZ, var2[3], false);
      final BlockPos var13 = new BlockPos(var7, var9, var11);
      byte var14 = 4;
      if ("detect".equals(var2[4]) && var2.length > 10) {
         World var15 = var6.getEntityWorld();
         double var16 = parseDouble(var7, var2[5], false);
         double var18 = parseDouble(var9, var2[6], false);
         double var20 = parseDouble(var11, var2[7], false);
         Block var22 = getBlockByText(var1, var2[8]);
         int var23 = parseInt(var2[9], -1, 15);
         BlockPos var24 = new BlockPos(var16, var18, var20);
         IBlockState var25 = var15.getBlockState(var24);
         if (var25.getBlock() != var22 || var23 >= 0 && var25.getBlock().getMetaFromState(var25) != var23) {
            throw new CommandException("commands.execute.failed", "detect", var6.getName());
         }

         var14 = 10;
      }

      String var27 = buildString(var2, var14);
      ICommandSender var28 = new ICommandSender() {
         private static final long a = 28473378064137L;

         @Override
         public String getName() {
            return var6.getName();
         }

         @Override
         public IChatComponent getDisplayName() {
            return var6.getDisplayName();
         }

         @Override
         public void addChatMessage(IChatComponent var1x) {
            var1.addChatMessage(var1x);
         }

         @Override
         public boolean canCommandSenderUseCommand(int var1x, String var2x) {
            return var1.canCommandSenderUseCommand(var1x, var2x);
         }

         @Override
         public BlockPos getPosition() {
            return var13;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(var7, var9, var11);
         }

         @Override
         public World getEntityWorld() {
            return var6.worldObj;
         }

         @Override
         public Entity getCommandSenderEntity() {
            return var6;
         }

         @Override
         public boolean sendCommandFeedback() {
            MinecraftServer var4 = MinecraftServer.getServer();
            return var4 == null || var4.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
         }

         @Override
         public void setCommandStat(CommandResultStats.Type var1x, int var2x) {
            var6.setCommandStat(var1x, var2x);
         }
      };
      ICommandManager var17 = MinecraftServer.getServer().getCommandManager();

      try {
         int var29 = var17.executeCommand(var28, var27);
         if (var29 < 1) {
            throw new CommandException("commands.execute.allInvocationsFailed", var27);
         }
      } catch (Throwable var26) {
         throw new CommandException("commands.execute.failed", var27, var6.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames())
         : (
            var2.length > 1 && var2.length <= 4
               ? func_175771_a(var2, 1, var3)
               : (
                  var2.length > 5 && var2.length <= 8 && "detect".equals(var2[4])
                     ? func_175771_a(var2, 5, var3)
                     : (var2.length == 9 && "detect".equals(var2[4]) ? getListOfStringsMatchingLastWord(var2, Block.blockRegistry.getKeys()) : null)
               )
         );
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
