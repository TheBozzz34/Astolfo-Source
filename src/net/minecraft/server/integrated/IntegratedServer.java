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

   public IntegratedServer(Minecraft mcIn) {
      super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.mc = mcIn;
      this.theWorldSettings = null;
   }

   public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings) {
      super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.setServerOwner(mcIn.getSession().getUsername());
      this.setFolderName(folderName);
      this.setWorldName(worldName);
      this.setDemo(mcIn.isDemo());
      this.canCreateBonusChest(settings.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setConfigManager(new IntegratedPlayerList(this));
      this.mc = mcIn;
      this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
   }

   @Override
   public ServerCommandManager createNewCommandManager() {
      return new IntegratedServerCommandManager();
   }

   @Override
   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType typeIn, String worldNameIn2) {
      this.convertMapIfNeeded(saveName);
      ISaveHandler iSaveHandler = this.getActiveAnvilConverter().getSaveLoader(saveName, true);
      this.setResourcePackFromWorld(this.getFolderName(), iSaveHandler);
      WorldInfo worldInfo = iSaveHandler.loadWorldInfo();
      if (Reflector.DimensionManager.exists()) {
         WorldServer worldServer = this.isDemo()
            ? (WorldServer)new DemoWorldServer(this, iSaveHandler, worldInfo, 0, this.theProfiler).init()
            : (WorldServer)new WorldServerOF(this, iSaveHandler, worldInfo, 0, this.theProfiler).init();
         worldServer.initialize(this.theWorldSettings);
         Integer[] integers = (Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs);
         Integer[] integers2 = integers;
         int i = integers.length;

         for (int j = 0; j < i; j++) {
            int k = integers2[j];
            WorldServer worldServer2 = k == 0 ? worldServer : (WorldServer)new WorldServerMulti(this, iSaveHandler, k, worldServer, this.theProfiler).init();
            worldServer2.addWorldAccess(new WorldManager(this, worldServer2));
            if (!this.isSinglePlayer()) {
               worldServer2.getWorldInfo().setGameType(this.getGameType());
            }

            if (Reflector.EventBus.exists()) {
               Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, worldServer2);
            }
         }

         this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldServer});
         if (worldServer.getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      } else {
         this.worldServers = new WorldServer[3];
         this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
         this.setResourcePackFromWorld(this.getFolderName(), iSaveHandler);
         if (worldInfo == null) {
            worldInfo = new WorldInfo(this.theWorldSettings, worldNameIn);
         } else {
            worldInfo.setWorldName(worldNameIn);
         }

         for (int l = 0; l < this.worldServers.length; l++) {
            byte b = 0;
            if (l == 1) {
               b = -1;
            }

            if (l == 2) {
               b = 1;
            }

            if (l == 0) {
               if (this.isDemo()) {
                  this.worldServers[l] = (WorldServer)new DemoWorldServer(this, iSaveHandler, worldInfo, b, this.theProfiler).init();
               } else {
                  this.worldServers[l] = (WorldServer)new WorldServerOF(this, iSaveHandler, worldInfo, b, this.theProfiler).init();
               }

               this.worldServers[l].initialize(this.theWorldSettings);
            } else {
               this.worldServers[l] = (WorldServer)new WorldServerMulti(this, iSaveHandler, b, this.worldServers[0], this.theProfiler).init();
            }

            this.worldServers[l].addWorldAccess(new WorldManager(this, this.worldServers[l]));
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
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      logger.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists()) {
         Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (!Reflector.callBoolean(object, Reflector.FMLCommonHandler_handleServerAboutToStart, this)) {
            return false;
         }
      }

      this.loadAllWorlds(
         this.getFolderName(),
         this.getWorldName(),
         this.theWorldSettings.getSeed(),
         this.theWorldSettings.getTerrainType(),
         this.theWorldSettings.getWorldName()
      );
      this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
      if (Reflector.FMLCommonHandler_handleServerStarting.exists()) {
         Object object2 = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == boolean.class) {
            return Reflector.callBoolean(object2, Reflector.FMLCommonHandler_handleServerStarting, this);
         }

         Reflector.callVoid(object2, Reflector.FMLCommonHandler_handleServerStarting, this);
      }

      return true;
   }

   @Override
   public void tick() {
      boolean bl = this.isGamePaused;
      this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
      if (!bl && this.isGamePaused) {
         logger.info("Saving and pausing game...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      if (this.isGamePaused) {
         Queue queue = this.futureTaskQueue;
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
            WorldInfo worldInfo = this.worldServers[0].getWorldInfo();
            WorldInfo worldInfo2 = this.mc.theWorld.getWorldInfo();
            if (!worldInfo.isDifficultyLocked() && worldInfo2.getDifficulty() != worldInfo.getDifficulty()) {
               logger.info("Changing difficulty to {}, from {}", worldInfo2.getDifficulty(), worldInfo.getDifficulty());
               this.setDifficultyForAllWorlds(worldInfo2.getDifficulty());
            } else if (worldInfo2.isDifficultyLocked() && !worldInfo.isDifficultyLocked()) {
               logger.info("Locking difficulty to {}", new Object[]{worldInfo2.getDifficulty()});

               for (WorldServer worldServer : this.worldServers) {
                  if (worldServer != null) {
                     worldServer.getWorldInfo().setDifficultyLocked(true);
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
   public void finalTick(CrashReport report) {
      this.mc.crashed(report);
   }

   @Override
   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report = super.addServerInfoToCrashReport(report);
      report.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Integrated Server (map_client.txt)";
         }
      });
      report.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String string = ClientBrandRetriever.getClientModName();
                  if (!string.equals("vanilla")) {
                     return "Definitely; Client brand changed to '" + string + "'";
                  }

                  string = IntegratedServer.this.getServerModName();
                  return !string.equals("vanilla")
                     ? "Definitely; Server brand changed to '" + string + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and both client + server brands are untouched."
                     );
               }
            }
         );
      return report;
   }

   @Override
   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      super.setDifficultyForAllWorlds(difficulty);
      if (this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
      }
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
      super.addServerStatsToSnooper(playerSnooper);
      playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
   }

   @Override
   public boolean isSnooperEnabled() {
      return Minecraft.getMinecraft().isSnooperEnabled();
   }

   @Override
   public String shareToLAN(WorldSettings.GameType typeIn, boolean allowCheats) {
      try {
         int i = -1;

         try {
            i = HttpUtil.getSuitableLanPort();
         } catch (IOException ioException) {
         }

         if (i <= 0) {
            i = 25564;
         }

         this.getNetworkSystem().addLanEndpoint((InetAddress)null, i);
         logger.info("Started on " + i);
         this.isPublic = true;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), i + "");
         this.lanServerPing.start();
         this.getConfigurationManager().setGameType(typeIn);
         this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
         return i + "";
      } catch (IOException ioException2) {
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
         @Override
         public void run() {
            for (EntityPlayerMP entityPlayerMP : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList())) {
               IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityPlayerMP);
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
      this.setInstance();
   }

   public boolean getPublic() {
      return this.isPublic;
   }

   @Override
   public void setGameType(WorldSettings.GameType gameMode) {
      this.getConfigurationManager().setGameType(gameMode);
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
