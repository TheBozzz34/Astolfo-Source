package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase {
   private static final Logger logger = LogManager.getLogger();
   private long profileStartTime;
   private int profileStartTick;
   private static final long b = 83906345623446L;

   @Override
   public String getCommandName() {
      return "debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.debug.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.debug.usage");
      }

      if (var2[0].equals("start")) {
         if (var2.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         notifyOperators(var1, this, "commands.debug.start");
         MinecraftServer.getServer().x();
         this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
         this.profileStartTick = MinecraftServer.getServer().getTickCounter();
      } else {
         if (!var2[0].equals("stop")) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (var2.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
            throw new CommandException("commands.debug.notStarted");
         }

         long var6 = MinecraftServer.getCurrentTimeMillis();
         int var8 = MinecraftServer.getServer().getTickCounter();
         long var9 = var6 - this.profileStartTime;
         int var11 = var8 - this.profileStartTick;
         this.saveProfileResults(var9, var11);
         MinecraftServer.getServer().theProfiler.profilingEnabled = false;
         notifyOperators(var1, this, "commands.debug.stop", (float)var9 / 1000.0F, var11);
      }
   }

   private void saveProfileResults(long var1, int var3) {
      File var6 = new File(
         MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
      );
      var6.getParentFile().mkdirs();

      try {
         FileWriter var7 = new FileWriter(var6);
         var7.write(this.getProfileResults(var1, var3));
         var7.close();
      } catch (Throwable var8) {
         logger.error("Could not save profiler results to " + var6, var8);
      }
   }

   private String getProfileResults(long var1, int var3) {
      StringBuilder var6 = new StringBuilder();
      var6.append("---- Minecraft Profiler Results ----\n");
      var6.append("// ");
      var6.append(getWittyComment());
      var6.append("\n\n");
      var6.append("Time span: ").append(var1).append(" ms\n");
      var6.append("Tick span: ").append(var3).append(" ticks\n");
      var6.append("// This is approximately ")
         .append(String.format("%.2f", var3 / ((float)var1 / 1000.0F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      var6.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.func_147202_a(0, "root", var6);
      var6.append("--- END PROFILE DUMP ---\n\n");
      return var6.toString();
   }

   private void func_147202_a(int var1, String var2, StringBuilder var3) {
      List var7 = MinecraftServer.getServer().theProfiler.getProfilingData(var2);
      if (var7 != null && var7.size() >= 3) {
         for (int var8 = 1; var8 < var7.size(); var8++) {
            Profiler.Result var9 = (Profiler.Result)var7.get(var8);
            var3.append(String.format("[%02d] ", var1));

            for (int var10 = 0; var10 < var1; var10++) {
               var3.append(" ");
            }

            var3.append(var9.field_76331_c)
               .append(" - ")
               .append(String.format("%.2f", var9.field_76332_a))
               .append("%/")
               .append(String.format("%.2f", var9.field_76330_b))
               .append("%\n");
            if (!var9.field_76331_c.equals("unspecified")) {
               try {
                  this.func_147202_a(var1 + 1, var2 + "." + var9.field_76331_c, var3);
               } catch (Exception var11) {
                  var3.append("[[ EXCEPTION ").append(var11).append(" ]]");
               }
            }
         }
      }
   }

   private static String getWittyComment() {
      String[] var2 = new String[]{
         "Shiny numbers!",
         "Am I not running fast enough? :(",
         "I'm working as hard as I can!",
         "Will I ever be good enough for you? :(",
         "Speedy. Zoooooom!",
         "Hello world",
         "40% better than a crash report.",
         "Now with extra numbers",
         "Now with less numbers",
         "Now with the same numbers",
         "You should add flames to things, it makes them go faster!",
         "Do you feel the need for... optimization?",
         "*cracks redstone whip*",
         "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
      };

      try {
         return var2[(int)(System.nanoTime() % var2.length)];
      } catch (Throwable var4) {
         return "Witty comment unavailable :(";
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, "start", "stop") : null;
   }
}
