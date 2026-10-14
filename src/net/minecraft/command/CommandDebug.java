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

   @Override
   public String getCommandName() {
      return "debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.debug.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.debug.usage");
      }

      if (args[0].equals("start")) {
         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         notifyOperators(sender, this, "commands.debug.start");
         MinecraftServer.getServer().x();
         this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
         this.profileStartTick = MinecraftServer.getServer().getTickCounter();
      } else {
         if (!args[0].equals("stop")) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
            throw new CommandException("commands.debug.notStarted");
         }

         long l = MinecraftServer.getCurrentTimeMillis();
         int i = MinecraftServer.getServer().getTickCounter();
         long m = l - this.profileStartTime;
         int j = i - this.profileStartTick;
         this.saveProfileResults(m, j);
         MinecraftServer.getServer().theProfiler.profilingEnabled = false;
         notifyOperators(sender, this, "commands.debug.stop", (float)m / 1000.0F, j);
      }
   }

   private void saveProfileResults(long timeSpan, int tickSpan) {
      File file = new File(
         MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
      );
      file.getParentFile().mkdirs();

      try {
         FileWriter fileWriter = new FileWriter(file);
         fileWriter.write(this.getProfileResults(timeSpan, tickSpan));
         fileWriter.close();
      } catch (Throwable throwable) {
         logger.error("Could not save profiler results to " + file, throwable);
      }
   }

   private String getProfileResults(long timeSpan, int tickSpan) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("---- Minecraft Profiler Results ----\n");
      stringBuilder.append("// ");
      stringBuilder.append(getWittyComment());
      stringBuilder.append("\n\n");
      stringBuilder.append("Time span: ").append(timeSpan).append(" ms\n");
      stringBuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
      stringBuilder.append("// This is approximately ")
         .append(String.format("%.2f", tickSpan / ((float)timeSpan / 1000.0F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.func_147202_a(0, "root", stringBuilder);
      stringBuilder.append("--- END PROFILE DUMP ---\n\n");
      return stringBuilder.toString();
   }

   private void func_147202_a(int i, String string, StringBuilder stringBuilder) {
      List list = MinecraftServer.getServer().theProfiler.getProfilingData(string);
      if (list != null && list.size() >= 3) {
         for (int j = 1; j < list.size(); j++) {
            Profiler.Result result = (Profiler.Result)list.get(j);
            stringBuilder.append(String.format("[%02d] ", i));

            for (int k = 0; k < i; k++) {
               stringBuilder.append(" ");
            }

            stringBuilder.append(result.field_76331_c)
               .append(" - ")
               .append(String.format("%.2f", result.field_76332_a))
               .append("%/")
               .append(String.format("%.2f", result.field_76330_b))
               .append("%\n");
            if (!result.field_76331_c.equals("unspecified")) {
               try {
                  this.func_147202_a(i + 1, string + "." + result.field_76331_c, stringBuilder);
               } catch (Exception exception) {
                  stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
               }
            }
         }
      }
   }

   private static String getWittyComment() {
      String[] strings = new String[]{
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
         return strings[(int)(System.nanoTime() % strings.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, "start", "stop") : null;
   }
}
