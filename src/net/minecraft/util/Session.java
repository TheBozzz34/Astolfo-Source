package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;

public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;
   private static final long a = 4815386971131L;

   public Session(String var1, String var2, String var3, String var4) {
      this.username = var1;
      this.playerID = var2;
      this.token = var3;
      this.sessionType = Session.Type.setSessionType(var4);
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public String getUsername() {
      return this.username;
   }

   public String getToken() {
      return this.token;
   }

   public GameProfile getProfile() {
      try {
         UUID var1 = UUIDTypeAdapter.fromString(this.getPlayerID());
         return new GameProfile(var1, this.getUsername());
      } catch (IllegalArgumentException var2) {
         return new GameProfile((UUID)null, this.getUsername());
      }
   }

   public Session.Type getSessionType() {
      return this.sessionType;
   }

   public enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> SESSION_TYPES = Maps.newHashMap();
      private final String sessionType;

      Type(String var3) {
         this.sessionType = var3;
      }

      public static Session.Type setSessionType(String var0) {
         return SESSION_TYPES.get(var0.toLowerCase());
      }

      static {
         for (Session.Type var14 : values()) {
            SESSION_TYPES.put(var14.sessionType, var14);
         }
      }
   }
}
