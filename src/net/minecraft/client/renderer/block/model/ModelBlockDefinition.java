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

   public static ModelBlockDefinition parseFromReader(Reader reader) {
      return GSON.fromJson(reader, ModelBlockDefinition.class);
   }

   public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> collection) {
      for (ModelBlockDefinition.Variants variants : collection) {
         this.mapVariants.put(variants.name, variants);
      }
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> list) {
      for (ModelBlockDefinition modelBlockDefinition : list) {
         this.mapVariants.putAll(modelBlockDefinition.mapVariants);
      }
   }

   public ModelBlockDefinition.Variants getVariants(String string) {
      ModelBlockDefinition.Variants variants = this.mapVariants.get(string);
      if (variants == null) {
         throw new ModelBlockDefinition.MissingVariantException();
      } else {
         return variants;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object instanceof ModelBlockDefinition) {
         ModelBlockDefinition modelBlockDefinition = (ModelBlockDefinition)object;
         return this.mapVariants.equals(modelBlockDefinition.mapVariants);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.mapVariants.hashCode();
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      public ModelBlockDefinition deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         List list = this.parseVariantsList(jsonDeserializationContext, jsonObject);
         return new ModelBlockDefinition((Collection<ModelBlockDefinition.Variants>)list);
      }

      public List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "variants");
         ArrayList arrayList = Lists.newArrayList();

         for (Entry entry : jsonObject2.entrySet()) {
            arrayList.add(this.parseVariants(jsonDeserializationContext, entry));
         }

         return arrayList;
      }

      public ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext jsonDeserializationContext, Entry<String, JsonElement> entry) {
         String string = (String)entry.getKey();
         ArrayList arrayList = Lists.newArrayList();
         JsonElement jsonElement = (JsonElement)entry.getValue();
         if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
               arrayList.add((ModelBlockDefinition.Variant)jsonDeserializationContext.deserialize(jsonElement2, ModelBlockDefinition.Variant.class));
            }
         } else {
            arrayList.add((ModelBlockDefinition.Variant)jsonDeserializationContext.deserialize(jsonElement, ModelBlockDefinition.Variant.class));
         }

         return new ModelBlockDefinition.Variants(string, arrayList);
      }
   }

   public class MissingVariantException extends RuntimeException {
   }

   public static class Variant {
      private final ResourceLocation modelLocation;
      private final ModelRotation modelRotation;
      private final boolean uvLock;
      private final int weight;

      public Variant(ResourceLocation modelLocationIn, ModelRotation modelRotationIn, boolean uvLockIn, int weightIn) {
         this.modelLocation = modelLocationIn;
         this.modelRotation = modelRotationIn;
         this.uvLock = uvLockIn;
         this.weight = weightIn;
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
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         }

         if (!(object instanceof ModelBlockDefinition.Variant)) {
            return false;
         }

         ModelBlockDefinition.Variant variant = (ModelBlockDefinition.Variant)object;
         return this.modelLocation.equals(variant.modelLocation) && this.modelRotation == variant.modelRotation && this.uvLock == variant.uvLock;
      }

      @Override
      public int hashCode() {
         int i = this.modelLocation.hashCode();
         i = 31 * i + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
         return 31 * i + (this.uvLock ? 1 : 0);
      }

      public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
         public ModelBlockDefinition.Variant deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String string = this.parseModel(jsonObject);
            ModelRotation modelRotation = this.parseRotation(jsonObject);
            boolean bl = this.parseUvLock(jsonObject);
            int i = this.parseWeight(jsonObject);
            return new ModelBlockDefinition.Variant(this.makeModelLocation(string), modelRotation, bl, i);
         }

         private ResourceLocation makeModelLocation(String string) {
            ResourceLocation resourceLocation = new ResourceLocation(string);
            return new ResourceLocation(resourceLocation.getResourceDomain(), "block/" + resourceLocation.getResourcePath());
         }

         private boolean parseUvLock(JsonObject jsonObject) {
            return JsonUtils.getBoolean(jsonObject, "uvlock", false);
         }

         public ModelRotation parseRotation(JsonObject jsonObject) {
            int i = JsonUtils.getInt(jsonObject, "x", 0);
            int j = JsonUtils.getInt(jsonObject, "y", 0);
            ModelRotation modelRotation = ModelRotation.getModelRotation(i, j);
            if (modelRotation == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
            } else {
               return modelRotation;
            }
         }

         public String parseModel(JsonObject jsonObject) {
            return JsonUtils.getString(jsonObject, "model");
         }

         public int parseWeight(JsonObject jsonObject) {
            return JsonUtils.getInt(jsonObject, "weight", 1);
         }
      }
   }

   public static class Variants {
      private final String name;
      private final List<ModelBlockDefinition.Variant> listVariants;

      public Variants(String nameIn, List<ModelBlockDefinition.Variant> listVariantsIn) {
         this.name = nameIn;
         this.listVariants = listVariantsIn;
      }

      public List<ModelBlockDefinition.Variant> getVariants() {
         return this.listVariants;
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         }

         if (!(object instanceof ModelBlockDefinition.Variants)) {
            return false;
         }

         ModelBlockDefinition.Variants variants = (ModelBlockDefinition.Variants)object;
         return !this.name.equals(variants.name) ? false : this.listVariants.equals(variants.listVariants);
      }

      @Override
      public int hashCode() {
         int i = this.name.hashCode();
         return 31 * i + this.listVariants.hashCode();
      }
   }
}
