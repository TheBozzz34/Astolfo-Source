package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;

public class IMetadataSerializer {
   private final IRegistry<String, IMetadataSerializer.Registration<? extends IMetadataSection>> metadataSectionSerializerRegistry = new RegistrySimple<>();
   private final GsonBuilder gsonBuilder = new GsonBuilder();
   private Gson gson;
   private static final long a = 63489025071718L;

   public IMetadataSerializer() {
      this.gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
      this.gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
      this.gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
   }

   public <T extends IMetadataSection> void registerMetadataSectionType(IMetadataSectionSerializer<T> var1, Class<T> var2) {
      this.metadataSectionSerializerRegistry.putObject(var1.getSectionName(), new IMetadataSerializer.Registration<>(var1, var2));
      this.gsonBuilder.registerTypeAdapter(var2, var1);
      this.gson = null;
   }

   public <T extends IMetadataSection> T parseMetadataSection(String var1, JsonObject var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Metadata section name cannot be null");
      } else if (!var2.has(var1)) {
         return null;
      } else if (!var2.get(var1).isJsonObject()) {
         throw new IllegalArgumentException("Invalid metadata for '" + var1 + "' - expected object, found " + var2.get(var1));
      } else {
         IMetadataSerializer.Registration var6 = this.metadataSectionSerializerRegistry.getObject(var1);
         if (var6 == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + var1 + "'");
         } else {
            return (T)(this.getGson().fromJson(var2.getAsJsonObject(var1), var6.clazz));
         }
      }
   }

   private Gson getGson() {
      if (this.gson == null) {
         this.gson = this.gsonBuilder.create();
      }

      return this.gson;
   }

   public class Registration<T extends IMetadataSection> {
      public final IMetadataSectionSerializer<T> section;
      public final Class<T> clazz;

      public Registration(IMetadataSectionSerializer<T> var2, Class<T> var3) {
         this.section = var2;
         this.clazz = var3;
      }
   }
}
