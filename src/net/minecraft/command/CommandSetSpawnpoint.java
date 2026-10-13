package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetSpawnpoint extends CommandBase {
   private static final long b = 56433896358896L;

   @Override
   public String getCommandName() {
      return "spawnpoint";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.spawnpoint.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 1 && var2.length < 4) {
         throw new WrongUsageException("commands.spawnpoint.usage");
      }

      EntityPlayerMP var6 = var2.length > 0 ? getPlayer(var1, var2[0]) : getCommandSenderAsPlayer(var1);
      BlockPos var7 = var2.length > 3 ? parseBlockPos(var1, var2, 1, true) : var6.getPosition();
      if (var6.worldObj != null) {
         var6.setSpawnPoint(var7, true);
         notifyOperators(var1, this, "commands.spawnpoint.success", var6.getName(), var7.getX(), var7.getY(), var7.getZ());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames())
         : (var2.length > 1 && var2.length <= 4 ? func_175771_a(var2, 1, var3) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
