package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {
   public final Vector3f positionFrom;
   public final Vector3f positionTo;
   public final Map<EnumFacing, BlockPartFace> mapFaces;
   public final BlockPartRotation partRotation;
   public final boolean shade;

   public BlockPart(
      Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn
   ) {
      this.positionFrom = positionFromIn;
      this.positionTo = positionToIn;
      this.mapFaces = mapFacesIn;
      this.partRotation = partRotationIn;
      this.shade = shadeIn;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      for (Entry entry : this.mapFaces.entrySet()) {
         float[] floats = this.getFaceUvs((EnumFacing)entry.getKey());
         ((BlockPartFace)entry.getValue()).blockFaceUV.setUvs(floats);
      }
   }

   private float[] getFaceUvs(EnumFacing enumFacing) {
      float[] floats;
      switch (enumFacing) {
         case DOWN:
         case UP:
            floats = new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
            break;
         case NORTH:
         case SOUTH:
            floats = new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
            break;
         case WEST:
         case EAST:
            floats = new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
            break;
         default:
            throw new NullPointerException();
      }

      return floats;
   }

   public static class Deserializer implements JsonDeserializer<BlockPart> {
      public BlockPart deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Vector3f vector3f = this.parsePositionFrom(jsonObject);
         Vector3f vector3f2 = this.parsePositionTo(jsonObject);
         BlockPartRotation blockPartRotation = this.parseRotation(jsonObject);
         Map map = this.parseFacesCheck(jsonDeserializationContext, jsonObject);
         if (jsonObject.has("shade") && !JsonUtils.isBoolean(jsonObject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         }

         boolean bl = JsonUtils.getBoolean(jsonObject, "shade", true);
         return new BlockPart(vector3f, vector3f2, map, blockPartRotation, bl);
      }

      private BlockPartRotation parseRotation(JsonObject jsonObject) {
         BlockPartRotation blockPartRotation = null;
         if (jsonObject.has("rotation")) {
            JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "rotation");
            Vector3f vector3f = this.parsePosition(jsonObject2, "origin");
            vector3f.scale(0.0625F);
            EnumFacing.Axis axis = this.parseAxis(jsonObject2);
            float f = this.parseAngle(jsonObject2);
            boolean bl = JsonUtils.getBoolean(jsonObject2, "rescale", false);
            blockPartRotation = new BlockPartRotation(vector3f, axis, f, bl);
         }

         return blockPartRotation;
      }

      private float parseAngle(JsonObject jsonObject) {
         float f = JsonUtils.getFloat(jsonObject, "angle");
         if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return f;
         }
      }

      private EnumFacing.Axis parseAxis(JsonObject jsonObject) {
         String string = JsonUtils.getString(jsonObject, "axis");
         EnumFacing.Axis axis2 = EnumFacing.Axis.byName(string.toLowerCase());
         if (axis2 == null) {
            throw new JsonParseException("Invalid rotation axis: " + string);
         } else {
            return axis2;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         Map map = this.parseFaces(jsonDeserializationContext, jsonObject);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         EnumMap enumMap = Maps.newEnumMap(EnumFacing.class);
         JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "faces");

         for (Entry entry : jsonObject2.entrySet()) {
            EnumFacing enumFacing = this.parseEnumFacing((String)entry.getKey());
            enumMap.put(enumFacing, (BlockPartFace)jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), BlockPartFace.class));
         }

         return enumMap;
      }

      private EnumFacing parseEnumFacing(String name) {
         EnumFacing enumFacing = EnumFacing.byName(name);
         if (enumFacing == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return enumFacing;
         }
      }

      private Vector3f parsePositionTo(JsonObject jsonObject) {
         Vector3f vector3f = this.parsePosition(jsonObject, "to");
         if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f parsePositionFrom(JsonObject jsonObject) {
         Vector3f vector3f = this.parsePosition(jsonObject, "from");
         if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f parsePosition(JsonObject jsonObject, String string) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, string);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
         }

         float[] floats = new float[3];

         for (int i = 0; i < floats.length; i++) {
            floats[i] = JsonUtils.getFloat(jsonArray.get(i), string + "[" + i + "]");
         }

         return new Vector3f(floats[0], floats[1], floats[2]);
      }
   }
}
