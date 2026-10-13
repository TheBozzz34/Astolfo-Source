package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetDefaultSpawnpoint extends CommandBase {
   private static final long b = 137965535915109L;

   @Override
   public String getCommandName() {
      return "setworldspawn";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.setworldspawn.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      BlockPos var6;
      if (var2.length == 0) {
         var6 = getCommandSenderAsPlayer(var1).getPosition();
      } else {
         if (var2.length != 3 || var1.getEntityWorld() == null) {
            throw new WrongUsageException("commands.setworldspawn.usage");
         }

         var6 = parseBlockPos(var1, var2, 0, true);
      }

      var1.getEntityWorld().setSpawnPoint(var6);
      MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(var6));
      notifyOperators(var1, this, "commands.setworldspawn.success", var6.getX(), var6.getY(), var6.getZ());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3 ? func_175771_a(var2, 0, var3) : null;
   }
}
