package net.minecraft.network.rcon;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
   private static final RConConsoleSource instance = new RConConsoleSource();
   private StringBuffer buffer = new StringBuffer();
   private static final long a = 45560449663671L;

   @Override
   public String getName() {
      return "Rcon";
   }

   @Override
   public IChatComponent getDisplayName() {
      return new ChatComponentText(this.getName());
   }

   @Override
   public void addChatMessage(IChatComponent var1) {
      this.buffer.append(var1.getUnformattedText());
   }

   @Override
   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return true;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(0, 0, 0);
   }

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   @Override
   public World getEntityWorld() {
      return MinecraftServer.getServer().getEntityWorld();
   }

   @Override
   public Entity getCommandSenderEntity() {
      return null;
   }

   @Override
   public boolean sendCommandFeedback() {
      return true;
   }

   @Override
   public void setCommandStat(CommandResultStats.Type var1, int var2) {
   }
}
