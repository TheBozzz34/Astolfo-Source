package net.minecraft.command.server;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender {
   private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");
   private int successCount;
   private boolean trackOutput = true;
   private IChatComponent lastOutput = null;
   private String commandStored = "";
   private String customName = "@";
   private final CommandResultStats resultStats = new CommandResultStats();

   public int getSuccessCount() {
      return this.successCount;
   }

   public IChatComponent getLastOutput() {
      return this.lastOutput;
   }

   public void writeDataToNBT(NBTTagCompound tagCompound) {
      tagCompound.setString("Command", this.commandStored);
      tagCompound.setInteger("SuccessCount", this.successCount);
      tagCompound.setString("CustomName", this.customName);
      tagCompound.setBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         tagCompound.setString("LastOutput", IChatComponent.Serializer.componentToJson(this.lastOutput));
      }

      this.resultStats.writeStatsToNBT(tagCompound);
   }

   public void readDataFromNBT(NBTTagCompound nbt) {
      this.commandStored = nbt.getString("Command");
      this.successCount = nbt.getInteger("SuccessCount");
      if (nbt.hasKey("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }

      if (nbt.hasKey("TrackOutput", 1)) {
         this.trackOutput = nbt.getBoolean("TrackOutput");
      }

      if (nbt.hasKey("LastOutput", 8) && this.trackOutput) {
         this.lastOutput = IChatComponent.Serializer.jsonToComponent(nbt.getString("LastOutput"));
      }

      this.resultStats.readStatsFromNBT(nbt);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return permLevel <= 2;
   }

   public void setCommand(String command) {
      this.commandStored = command;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.commandStored;
   }

   public void trigger(World worldIn) {
      if (worldIn.isRemote) {
         this.successCount = 0;
      }

      MinecraftServer minecraftServer = MinecraftServer.getServer();
      if (minecraftServer != null && minecraftServer.isAnvilFileSet() && minecraftServer.isCommandBlockEnabled()) {
         ICommandManager iCommandManager = minecraftServer.getCommandManager();

         try {
            this.lastOutput = null;
            this.successCount = iCommandManager.executeCommand(this, this.commandStored);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Executing command block");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Command to be executed");
            crashReportCategory.addCrashSectionCallable("Command", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.getCommand();
               }
            });
            crashReportCategory.addCrashSectionCallable("Name", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.getName();
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         this.successCount = 0;
      }
   }

   @Override
   public String getName() {
      return this.customName;
   }

   @Override
   public IChatComponent getDisplayName() {
      return new ChatComponentText(this.getName());
   }

   public void setName(String string) {
      this.customName = string;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
         this.lastOutput = new ChatComponentText("[" + timestampFormat.format(new Date()) + "] ").appendSibling(component);
         this.updateCommand();
      }
   }

   @Override
   public boolean sendCommandFeedback() {
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      return minecraftServer == null || !minecraftServer.isAnvilFileSet() || minecraftServer.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
   }

   @Override
   public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
      this.resultStats.setCommandStatScore(this, typeIn, amount);
   }

   public abstract void updateCommand();

   public abstract int func_145751_f();

   public abstract void func_145757_a(ByteBuf byteBuf);

   public void setLastOutput(IChatComponent lastOutputMessage) {
      this.lastOutput = lastOutputMessage;
   }

   public void setTrackOutput(boolean shouldTrackOutput) {
      this.trackOutput = shouldTrackOutput;
   }

   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean tryOpenEditCommandBlock(EntityPlayer playerIn) {
      if (!playerIn.capabilities.isCreativeMode) {
         return false;
      }

      if (playerIn.getEntityWorld().isRemote) {
         playerIn.openEditCommandBlock(this);
      }

      return true;
   }

   public CommandResultStats getCommandResultStats() {
      return this.resultStats;
   }
}
