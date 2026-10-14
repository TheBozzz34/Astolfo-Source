package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
   public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
   private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
   private final LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
   private final MinecraftServer mcServer;
   public final Gson gson;
   private final File usercacheFile;
   private static final ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerProfileCache.ProfileEntry.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };

   public PlayerProfileCache(MinecraftServer server, File cacheFile) {
      this.mcServer = server;
      this.usercacheFile = cacheFile;
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.gson = gsonBuilder.create();
      this.load();
   }

   private static GameProfile getGameProfile(MinecraftServer server, String username) {
      final GameProfile[] gameProfiles = new GameProfile[1];
      ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
         @Override
         public void onProfileLookupSucceeded(GameProfile gameProfile) {
            gameProfiles[0] = gameProfile;
         }

         @Override
         public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
            gameProfiles[0] = null;
         }
      };
      server.getGameProfileRepository().findProfilesByNames(new String[]{username}, Agent.MINECRAFT, profileLookupCallback);
      if (!server.isServerInOnlineMode() && gameProfiles[0] == null) {
         UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, username));
         GameProfile gameProfile2 = new GameProfile(uuid, username);
         profileLookupCallback.onProfileLookupSucceeded(gameProfile2);
      }

      return gameProfiles[0];
   }

   public void addEntry(GameProfile gameProfile) {
      this.addEntry(gameProfile, (Date)null);
   }

   private void addEntry(GameProfile gameProfile, Date expirationDate) {
      UUID uuid = gameProfile.getId();
      if (expirationDate == null) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         calendar.add(2, 1);
         expirationDate = calendar.getTime();
      }

      String string = gameProfile.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry profileEntry = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);
      if (this.uuidToProfileEntryMap.containsKey(uuid)) {
         PlayerProfileCache.ProfileEntry profileEntry2 = this.uuidToProfileEntryMap.get(uuid);
         this.usernameToProfileEntryMap.remove(profileEntry2.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(gameProfile);
      }

      this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), profileEntry);
      this.uuidToProfileEntryMap.put(uuid, profileEntry);
      this.gameProfiles.addFirst(gameProfile);
      this.save();
   }

   public GameProfile getGameProfileForUsername(String username) {
      String string = username.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry profileEntry = this.usernameToProfileEntryMap.get(string);
      if (profileEntry != null && new Date().getTime() >= profileEntry.expirationDate.getTime()) {
         this.uuidToProfileEntryMap.remove(profileEntry.getGameProfile().getId());
         this.usernameToProfileEntryMap.remove(profileEntry.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(profileEntry.getGameProfile());
         profileEntry = null;
      }

      if (profileEntry != null) {
         GameProfile gameProfile = profileEntry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      } else {
         GameProfile gameProfile2 = getGameProfile(this.mcServer, string);
         if (gameProfile2 != null) {
            this.addEntry(gameProfile2);
            profileEntry = this.usernameToProfileEntryMap.get(string);
         }
      }

      this.save();
      return profileEntry == null ? null : profileEntry.getGameProfile();
   }

   public String[] getUsernames() {
      ArrayList arrayList = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   public GameProfile getProfileByUUID(UUID uuid) {
      PlayerProfileCache.ProfileEntry profileEntry = this.uuidToProfileEntryMap.get(uuid);
      return profileEntry == null ? null : profileEntry.getGameProfile();
   }

   private PlayerProfileCache.ProfileEntry getByUUID(UUID uuid) {
      PlayerProfileCache.ProfileEntry profileEntry = this.uuidToProfileEntryMap.get(uuid);
      if (profileEntry != null) {
         GameProfile gameProfile = profileEntry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      }

      return profileEntry;
   }

   public void load() {
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = Files.newReader(this.usercacheFile, Charsets.UTF_8);
         List list = this.gson.fromJson(bufferedReader, TYPE);
         this.usernameToProfileEntryMap.clear();
         this.uuidToProfileEntryMap.clear();
         this.gameProfiles.clear();

         for (Object object : Lists.reverse(list)) {
            PlayerProfileCache.ProfileEntry profileEntry = (PlayerProfileCache.ProfileEntry)object;
            if (profileEntry != null) {
               this.addEntry(profileEntry.getGameProfile(), profileEntry.getExpirationDate());
            }
         }
      } catch (FileNotFoundException fileNotFoundException) {
      } catch (JsonParseException jsonParseException) {
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }
   }

   public void save() {
      String string = this.gson.toJson(this.getEntriesWithLimit(1000));
      BufferedWriter bufferedWriter = null;

      try {
         bufferedWriter = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
         bufferedWriter.write(string);
         return;
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
      } finally {
         IOUtils.closeQuietly(bufferedWriter);
      }
   }

   private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int limitSize) {
      ArrayList arrayList = Lists.newArrayList();

      for (GameProfile gameProfile : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize))) {
         PlayerProfileCache.ProfileEntry profileEntry = this.getByUUID(gameProfile.getId());
         if (profileEntry != null) {
            arrayList.add(profileEntry);
         }
      }

      return arrayList;
   }

   public class ProfileEntry {
      private final GameProfile gameProfile;
      private final Date expirationDate;

      public ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
         this.gameProfile = gameProfileIn;
         this.expirationDate = expirationDateIn;
      }

      public GameProfile getGameProfile() {
         return this.gameProfile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }
   }

   public class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      public Serializer() {
      }

      public JsonElement serialize(PlayerProfileCache.ProfileEntry profileEntry, Type type2, JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("name", profileEntry.getGameProfile().getName());
         UUID uuid2 = profileEntry.getGameProfile().getId();
         jsonObject.addProperty("uuid", uuid2 == null ? "" : uuid2.toString());
         jsonObject.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(profileEntry.getExpirationDate()));
         return jsonObject;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement2 = jsonObject.get("name");
            JsonElement jsonElement3 = jsonObject.get("uuid");
            JsonElement jsonElement4 = jsonObject.get("expiresOn");
            if (jsonElement2 != null && jsonElement3 != null) {
               String string = jsonElement3.getAsString();
               String string2 = jsonElement2.getAsString();
               Date date = null;
               if (jsonElement4 != null) {
                  try {
                     date = PlayerProfileCache.dateFormat.parse(jsonElement4.getAsString());
                  } catch (ParseException parseException) {
                     date = null;
                  }
               }

               if (string2 != null && string != null) {
                  UUID uuid2;
                  try {
                     uuid2 = UUID.fromString(string);
                  } catch (Throwable throwable) {
                     return null;
                  }

                  return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid2, string2), date);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
