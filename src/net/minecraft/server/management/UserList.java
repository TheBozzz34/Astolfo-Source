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

   public UserList(File saveFile) {
      this.saveFile = saveFile;
      GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
      gsonBuilder.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
      this.gson = gsonBuilder.create();
   }

   public boolean isLanServer() {
      return this.lanServer;
   }

   public void setLanServer(boolean state) {
      this.lanServer = state;
   }

   public void addEntry(V entry) {
      this.values.put(this.getObjectKey((K)entry.getValue()), (V)entry);

      try {
         this.writeChanges();
      } catch (IOException ioException) {
         logger.warn("Could not save the list after adding a user.", ioException);
      }
   }

   public V getEntry(K obj) {
      this.removeExpired();
      return this.values.get(this.getObjectKey((K)obj));
   }

   public void removeEntry(K entry) {
      this.values.remove(this.getObjectKey((K)entry));

      try {
         this.writeChanges();
      } catch (IOException ioException) {
         logger.warn("Could not save the list after removing a user.", ioException);
      }
   }

   public String[] getKeys() {
      return this.values.keySet().toArray(new String[this.values.size()]);
   }

   public String getObjectKey(K obj) {
      return obj.toString();
   }

   public boolean hasEntry(K entry) {
      return this.values.containsKey(this.getObjectKey((K)entry));
   }

   private void removeExpired() {
      ArrayList arrayList = Lists.newArrayList();

      for (UserListEntry userListEntry : this.values.values()) {
         if (userListEntry.hasBanExpired()) {
            arrayList.add(userListEntry.getValue());
         }
      }

      for (Object object : arrayList) {
         this.values.remove(object);
      }
   }

   public UserListEntry<K> createEntry(JsonObject entryData) {
      return new UserListEntry<>(null, entryData);
   }

   public Map<String, V> getValues() {
      return this.values;
   }

   public void writeChanges() throws IOException {
      Collection collection = this.values.values();
      String string = this.gson.toJson(collection);
      BufferedWriter bufferedWriter = null;

      try {
         bufferedWriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
         bufferedWriter.write(string);
      } finally {
         IOUtils.closeQuietly(bufferedWriter);
      }
   }

   public class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
      public Serializer() {
      }

      public JsonElement serialize(UserListEntry<K> userListEntry, Type type2, JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();
         userListEntry.onSerialization(jsonObject);
         return jsonObject;
      }

      public UserListEntry<K> deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return UserList.this.createEntry(jsonObject);
         } else {
            return null;
         }
      }
   }
}
