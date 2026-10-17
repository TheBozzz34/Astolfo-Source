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
   public static final ExecutorService executorService = Executors.newCachedThreadPool();
   private final HttpClient httpClient;
   public static int pendingRequests = 0;

   public HypixelAPI(UUID apiKey) {
      this.apiKey = apiKey;
      this.httpClient = HttpClientBuilder.create().build();
   }

   public void shutdown() {
      executorService.shutdown();
   }

   public UUID getApiKey() {
      return this.apiKey;
   }

   public void setApiKey(UUID uuid) {
      this.apiKey = uuid;
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

   public CompletableFuture<PlayerReply> getPlayerByUuid(UUID player) {
      return this.get(PlayerReply.class, "player", "uuid", player);
   }

   public CompletableFuture<PlayerReply> getPlayerByUuid(String player) {
      return this.get(PlayerReply.class, "player", "uuid", player);
   }

   @Deprecated
   public CompletableFuture<PlayerReply> getPlayerByName(String player) {
      return this.get(PlayerReply.class, "player", "name", player);
   }

   public CompletableFuture<FriendsReply> getFriends(UUID player) {
      return this.get(FriendsReply.class, "friends", "uuid", player);
   }

   public CompletableFuture<FriendsReply> getFriends(String player) {
      return this.get(FriendsReply.class, "friends", "uuid", player);
   }

   public CompletableFuture<GuildReply> getGuildByPlayer(UUID player) {
      return this.get(GuildReply.class, "guild", "player", player);
   }

   public CompletableFuture<GuildReply> getGuildByPlayer(String player) {
      return this.get(GuildReply.class, "guild", "player", player);
   }

   public CompletableFuture<GuildReply> getGuildByName(String name) {
      return this.get(GuildReply.class, "guild", "name", name);
   }

   public CompletableFuture<GuildReply> getGuildById(String id) {
      return this.get(GuildReply.class, "guild", "id", id);
   }

   @Deprecated
   public CompletableFuture<FindGuildReply> findGuildByPlayer(UUID player) {
      return this.get(FindGuildReply.class, "findGuild", "byUuid", player);
   }

   @Deprecated
   public CompletableFuture<FindGuildReply> findGuildByPlayer(String player) {
      return this.get(FindGuildReply.class, "findGuild", "byUuid", player);
   }

   @Deprecated
   public CompletableFuture<GuildReply> findGuildByName(String name) {
      return this.get(GuildReply.class, "findGuild", "byName", name);
   }

   public CompletableFuture<KeyReply> getKey() {
      return this.get(KeyReply.class, "key");
   }

   public CompletableFuture<GameCountsReply> getGameCounts() {
      return this.get(GameCountsReply.class, "gameCounts");
   }

   public CompletableFuture<SkyBlockProfileReply> getSkyBlockProfile(String profile) {
      return this.get(SkyBlockProfileReply.class, "skyblock/profile", "profile", profile);
   }

   public CompletableFuture<SkyBlockNewsReply> getSkyBlockNews() {
      return this.get(SkyBlockNewsReply.class, "skyblock/news");
   }

   public CompletableFuture<SkyBlockAuctionsReply> getSkyBlockAuctions(int page) {
      return this.get(SkyBlockAuctionsReply.class, "skyblock/auctions", "page", page);
   }

   public CompletableFuture<StatusReply> getStatus(UUID uuid) {
      return this.get(StatusReply.class, "status", "uuid", uuid);
   }

   public CompletableFuture<RecentGamesReply> getRecentGames(UUID uuid) {
      return this.get(RecentGamesReply.class, "recentGames", "uuid", uuid);
   }

   public CompletableFuture<ResourceReply> getResource(ResourceType resource) {
      return this.getResource(resource.getPath());
   }

   public CompletableFuture<BazaarReply> getBazaar() {
      return this.get(BazaarReply.class, "skyblock/bazaar");
   }

   public CompletableFuture<ResourceReply> getResource(String resource) {
      return this.requestResource(resource);
   }

   private <R extends AbstractReply> CompletableFuture<R> get(Class<R> clazz, String request, Object... params) {
      CompletableFuture completableFuture = new CompletableFuture();
      pendingRequests++;

      try {
         if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Need both key and value for parameters");
         }

         StringBuilder stringBuilder = new StringBuilder("https://api.hypixel.net/");
         stringBuilder.append(request);
         stringBuilder.append("?key=").append(this.apiKey);

         for (int i = 0; i < params.length - 1; i += 2) {
            stringBuilder.append("&").append(params[i]).append("=").append(params[i + 1]);
         }

         executorService.submit(() -> {
            try {
               AbstractReply abstractReply = this.httpClient.execute(new HttpGet(stringBuilder.toString()), obj -> {
                  String string = EntityUtils.toString(obj.getEntity(), Charset.defaultCharset());
                  return clazz == ResourceReply.class ? new ResourceReply(GSON.fromJson(string, JsonObject.class)) : GSON.fromJson(string, clazz);
               });
               this.checkReply(abstractReply);
               completableFuture.complete(abstractReply);
               pendingRequests--;
            } catch (Throwable throwable) {
               completableFuture.completeExceptionally(throwable);
               pendingRequests--;
            }
         });
      } catch (Throwable throwable2) {
         pendingRequests--;
         completableFuture.completeExceptionally(throwable2);
      }

      return completableFuture;
   }

   private CompletableFuture<ResourceReply> requestResource(String resource) {
      CompletableFuture completableFuture = new CompletableFuture();

      try {
         StringBuilder stringBuilder = new StringBuilder("https://api.hypixel.net/");
         stringBuilder.append("resources/").append(resource);
         executorService.submit(() -> {
            try {
               ResourceReply response = this.httpClient.execute(new HttpGet(stringBuilder.toString()), obj -> {
                  String content = EntityUtils.toString(obj.getEntity(), "UTF-8");
                  return new ResourceReply(GSON.fromJson(content, JsonObject.class));
               });
               this.checkReply(response);
               completableFuture.complete(response);
            } catch (Throwable t) {
               completableFuture.completeExceptionally(t);
            }
         });
      } catch (Throwable throwable) {
         completableFuture.completeExceptionally(throwable);
      }

      return completableFuture;
   }

   private <T extends AbstractReply> void checkReply(T reply) {
      if (reply != null) {
         if (reply.isThrottle()) {
            throw new APIThrottleException();
         }

         if (!reply.isSuccess()) {
            throw new HypixelAPIException(reply.getCause());
         }
      }
   }
}
