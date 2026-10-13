package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {
   private static final long b = 123473374347705L;

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(var1);
   }

   @Override
   public String getCommandName() {
      return "seed";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.seed.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      World var6 = var1 instanceof EntityPlayer ? ((EntityPlayer)var1).worldObj : MinecraftServer.getServer().worldServerForDimension(0);
      var1.addChatMessage(new ChatComponentTranslation("commands.seed.success", var6.getSeed()));
   }
}
