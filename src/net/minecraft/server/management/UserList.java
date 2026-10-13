package net.minecraft.server.management;

import com.google.common.base.Charsets;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>> {
   public static final Logger logger = LogManager.getLogger();
   public final Gson gson;
   private final File saveFile;
   private final Map<String, V> values = Maps.newHashMap();
   private boolean lanServer = true;
   private static final ParameterizedType saveFileFormat = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{UserListEntry.class};
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
   private static final long b = 69898675378643L;

   public UserList(File var1) {
      this.saveFile = var1;
      GsonBuilder var2 = new GsonBuilder().setPrettyPrinting();
      var2.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
      this.gson = var2.create();
   }

   public boolean isLanServer() {
      return this.lanServer;
   }

   public void setLanServer(boolean var1) {
      this.lanServer = var1;
   }

   public void addEntry(V var1) {
      this.values.put(this.getObjectKey((K)var1.getValue()), (V)var1);

      try {
         this.writeChanges();
      } catch (IOException var5) {
         logger.warn("Could not save the list after adding a user.", var5);
      }
   }

   public V getEntry(K var1) {
      this.removeExpired();
      return this.values.get(this.getObjectKey((K)var1));
   }

   public void removeEntry(K var1) {
      this.values.remove(this.getObjectKey((K)var1));

      try {
         this.writeChanges();
      } catch (IOException var5) {
         logger.warn("Could not save the list after removing a user.", var5);
      }
   }

   public String[] getKeys() {
      return this.values.keySet().toArray(new String[this.values.size()]);
   }

   public String getObjectKey(K var1) {
      return var1.toString();
   }

   public boolean hasEntry(K var1) {
      return this.values.containsKey(this.getObjectKey((K)var1));
   }

   private void removeExpired() {
      ArrayList var4 = Lists.newArrayList();

      for (UserListEntry var6 : this.values.values()) {
         if (var6.hasBanExpired()) {
            var4.add(var6.getValue());
         }
      }

      for (Object var8 : var4) {
         this.values.remove(var8);
      }
   }

   public UserListEntry<K> createEntry(JsonObject var1) {
      return new UserListEntry<>(null, var1);
   }

   public Map<String, V> getValues() {
      return this.values;
   }

   public void writeChanges() throws IOException {
      Collection var1 = this.values.values();
      String var2 = this.gson.toJson(var1);
      BufferedWriter var3 = null;

      try {
         var3 = Files.newWriter(this.saveFile, Charsets.UTF_8);
         var3.write(var2);
      } finally {
         IOUtils.closeQuietly(var3);
      }
   }

   public class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
      private static final long a = 81462614238111L;

      public Serializer() {
      }

      public JsonElement serialize(UserListEntry<K> var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var1.onSerialization(var4);
         return var4;
      }

      public UserListEntry<K> deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var7 = var1.getAsJsonObject();
            return UserList.this.createEntry(var7);
         } else {
            return null;
         }
      }
   }
}
