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
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Gson SERIALIZER = new GsonBuilder()
      .registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer())
      .registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer())
      .registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
      .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
      .registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
      .registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
      .create();
   private final List<BlockPart> elements;
   private final boolean gui3d;
   private final boolean ambientOcclusion;
   private ItemCameraTransforms cameraTransforms;
   public String name = "";
   public final Map<String, String> textures;
   public ModelBlock parent;
   public ResourceLocation parentLocation;
   private static final long a = 17964793217280L;

   public static ModelBlock deserialize(Reader var0) {
      return SERIALIZER.fromJson(var0, ModelBlock.class);
   }

   public static ModelBlock deserialize(String var0) {
      return deserialize(new StringReader(var0));
   }

   public ModelBlock(List<BlockPart> var1, Map<String, String> var2, boolean var3, boolean var4, ItemCameraTransforms var5) {
      this((ResourceLocation)null, var1, var2, var3, var4, var5);
   }

   public ModelBlock(ResourceLocation var1, Map<String, String> var2, boolean var3, boolean var4, ItemCameraTransforms var5) {
      this(var1, Collections.emptyList(), var2, var3, var4, var5);
   }

   private ModelBlock(ResourceLocation var1, List<BlockPart> var2, Map<String, String> var3, boolean var4, boolean var5, ItemCameraTransforms var6) {
      this.elements = var2;
      this.ambientOcclusion = var4;
      this.gui3d = var5;
      this.textures = var3;
      this.parentLocation = var1;
      this.cameraTransforms = var6;
   }

   public List<BlockPart> getElements() {
      return this.hasParent() ? this.parent.getElements() : this.elements;
   }

   private boolean hasParent() {
      return this.parent != null;
   }

   public boolean isAmbientOcclusion() {
      return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isResolved() {
      return this.parentLocation == null || this.parent != null && this.parent.isResolved();
   }

   public void getParentFromMap(Map<ResourceLocation, ModelBlock> var1) {
      if (this.parentLocation != null) {
         this.parent = (ModelBlock)var1.get(this.parentLocation);
      }
   }

   public boolean isTexturePresent(String var1) {
      return !"missingno".equals(this.resolveTextureName(var1));
   }

   public String resolveTextureName(String var1) {
      if (!this.startsWithHash(var1)) {
         var1 = '#' + var1;
      }

      return this.resolveTextureName(var1, new ModelBlock.Bookkeep(this));
   }

   private String resolveTextureName(String var1, ModelBlock.Bookkeep var2) {
      if (this.startsWithHash(var1)) {
         if (this == var2.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: " + var1 + " in " + this.name);
            return "missingno";
         }

         String var6 = this.textures.get(var1.substring(1));
         if (var6 == null && this.hasParent()) {
            var6 = this.parent.resolveTextureName(var1, var2);
         }

         var2.modelExt = this;
         if (var6 != null && this.startsWithHash(var6)) {
            var6 = var2.model.resolveTextureName(var6, var2);
         }

         return var6 != null && !this.startsWithHash(var6) ? var6 : "missingno";
      } else {
         return var1;
      }
   }

   private boolean startsWithHash(String var1) {
      return var1.charAt(0) == '#';
   }

   public ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   public ModelBlock getRootModel() {
      return this.hasParent() ? this.parent.getRootModel() : this;
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f var1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
      ItemTransformVec3f var2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
      ItemTransformVec3f var3 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f var4 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f var5 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f var6 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(var1, var2, var3, var4, var5, var6);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType var1) {
      return this.parent != null && !this.cameraTransforms.func_181687_c(var1) ? this.parent.getTransform(var1) : this.cameraTransforms.getTransform(var1);
   }

   public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> var0) {
      for (ModelBlock var5 : var0.values()) {
         try {
            ModelBlock var6 = var5.parent;

            for (ModelBlock var7 = var6.parent; var6 != var7; var7 = var7.parent.parent) {
               var6 = var6.parent;
            }

            throw new ModelBlock.LoopException();
         } catch (NullPointerException var8) {
         }
      }
   }

   public static final class Bookkeep {
      public final ModelBlock model;
      public ModelBlock modelExt;

      public Bookkeep(ModelBlock var1) {
         this.model = var1;
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      private static final long a = 59645468926705L;

      public ModelBlock deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var7 = var1.getAsJsonObject();
         List var8 = this.getModelElements(var3, var7);
         String var9 = this.getParent(var7);
         boolean var10 = StringUtils.isEmpty(var9);
         boolean var11 = var8.isEmpty();
         if (var11 && var10) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         }

         if (!var10 && !var11) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         }

         Map var12 = this.getTextures(var7);
         boolean var13 = this.getAmbientOcclusionEnabled(var7);
         ItemCameraTransforms var14 = ItemCameraTransforms.DEFAULT;
         if (var7.has("display")) {
            JsonObject var15 = JsonUtils.getJsonObject(var7, "display");
            var14 = var3.deserialize(var15, ItemCameraTransforms.class);
         }

         return var11 ? new ModelBlock(new ResourceLocation(var9), var12, var13, true, var14) : new ModelBlock(var8, var12, var13, true, var14);
      }

      private Map<String, String> getTextures(JsonObject var1) {
         HashMap var5 = Maps.newHashMap();
         if (var1.has("textures")) {
            JsonObject var6 = var1.getAsJsonObject("textures");

            for (Entry var8 : var6.entrySet()) {
               var5.put(var8.getKey(), ((JsonElement)var8.getValue()).getAsString());
            }
         }

         return var5;
      }

      private String getParent(JsonObject var1) {
         return JsonUtils.getString(var1, "parent", "");
      }

      public boolean getAmbientOcclusionEnabled(JsonObject var1) {
         return JsonUtils.getBoolean(var1, "ambientocclusion", true);
      }

      public List<BlockPart> getModelElements(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var6 = Lists.newArrayList();
         if (var2.has("elements")) {
            for (JsonElement var8 : JsonUtils.getJsonArray(var2, "elements")) {
               var6.add((BlockPart)var1.deserialize(var8, BlockPart.class));
            }
         }

         return var6;
      }
   }

   public static class LoopException extends RuntimeException {
   }
}
