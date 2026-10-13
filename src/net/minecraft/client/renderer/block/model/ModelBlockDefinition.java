package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition {
   public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer())
      .registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer())
      .create();
   private final Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.newHashMap();
   private static final long a = 46443495491751L;

   public static ModelBlockDefinition parseFromReader(Reader var0) {
      return GSON.fromJson(var0, ModelBlockDefinition.class);
   }

   public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> var1) {
      for (ModelBlockDefinition.Variants var6 : var1) {
         this.mapVariants.put(var6.name, var6);
      }
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> var1) {
      for (ModelBlockDefinition var6 : var1) {
         this.mapVariants.putAll(var6.mapVariants);
      }
   }

   public ModelBlockDefinition.Variants getVariants(String var1) {
      ModelBlockDefinition.Variants var5 = this.mapVariants.get(var1);
      if (var5 == null) {
         throw new ModelBlockDefinition.MissingVariantException();
      } else {
         return var5;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ModelBlockDefinition) {
         ModelBlockDefinition var5 = (ModelBlockDefinition)var1;
         return this.mapVariants.equals(var5.mapVariants);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.mapVariants.hashCode();
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      private static final long a = 39663679932867L;

      public ModelBlockDefinition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.parseVariantsList(var3, var4);
         return new ModelBlockDefinition((Collection<ModelBlockDefinition.Variants>)var5);
      }

      public List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext var1, JsonObject var2) {
         JsonObject var6 = JsonUtils.getJsonObject(var2, "variants");
         ArrayList var7 = Lists.newArrayList();

         for (Entry var9 : var6.entrySet()) {
            var7.add(this.parseVariants(var1, var9));
         }

         return var7;
      }

      public ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext var1, Entry<String, JsonElement> var2) {
         String var6 = (String)var2.getKey();
         ArrayList var7 = Lists.newArrayList();
         JsonElement var8 = (JsonElement)var2.getValue();
         if (var8.isJsonArray()) {
            for (JsonElement var10 : var8.getAsJsonArray()) {
               var7.add((ModelBlockDefinition.Variant)var1.deserialize(var10, ModelBlockDefinition.Variant.class));
            }
         } else {
            var7.add((ModelBlockDefinition.Variant)var1.deserialize(var8, ModelBlockDefinition.Variant.class));
         }

         return new ModelBlockDefinition.Variants(var6, var7);
      }
   }

   public class MissingVariantException extends RuntimeException {
   }

   public static class Variant {
      private final ResourceLocation modelLocation;
      private final ModelRotation modelRotation;
      private final boolean uvLock;
      private final int weight;
      private static final long a = 16405235670155L;

      public Variant(ResourceLocation var1, ModelRotation var2, boolean var3, int var4) {
         this.modelLocation = var1;
         this.modelRotation = var2;
         this.uvLock = var3;
         this.weight = var4;
      }

      public ResourceLocation getModelLocation() {
         return this.modelLocation;
      }

      public ModelRotation getRotation() {
         return this.modelRotation;
      }

      public boolean isUvLocked() {
         return this.uvLock;
      }

      public int getWeight() {
         return this.weight;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         }

         if (!(var1 instanceof ModelBlockDefinition.Variant)) {
            return false;
         }

         ModelBlockDefinition.Variant var5 = (ModelBlockDefinition.Variant)var1;
         return this.modelLocation.equals(var5.modelLocation) && this.modelRotation == var5.modelRotation && this.uvLock == var5.uvLock;
      }

      @Override
      public int hashCode() {
         int var4 = this.modelLocation.hashCode();
         var4 = 31 * var4 + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
         return 31 * var4 + (this.uvLock ? 1 : 0);
      }

      public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
         private static final long a = 53774775093472L;

         public ModelBlockDefinition.Variant deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = var1.getAsJsonObject();
            String var5 = this.parseModel(var4);
            ModelRotation var6 = this.parseRotation(var4);
            boolean var7 = this.parseUvLock(var4);
            int var8 = this.parseWeight(var4);
            return new ModelBlockDefinition.Variant(this.makeModelLocation(var5), var6, var7, var8);
         }

         private ResourceLocation makeModelLocation(String var1) {
            ResourceLocation var4 = new ResourceLocation(var1);
            return new ResourceLocation(var4.getResourceDomain(), "block/" + var4.getResourcePath());
         }

         private boolean parseUvLock(JsonObject var1) {
            return JsonUtils.getBoolean(var1, "uvlock", false);
         }

         public ModelRotation parseRotation(JsonObject var1) {
            int var5 = JsonUtils.getInt(var1, "x", 0);
            int var6 = JsonUtils.getInt(var1, "y", 0);
            ModelRotation var7 = ModelRotation.getModelRotation(var5, var6);
            if (var7 == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + var5 + ", y: " + var6);
            } else {
               return var7;
            }
         }

         public String parseModel(JsonObject var1) {
            return JsonUtils.getString(var1, "model");
         }

         public int parseWeight(JsonObject var1) {
            return JsonUtils.getInt(var1, "weight", 1);
         }
      }
   }

   public static class Variants {
      private final String name;
      private final List<ModelBlockDefinition.Variant> listVariants;
      private static final long a = 11347617563970L;
      private static final long b = -5962487558398017505L;

      public Variants(String var1, List<ModelBlockDefinition.Variant> var2) {
         this.name = var1;
         this.listVariants = var2;
      }

      public List<ModelBlockDefinition.Variant> getVariants() {
         return this.listVariants;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         }

         if (!(var1 instanceof ModelBlockDefinition.Variants)) {
            return false;
         }

         ModelBlockDefinition.Variants var5 = (ModelBlockDefinition.Variants)var1;
         return !this.name.equals(var5.name) ? false : this.listVariants.equals(var5.listVariants);
      }

      @Override
      public int hashCode() {
         int var3 = this.name.hashCode();
         return (int)b * var3 + this.listVariants.hashCode();
      }
   }
}
