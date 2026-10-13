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
   private static final long a = 66599394280267L;

   public int getSuccessCount() {
      return this.successCount;
   }

   public IChatComponent getLastOutput() {
      return this.lastOutput;
   }

   public void writeDataToNBT(NBTTagCompound var1) {
      var1.setString("Command", this.commandStored);
      var1.setInteger("SuccessCount", this.successCount);
      var1.setString("CustomName", this.customName);
      var1.setBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         var1.setString("LastOutput", IChatComponent.Serializer.componentToJson(this.lastOutput));
      }

      this.resultStats.writeStatsToNBT(var1);
   }

   public void readDataFromNBT(NBTTagCompound var1) {
      this.commandStored = var1.getString("Command");
      this.successCount = var1.getInteger("SuccessCount");
      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }

      if (var1.hasKey("TrackOutput", 1)) {
         this.trackOutput = var1.getBoolean("TrackOutput");
      }

      if (var1.hasKey("LastOutput", 8) && this.trackOutput) {
         this.lastOutput = IChatComponent.Serializer.jsonToComponent(var1.getString("LastOutput"));
      }

      this.resultStats.readStatsFromNBT(var1);
   }

   @Override
   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return var1 <= 2;
   }

   public void setCommand(String var1) {
      this.commandStored = var1;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.commandStored;
   }

   public void trigger(World var1) {
      if (var1.isRemote) {
         this.successCount = 0;
      }

      MinecraftServer var5 = MinecraftServer.getServer();
      if (var5 != null && var5.isAnvilFileSet() && var5.isCommandBlockEnabled()) {
         ICommandManager var6 = var5.getCommandManager();

         try {
            this.lastOutput = null;
            this.successCount = var6.executeCommand(this, this.commandStored);
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.makeCrashReport(var10, "Executing command block");
            CrashReportCategory var9 = var8.makeCategory("Command to be executed");
            var9.addCrashSectionCallable("Command", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.getCommand();
               }
            });
            var9.addCrashSectionCallable("Name", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.getName();
               }
            });
            throw new ReportedException(var8);
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

   public void setName(String var1) {
      this.customName = var1;
   }

   @Override
   public void addChatMessage(IChatComponent var1) {
      if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
         this.lastOutput = new ChatComponentText("[" + timestampFormat.format(new Date()) + "] ").appendSibling(var1);
         this.updateCommand();
      }
   }

   @Override
   public boolean sendCommandFeedback() {
      MinecraftServer var4 = MinecraftServer.getServer();
      return var4 == null || !var4.isAnvilFileSet() || var4.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
   }

   @Override
   public void setCommandStat(CommandResultStats.Type var1, int var2) {
      this.resultStats.setCommandStatScore(this, var1, var2);
   }

   public abstract void updateCommand();

   public abstract int func_145751_f();

   public abstract void func_145757_a(ByteBuf var1);

   public void setLastOutput(IChatComponent var1) {
      this.lastOutput = var1;
   }

   public void setTrackOutput(boolean var1) {
      this.trackOutput = var1;
   }

   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean tryOpenEditCommandBlock(EntityPlayer var1) {
      if (!var1.capabilities.isCreativeMode) {
         return false;
      }

      if (var1.getEntityWorld().isRemote) {
         var1.openEditCommandBlock(this);
      }

      return true;
   }

   public CommandResultStats getCommandResultStats() {
      return this.resultStats;
   }
}
