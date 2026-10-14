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

   public static ModelBlock deserialize(Reader readerIn) {
      return SERIALIZER.fromJson(readerIn, ModelBlock.class);
   }

   public static ModelBlock deserialize(String jsonString) {
      return deserialize(new StringReader(jsonString));
   }

   public ModelBlock(
      List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this((ResourceLocation)null, elementsIn, texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   public ModelBlock(
      ResourceLocation parentLocationIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this(parentLocationIn, Collections.emptyList(), texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   private ModelBlock(
      ResourceLocation parentLocationIn,
      List<BlockPart> elementsIn,
      Map<String, String> texturesIn,
      boolean ambientOcclusionIn,
      boolean gui3dIn,
      ItemCameraTransforms cameraTransformsIn
   ) {
      this.elements = elementsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.textures = texturesIn;
      this.parentLocation = parentLocationIn;
      this.cameraTransforms = cameraTransformsIn;
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

   public void getParentFromMap(Map<ResourceLocation, ModelBlock> map) {
      if (this.parentLocation != null) {
         this.parent = (ModelBlock)map.get(this.parentLocation);
      }
   }

   public boolean isTexturePresent(String textureName) {
      return !"missingno".equals(this.resolveTextureName(textureName));
   }

   public String resolveTextureName(String textureName) {
      if (!this.startsWithHash(textureName)) {
         textureName = '#' + textureName;
      }

      return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
   }

   private String resolveTextureName(String textureName, ModelBlock.Bookkeep bookkeep) {
      if (this.startsWithHash(textureName)) {
         if (this == bookkeep.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
            return "missingno";
         }

         String string = this.textures.get(textureName.substring(1));
         if (string == null && this.hasParent()) {
            string = this.parent.resolveTextureName(textureName, bookkeep);
         }

         bookkeep.modelExt = this;
         if (string != null && this.startsWithHash(string)) {
            string = bookkeep.model.resolveTextureName(string, bookkeep);
         }

         return string != null && !this.startsWithHash(string) ? string : "missingno";
      } else {
         return textureName;
      }
   }

   private boolean startsWithHash(String hash) {
      return hash.charAt(0) == '#';
   }

   public ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   public ModelBlock getRootModel() {
      return this.hasParent() ? this.parent.getRootModel() : this;
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f itemTransformVec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
      ItemTransformVec3f itemTransformVec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
      ItemTransformVec3f itemTransformVec3f3 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemTransformVec3f4 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemTransformVec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemTransformVec3f6 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(itemTransformVec3f, itemTransformVec3f2, itemTransformVec3f3, itemTransformVec3f4, itemTransformVec3f5, itemTransformVec3f6);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType typeIn) {
      return this.parent != null && !this.cameraTransforms.func_181687_c(typeIn)
         ? this.parent.getTransform(typeIn)
         : this.cameraTransforms.getTransform(typeIn);
   }

   public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> map) {
      for (ModelBlock modelBlock : map.values()) {
         try {
            ModelBlock modelBlock2 = modelBlock.parent;

            for (ModelBlock modelBlock3 = modelBlock2.parent; modelBlock2 != modelBlock3; modelBlock3 = modelBlock3.parent.parent) {
               modelBlock2 = modelBlock2.parent;
            }

            throw new ModelBlock.LoopException();
         } catch (NullPointerException nullPointerException) {
         }
      }
   }

   public static final class Bookkeep {
      public final ModelBlock model;
      public ModelBlock modelExt;

      public Bookkeep(ModelBlock modelBlock) {
         this.model = modelBlock;
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public ModelBlock deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         List list = this.getModelElements(jsonDeserializationContext, jsonObject);
         String string = this.getParent(jsonObject);
         boolean bl = StringUtils.isEmpty(string);
         boolean bl2 = list.isEmpty();
         if (bl2 && bl) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         }

         if (!bl && !bl2) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         }

         Map map = this.getTextures(jsonObject);
         boolean bl3 = this.getAmbientOcclusionEnabled(jsonObject);
         ItemCameraTransforms itemCameraTransforms = ItemCameraTransforms.DEFAULT;
         if (jsonObject.has("display")) {
            JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "display");
            itemCameraTransforms = jsonDeserializationContext.deserialize(jsonObject2, ItemCameraTransforms.class);
         }

         return bl2 ? new ModelBlock(new ResourceLocation(string), map, bl3, true, itemCameraTransforms) : new ModelBlock(list, map, bl3, true, itemCameraTransforms);
      }

      private Map<String, String> getTextures(JsonObject jsonObject) {
         HashMap hashMap = Maps.newHashMap();
         if (jsonObject.has("textures")) {
            JsonObject jsonObject2 = jsonObject.getAsJsonObject("textures");

            for (Entry entry : jsonObject2.entrySet()) {
               hashMap.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
         }

         return hashMap;
      }

      private String getParent(JsonObject jsonObject) {
         return JsonUtils.getString(jsonObject, "parent", "");
      }

      public boolean getAmbientOcclusionEnabled(JsonObject jsonObject) {
         return JsonUtils.getBoolean(jsonObject, "ambientocclusion", true);
      }

      public List<BlockPart> getModelElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         ArrayList arrayList = Lists.newArrayList();
         if (jsonObject.has("elements")) {
            for (JsonElement jsonElement : JsonUtils.getJsonArray(jsonObject, "elements")) {
               arrayList.add((BlockPart)jsonDeserializationContext.deserialize(jsonElement, BlockPart.class));
            }
         }

         return arrayList;
      }
   }

   public static class LoopException extends RuntimeException {
   }
}
