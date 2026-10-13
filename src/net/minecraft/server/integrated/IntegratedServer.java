package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Reflector;
import net.minecraft.src.WorldServerOF;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final WorldSettings theWorldSettings;
   private boolean isGamePaused;
   private boolean isPublic;
   private ThreadLanServerPing lanServerPing;
   private static final long bb = 113248737067458L;

   public IntegratedServer(Minecraft var1) {
      super(var1.getProxy(), new File(var1.mcDataDir, USER_CACHE_FILE.getName()));
      this.mc = var1;
      this.theWorldSettings = null;
   }

   public IntegratedServer(Minecraft var1, String var2, String var3, WorldSettings var4) {
      super(new File(var1.mcDataDir, "saves"), var1.getProxy(), new File(var1.mcDataDir, USER_CACHE_FILE.getName()));
      this.s(var1.getSession().getUsername());
      this.L(var2);
      this.h(var3);
      this.y(var1.isDemo());
      this.F(var4.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setConfigManager(new IntegratedPlayerList(this));
      this.mc = var1;
      this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : var4;
   }

   @Override
   public ServerCommandManager createNewCommandManager() {
      return new IntegratedServerCommandManager();
   }

   @Override
   public void loadAllWorlds(String var1, String var2, long var3, WorldType var5, String var6) {
      this.convertMapIfNeeded(var1);
      ISaveHandler var11 = this.getActiveAnvilConverter().getSaveLoader(var1, true);
      this.setResourcePackFromWorld(this.getFolderName(), var11);
      WorldInfo var12 = var11.loadWorldInfo();
      if (Reflector.DimensionManager.exists()) {
         WorldServer var13 = this.isDemo()
            ? (WorldServer)new DemoWorldServer(this, var11, var12, 0, this.theProfiler).init()
            : (WorldServer)new WorldServerOF(this, var11, var12, 0, this.theProfiler).init();
         var13.initialize(this.theWorldSettings);
         Integer[] var14 = (Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs);
         Integer[] var15 = var14;
         int var16 = var14.length;

         for (int var17 = 0; var17 < var16; var17++) {
            int var18 = var15[var17];
            WorldServer var19 = var18 == 0 ? var13 : (WorldServer)new WorldServerMulti(this, var11, var18, var13, this.theProfiler).init();
            var19.addWorldAccess(new WorldManager(this, var19));
            if (!this.isSinglePlayer()) {
               var19.getWorldInfo().setGameType(this.getGameType());
            }

            if (Reflector.EventBus.exists()) {
               Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, var19);
            }
         }

         this.getConfigurationManager().setPlayerManager(new WorldServer[]{var13});
         if (var13.getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      } else {
         this.worldServers = new WorldServer[3];
         this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
         this.setResourcePackFromWorld(this.getFolderName(), var11);
         if (var12 == null) {
            var12 = new WorldInfo(this.theWorldSettings, var2);
         } else {
            var12.setWorldName(var2);
         }

         for (int var20 = 0; var20 < this.worldServers.length; var20++) {
            byte var21 = 0;
            if (var20 == 1) {
               var21 = -1;
            }

            if (var20 == 2) {
               var21 = 1;
            }

            if (var20 == 0) {
               if (this.isDemo()) {
                  this.worldServers[var20] = (WorldServer)new DemoWorldServer(this, var11, var12, var21, this.theProfiler).init();
               } else {
                  this.worldServers[var20] = (WorldServer)new WorldServerOF(this, var11, var12, var21, this.theProfiler).init();
               }

               this.worldServers[var20].initialize(this.theWorldSettings);
            } else {
               this.worldServers[var20] = (WorldServer)new WorldServerMulti(this, var11, var21, this.worldServers[0], this.theProfiler).init();
            }

            this.worldServers[var20].addWorldAccess(new WorldManager(this, this.worldServers[var20]));
         }

         this.getConfigurationManager().setPlayerManager(this.worldServers);
         if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      }

      this.initialWorldChunkLoad();
   }

   @Override
   public boolean startServer() throws IOException {
      logger.info("Starting integrated minecraft server version 1.8.8");
      this.L(true);
      this.b(true);
      this.i(true);
      this.v(true);
      this.E(true);
      logger.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists()) {
         Object var4 = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (!Reflector.callBoolean(var4, Reflector.FMLCommonHandler_handleServerAboutToStart, this)) {
            return false;
         }
      }

      this.loadAllWorlds(
         this.getFolderName(), this.l(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.getWorldName()
      );
      this.G(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
      if (Reflector.FMLCommonHandler_handleServerStarting.exists()) {
         Object var5 = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == boolean.class) {
            return Reflector.callBoolean(var5, Reflector.FMLCommonHandler_handleServerStarting, this);
         }

         Reflector.callVoid(var5, Reflector.FMLCommonHandler_handleServerStarting, this);
      }

      return true;
   }

   @Override
   public void tick() {
      boolean var4 = this.isGamePaused;
      this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
      if (!var4 && this.isGamePaused) {
         logger.info("Saving and pausing game...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      if (this.isGamePaused) {
         Queue var6 = this.futureTaskQueue;
         synchronized (this.futureTaskQueue) {
            while (!this.futureTaskQueue.isEmpty()) {
               Util.runTask(this.futureTaskQueue.poll(), logger);
            }
         }
      } else {
         super.tick();
         if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
            logger.info("Changing view distance to {}, from {}", this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance());
            this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
         }

         if (this.mc.theWorld != null) {
            WorldInfo var12 = this.worldServers[0].getWorldInfo();
            WorldInfo var13 = this.mc.theWorld.getWorldInfo();
            if (!var12.isDifficultyLocked() && var13.getDifficulty() != var12.getDifficulty()) {
               logger.info("Changing difficulty to {}, from {}", var13.getDifficulty(), var12.getDifficulty());
               this.setDifficultyForAllWorlds(var13.getDifficulty());
            } else if (var13.isDifficultyLocked() && !var12.isDifficultyLocked()) {
               logger.info("Locking difficulty to {}", new Object[]{var13.getDifficulty()});

               for (WorldServer var10 : this.worldServers) {
                  if (var10 != null) {
                     var10.getWorldInfo().setDifficultyLocked(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean canStructuresSpawn() {
      return false;
   }

   @Override
   public WorldSettings.GameType getGameType() {
      return this.theWorldSettings.getGameType();
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return this.mc.theWorld == null ? this.mc.gameSettings.difficulty : this.mc.theWorld.getWorldInfo().getDifficulty();
   }

   @Override
   public boolean isHardcore() {
      return this.theWorldSettings.getHardcoreEnabled();
   }

   @Override
   public boolean shouldBroadcastRconToOps() {
      return true;
   }

   @Override
   public boolean shouldBroadcastConsoleToOps() {
      return true;
   }

   @Override
   public File getDataDirectory() {
      return this.mc.mcDataDir;
   }

   @Override
   public boolean shouldUseNativeTransport() {
      return false;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public void finalTick(CrashReport var1) {
      this.mc.crashed(var1);
   }

   @Override
   public CrashReport addServerInfoToCrashReport(CrashReport var1) {
      var1 = super.addServerInfoToCrashReport(var1);
      var1.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         private static final long a = 88884208460044L;

         public String call() throws Exception {
            return "Integrated Server (map_client.txt)";
         }
      });
      var1.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               private static final long a = 95120827268702L;

               public String call() throws Exception {
                  String var4 = ClientBrandRetriever.getClientModName();
                  if (!var4.equals("vanilla")) {
                     return "Definitely; Client brand changed to '" + var4 + "'";
                  }

                  var4 = IntegratedServer.this.getServerModName();
                  return !var4.equals("vanilla")
                     ? "Definitely; Server brand changed to '" + var4 + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and both client + server brands are untouched."
                     );
               }
            }
         );
      return var1;
   }

   @Override
   public void setDifficultyForAllWorlds(EnumDifficulty var1) {
      super.setDifficultyForAllWorlds(var1);
      if (this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficulty(var1);
      }
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper var1) {
      super.addServerStatsToSnooper(var1);
      var1.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
   }

   @Override
   public boolean isSnooperEnabled() {
      return Minecraft.getMinecraft().isSnooperEnabled();
   }

   @Override
   public String shareToLAN(WorldSettings.GameType var1, boolean var2) {
      try {
         int var6 = -1;

         try {
            var6 = HttpUtil.getSuitableLanPort();
         } catch (IOException var8) {
         }

         if (var6 <= 0) {
            var6 = 25564;
         }

         this.getNetworkSystem().addLanEndpoint((InetAddress)null, var6);
         logger.info("Started on " + var6);
         this.isPublic = true;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var6 + "");
         this.lanServerPing.start();
         this.getConfigurationManager().setGameType(var1);
         this.getConfigurationManager().setCommandsAllowedForAll(var2);
         return var6 + "";
      } catch (IOException var9) {
         return null;
      }
   }

   @Override
   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   @Override
   public void initiateShutdown() {
      Futures.getUnchecked(this.addScheduledTask(new Runnable() {
         private static final long a = 46232131130542L;

         @Override
         public void run() {
            for (EntityPlayerMP var5 : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList())) {
               IntegratedServer.this.getConfigurationManager().playerLoggedOut(var5);
            }
         }
      }));
      super.initiateShutdown();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   public void setStaticInstance() {
      this.T();
   }

   public boolean getPublic() {
      return this.isPublic;
   }

   @Override
   public void setGameType(WorldSettings.GameType var1) {
      this.getConfigurationManager().setGameType(var1);
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public int getOpPermissionLevel() {
      return 4;
   }
}
