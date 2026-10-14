package net.minecraft.src;

import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class WorldServerOF extends WorldServer {
   private MinecraftServer mcServer;
   private static final long cb = 120610004128889L;

   public WorldServerOF(MinecraftServer par1MinecraftServer, ISaveHandler par2iSaveHandler, WorldInfo worldInfo, int par4, Profiler par6Profiler) {
      super(par1MinecraftServer, par2iSaveHandler, worldInfo, par4, par6Profiler);
      this.mcServer = par1MinecraftServer;
   }

   @Override
   public void tick() {
      super.tick();
      if (!Config.O()) {
         this.fixWorldTime();
      }

      if (Config.waterOpacityChanged) {
         Config.waterOpacityChanged = false;
         ClearWater.updateWaterOpacity(Config.getGameSettings(), this);
      }
   }

   @Override
   public void updateWeather() {
      if (!Config.pd()) {
         this.fixWorldWeather();
      }

      super.updateWeather();
   }

   private void fixWorldWeather() {
      if (this.worldInfo.isRaining() || this.worldInfo.isThundering()) {
         this.worldInfo.setRainTime(0);
         this.worldInfo.setRaining(false);
         this.V(0.0F);
         this.worldInfo.setThunderTime(0);
         this.worldInfo.setThundering(false);
         this.t(0.0F);
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, 0.0F));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, 0.0F));
      }
   }

   private void fixWorldTime() {
      if (this.worldInfo.getGameType().getID() == 1) {
         long l = this.getWorldTime();
         long m = l % 24000L;
         if (Config.isTimeDayOnly()) {
            if (m <= 1000L) {
               this.setWorldTime(l - m + 1001L);
            }

            if (m >= 11000L) {
               this.setWorldTime(l - m + 24001L);
            }
         }

         if (Config.isTimeNightOnly()) {
            if (m <= 14000L) {
               this.setWorldTime(l - m + 14001L);
            }

            if (m >= 22000L) {
               this.setWorldTime(l - m + 24000L + 14001L);
            }
         }
      }
   }
}
