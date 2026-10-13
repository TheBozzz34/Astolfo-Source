package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase {
   private static final long b = 110640055066242L;

   @Override
   public String getCommandName() {
      return "weather";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.weather.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2.length <= 2) {
         int var6 = (300 + new Random().nextInt(600)) * 20;
         if (var2.length >= 2) {
            var6 = parseInt(var2[1], 1, 1000000) * 20;
         }

         WorldServer var7 = MinecraftServer.getServer().worldServers[0];
         WorldInfo var8 = var7.getWorldInfo();
         if ("clear".equalsIgnoreCase(var2[0])) {
            var8.setCleanWeatherTime(var6);
            var8.setRainTime(0);
            var8.setThunderTime(0);
            var8.setRaining(false);
            var8.setThundering(false);
            notifyOperators(var1, this, "commands.weather.clear");
         } else if ("rain".equalsIgnoreCase(var2[0])) {
            var8.setCleanWeatherTime(0);
            var8.setRainTime(var6);
            var8.setThunderTime(var6);
            var8.setRaining(true);
            var8.setThundering(false);
            notifyOperators(var1, this, "commands.weather.rain");
         } else {
            if (!"thunder".equalsIgnoreCase(var2[0])) {
               throw new WrongUsageException("commands.weather.usage");
            }

            var8.setCleanWeatherTime(0);
            var8.setRainTime(var6);
            var8.setThunderTime(var6);
            var8.setRaining(true);
            var8.setThundering(true);
            notifyOperators(var1, this, "commands.weather.thunder");
         }
      } else {
         throw new WrongUsageException("commands.weather.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, "clear", "rain", "thunder") : null;
   }
}
