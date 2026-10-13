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
   private static final long a = 117735158217375L;
   private static final long b = -5843857787133623320L;

   public PlayerProfileCache(MinecraftServer var1, File var2) {
      this.mcServer = var1;
      this.usercacheFile = var2;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.gson = var3.create();
      this.load();
   }

   private static GameProfile getGameProfile(MinecraftServer var0, String var1) {
      final GameProfile[] var5 = new GameProfile[1];
      ProfileLookupCallback var6 = new ProfileLookupCallback() {
         @Override
         public void onProfileLookupSucceeded(GameProfile var1) {
            var5[0] = var1;
         }

         @Override
         public void onProfileLookupFailed(GameProfile var1, Exception var2) {
            var5[0] = null;
         }
      };
      var0.getGameProfileRepository().findProfilesByNames(new String[]{var1}, Agent.MINECRAFT, var6);
      if (!var0.isServerInOnlineMode() && var5[0] == null) {
         UUID var7 = EntityPlayer.getUUID(new GameProfile((UUID)null, var1));
         GameProfile var8 = new GameProfile(var7, var1);
         var6.onProfileLookupSucceeded(var8);
      }

      return var5[0];
   }

   public void addEntry(GameProfile var1) {
      this.addEntry(var1, (Date)null);
   }

   private void addEntry(GameProfile var1, Date var2) {
      UUID var6 = var1.getId();
      if (var2 == null) {
         Calendar var7 = Calendar.getInstance();
         var7.setTime(new Date());
         var7.add(2, 1);
         var2 = var7.getTime();
      }

      String var10 = var1.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry var8 = new PlayerProfileCache.ProfileEntry(var1, var2);
      if (this.uuidToProfileEntryMap.containsKey(var6)) {
         PlayerProfileCache.ProfileEntry var9 = this.uuidToProfileEntryMap.get(var6);
         this.usernameToProfileEntryMap.remove(var9.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(var1);
      }

      this.usernameToProfileEntryMap.put(var1.getName().toLowerCase(Locale.ROOT), var8);
      this.uuidToProfileEntryMap.put(var6, var8);
      this.gameProfiles.addFirst(var1);
      this.save();
   }

   public GameProfile getGameProfileForUsername(String var1) {
      String var5 = var1.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry var6 = this.usernameToProfileEntryMap.get(var5);
      if (var6 != null && new Date().getTime() >= var6.expirationDate.getTime()) {
         this.uuidToProfileEntryMap.remove(var6.getGameProfile().getId());
         this.usernameToProfileEntryMap.remove(var6.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(var6.getGameProfile());
         var6 = null;
      }

      if (var6 != null) {
         GameProfile var7 = var6.getGameProfile();
         this.gameProfiles.remove(var7);
         this.gameProfiles.addFirst(var7);
      } else {
         GameProfile var8 = getGameProfile(this.mcServer, var5);
         if (var8 != null) {
            this.addEntry(var8);
            var6 = this.usernameToProfileEntryMap.get(var5);
         }
      }

      this.save();
      return var6 == null ? null : var6.getGameProfile();
   }

   public String[] getUsernames() {
      ArrayList var1 = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
      return (java.lang.String[])(var1.toArray(new String[var1.size()]));
   }

   public GameProfile getProfileByUUID(UUID var1) {
      PlayerProfileCache.ProfileEntry var5 = this.uuidToProfileEntryMap.get(var1);
      return var5 == null ? null : var5.getGameProfile();
   }

   private PlayerProfileCache.ProfileEntry getByUUID(UUID var1) {
      PlayerProfileCache.ProfileEntry var5 = this.uuidToProfileEntryMap.get(var1);
      if (var5 != null) {
         GameProfile var6 = var5.getGameProfile();
         this.gameProfiles.remove(var6);
         this.gameProfiles.addFirst(var6);
      }

      return var5;
   }

   public void load() {
      BufferedReader var4 = null;

      try {
         var4 = Files.newReader(this.usercacheFile, Charsets.UTF_8);
         List var5 = this.gson.fromJson(var4, TYPE);
         this.usernameToProfileEntryMap.clear();
         this.uuidToProfileEntryMap.clear();
         this.gameProfiles.clear();

         for (Object var7$raw : Lists.reverse(var5)) {
            PlayerProfileCache.ProfileEntry var7 = (PlayerProfileCache.ProfileEntry)var7$raw;
            if (var7 != null) {
               this.addEntry(var7.getGameProfile(), var7.getExpirationDate());
            }
         }
      } catch (FileNotFoundException var12) {
      } catch (JsonParseException var13) {
      } finally {
         IOUtils.closeQuietly(var4);
      }
   }

   public void save() {
      String var3 = this.gson.toJson(this.getEntriesWithLimit((int)b));
      BufferedWriter var4 = null;

      try {
         var4 = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
         var4.write(var3);
         return;
      } catch (FileNotFoundException var10) {
         return;
      } catch (IOException var11) {
      } finally {
         IOUtils.closeQuietly(var4);
      }
   }

   private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int var1) {
      ArrayList var5 = Lists.newArrayList();

      for (GameProfile var7 : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), var1))) {
         PlayerProfileCache.ProfileEntry var8 = this.getByUUID(var7.getId());
         if (var8 != null) {
            var5.add(var8);
         }
      }

      return var5;
   }

   public class ProfileEntry {
      private final GameProfile gameProfile;
      private final Date expirationDate;

      public ProfileEntry(GameProfile var2, Date var3) {
         this.gameProfile = var2;
         this.expirationDate = var3;
      }

      public GameProfile getGameProfile() {
         return this.gameProfile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }
   }

   public class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      private static final long a = 96927441437414L;

      public Serializer() {
      }

      public JsonElement serialize(PlayerProfileCache.ProfileEntry var1, Type var2, JsonSerializationContext var3) {
         JsonObject var7 = new JsonObject();
         var7.addProperty("name", var1.getGameProfile().getName());
         UUID var8 = var1.getGameProfile().getId();
         var7.addProperty("uuid", var8 == null ? "" : var8.toString());
         var7.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(var1.getExpirationDate()));
         return var7;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var7 = var1.getAsJsonObject();
            JsonElement var8 = var7.get("name");
            JsonElement var9 = var7.get("uuid");
            JsonElement var10 = var7.get("expiresOn");
            if (var8 != null && var9 != null) {
               String var11 = var9.getAsString();
               String var12 = var8.getAsString();
               Date var13 = null;
               if (var10 != null) {
                  try {
                     var13 = PlayerProfileCache.dateFormat.parse(var10.getAsString());
                  } catch (ParseException var17) {
                     var13 = null;
                  }
               }

               if (var12 != null && var11 != null) {
                  UUID var14;
                  try {
                     var14 = UUID.fromString(var11);
                  } catch (Throwable var16) {
                     return null;
                  }

                  return PlayerProfileCache.this.new ProfileEntry(new GameProfile(var14, var12), var13);
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
