package net.hypixel.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.hypixel.api.adapters.BoostersTypeAdapterFactory;
import net.hypixel.api.adapters.DateTimeTypeAdapter;
import net.hypixel.api.adapters.GameTypeTypeAdapter;
import net.hypixel.api.adapters.UUIDTypeAdapter;
import net.hypixel.api.exceptions.APIThrottleException;
import net.hypixel.api.exceptions.HypixelAPIException;
import net.hypixel.api.reply.AbstractReply;
import net.hypixel.api.reply.BoostersReply;
import net.hypixel.api.reply.FindGuildReply;
import net.hypixel.api.reply.FriendsReply;
import net.hypixel.api.reply.GameCountsReply;
import net.hypixel.api.reply.GuildReply;
import net.hypixel.api.reply.KeyReply;
import net.hypixel.api.reply.LeaderboardsReply;
import net.hypixel.api.reply.PlayerCountReply;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.RecentGamesReply;
import net.hypixel.api.reply.StatusReply;
import net.hypixel.api.reply.WatchdogStatsReply;
import net.hypixel.api.reply.skyblock.BazaarReply;
import net.hypixel.api.reply.skyblock.ResourceReply;
import net.hypixel.api.reply.skyblock.SkyBlockAuctionsReply;
import net.hypixel.api.reply.skyblock.SkyBlockNewsReply;
import net.hypixel.api.reply.skyblock.SkyBlockProfileReply;
import net.hypixel.api.util.GameType;
import net.hypixel.api.util.ResourceType;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HypixelAPI {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
      .registerTypeAdapter(GameType.class, new GameTypeTypeAdapter())
      .registerTypeAdapter(ZonedDateTime.class, new DateTimeTypeAdapter())
      .registerTypeAdapterFactory(new BoostersTypeAdapterFactory<>(BoostersReply.Booster.class))
      .create();
   private UUID apiKey;
   public static final ExecutorService H = Executors.newCachedThreadPool();
   private final HttpClient httpClient;
   public static int O = 0;

   public HypixelAPI(UUID var1) {
      this.apiKey = var1;
      this.httpClient = HttpClientBuilder.create().build();
   }

   public void shutdown() {
      H.shutdown();
   }

   public UUID getApiKey() {
      return this.apiKey;
   }

   public void l(UUID var1) {
      this.apiKey = var1;
   }

   public CompletableFuture<BoostersReply> getBoosters() {
      return this.get(BoostersReply.class, "boosters");
   }

   public CompletableFuture<LeaderboardsReply> getLeaderboards() {
      return this.get(LeaderboardsReply.class, "leaderboards");
   }

   public CompletableFuture<WatchdogStatsReply> getWatchdogStats() {
      return this.get(WatchdogStatsReply.class, "watchdogStats");
   }

   @Deprecated
   public CompletableFuture<PlayerCountReply> getPlayerCount() {
      return this.get(PlayerCountReply.class, "playerCount");
   }

   public CompletableFuture<PlayerReply> getPlayerByUuid(UUID var1) {
      return this.get(PlayerReply.class, "player", "uuid", var1);
   }

   public CompletableFuture<PlayerReply> getPlayerByUuid(String var1) {
      return this.get(PlayerReply.class, "player", "uuid", var1);
   }

   @Deprecated
   public CompletableFuture<PlayerReply> getPlayerByName(String var1) {
      return this.get(PlayerReply.class, "player", "name", var1);
   }

   public CompletableFuture<FriendsReply> getFriends(UUID var1) {
      return this.get(FriendsReply.class, "friends", "uuid", var1);
   }

   public CompletableFuture<FriendsReply> getFriends(String var1) {
      return this.get(FriendsReply.class, "friends", "uuid", var1);
   }

   public CompletableFuture<GuildReply> getGuildByPlayer(UUID var1) {
      return this.get(GuildReply.class, "guild", "player", var1);
   }

   public CompletableFuture<GuildReply> getGuildByPlayer(String var1) {
      return this.get(GuildReply.class, "guild", "player", var1);
   }

   public CompletableFuture<GuildReply> getGuildByName(String var1) {
      return this.get(GuildReply.class, "guild", "name", var1);
   }

   public CompletableFuture<GuildReply> getGuildById(String var1) {
      return this.get(GuildReply.class, "guild", "id", var1);
   }

   @Deprecated
   public CompletableFuture<FindGuildReply> findGuildByPlayer(UUID var1) {
      return this.get(FindGuildReply.class, "findGuild", "byUuid", var1);
   }

   @Deprecated
   public CompletableFuture<FindGuildReply> findGuildByPlayer(String var1) {
      return this.get(FindGuildReply.class, "findGuild", "byUuid", var1);
   }

   @Deprecated
   public CompletableFuture<GuildReply> findGuildByName(String var1) {
      return this.get(GuildReply.class, "findGuild", "byName", var1);
   }

   public CompletableFuture<KeyReply> getKey() {
      return this.get(KeyReply.class, "key");
   }

   public CompletableFuture<GameCountsReply> getGameCounts() {
      return this.get(GameCountsReply.class, "gameCounts");
   }

   public CompletableFuture<SkyBlockProfileReply> getSkyBlockProfile(String var1) {
      return this.get(SkyBlockProfileReply.class, "skyblock/profile", "profile", var1);
   }

   public CompletableFuture<SkyBlockNewsReply> getSkyBlockNews() {
      return this.get(SkyBlockNewsReply.class, "skyblock/news");
   }

   public CompletableFuture<SkyBlockAuctionsReply> getSkyBlockAuctions(int var1) {
      return this.get(SkyBlockAuctionsReply.class, "skyblock/auctions", "page", var1);
   }

   public CompletableFuture<StatusReply> getStatus(UUID var1) {
      return this.get(StatusReply.class, "status", "uuid", var1);
   }

   public CompletableFuture<RecentGamesReply> getRecentGames(UUID var1) {
      return this.get(RecentGamesReply.class, "recentGames", "uuid", var1);
   }

   public CompletableFuture<ResourceReply> getResource(ResourceType var1) {
      return this.getResource(var1.getPath());
   }

   public CompletableFuture<BazaarReply> getBazaar() {
      return this.get(BazaarReply.class, "skyblock/bazaar");
   }

   public CompletableFuture<ResourceReply> getResource(String var1) {
      return this.requestResource(var1);
   }

   private <R extends AbstractReply> CompletableFuture<R> get(Class<R> var1, String var2, Object... var3) {
      CompletableFuture var4 = new CompletableFuture();
      O++;

      try {
         if (var3.length % 2 != 0) {
            throw new IllegalArgumentException("Need both key and value for parameters");
         }

         StringBuilder var5 = new StringBuilder("https://api.hypixel.net/");
         var5.append(var2);
         var5.append("?key=").append(this.apiKey);

         for (int var6 = 0; var6 < var3.length - 1; var6 += 2) {
            var5.append("&").append(var3[var6]).append("=").append(var3[var6 + 1]);
         }

         H.submit(() -> {
            try {
               AbstractReply var4x = this.httpClient.execute(new HttpGet(var5.toString()), var1xx -> {
                  String var2xx = EntityUtils.toString(var1xx.getEntity(), Charset.defaultCharset());
                  return var1 == ResourceReply.class ? new ResourceReply(GSON.fromJson(var2xx, JsonObject.class)) : GSON.fromJson(var2xx, var1);
               });
               this.checkReply(var4x);
               var4.complete(var4x);
               O--;
            } catch (Throwable var5x) {
               var4.completeExceptionally(var5x);
               O--;
            }
         });
      } catch (Throwable var7) {
         O--;
         var4.completeExceptionally(var7);
      }

      return var4;
   }

   private CompletableFuture<ResourceReply> requestResource(String var1) {
      CompletableFuture var2 = new CompletableFuture();

      try {
         StringBuilder var3 = new StringBuilder("https://api.hypixel.net/");
         var3.append("resources/").append(var1);
         H.submit(() -> {
            try {
               ResourceReply var3x = this.httpClient.execute(new HttpGet(var3.toString()), var0 -> {
                  String var1xx = EntityUtils.toString(var0.getEntity(), "UTF-8");
                  return new ResourceReply(GSON.fromJson(var1xx, JsonObject.class));
               });
               this.checkReply(var3x);
               var2.complete(var3x);
            } catch (Throwable var4x) {
               var2.completeExceptionally(var4x);
            }
         });
      } catch (Throwable var4) {
         var2.completeExceptionally(var4);
      }

      return var2;
   }

   private <T extends AbstractReply> void checkReply(T var1) {
      if (var1 != null) {
         if (var1.isThrottle()) {
            throw new APIThrottleException();
         }

         if (!var1.isSuccess()) {
            throw new HypixelAPIException(var1.getCause());
         }
      }
   }
}
