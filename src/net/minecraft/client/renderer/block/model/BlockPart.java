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
   private static final long b = 4500400533291L;

   public BlockPart(Vector3f var1, Vector3f var2, Map<EnumFacing, BlockPartFace> var3, BlockPartRotation var4, boolean var5) {
      this.positionFrom = var1;
      this.positionTo = var2;
      this.mapFaces = var3;
      this.partRotation = var4;
      this.shade = var5;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      for (Entry var5 : this.mapFaces.entrySet()) {
         float[] var6 = this.getFaceUvs((EnumFacing)var5.getKey());
         ((BlockPartFace)var5.getValue()).blockFaceUV.setUvs(var6);
      }
   }

   private float[] getFaceUvs(EnumFacing var1) {
      float[] var5;
      switch (var1) {
         case DOWN:
         case UP:
            var5 = new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
            break;
         case NORTH:
         case SOUTH:
            var5 = new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
            break;
         case WEST:
         case EAST:
            var5 = new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
            break;
         default:
            throw new NullPointerException();
      }

      return var5;
   }

   public static class Deserializer implements JsonDeserializer<BlockPart> {
      private static final long a = 38873109812456L;

      public BlockPart deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var7 = var1.getAsJsonObject();
         Vector3f var8 = this.parsePositionFrom(var7);
         Vector3f var9 = this.parsePositionTo(var7);
         BlockPartRotation var10 = this.parseRotation(var7);
         Map var11 = this.parseFacesCheck(var3, var7);
         if (var7.has("shade") && !JsonUtils.isBoolean(var7, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         }

         boolean var12 = JsonUtils.getBoolean(var7, "shade", true);
         return new BlockPart(var8, var9, var11, var10, var12);
      }

      private BlockPartRotation parseRotation(JsonObject var1) {
         BlockPartRotation var5 = null;
         if (var1.has("rotation")) {
            JsonObject var6 = JsonUtils.getJsonObject(var1, "rotation");
            Vector3f var7 = this.parsePosition(var6, "origin");
            var7.scale(0.0625F);
            EnumFacing.Axis var8 = this.parseAxis(var6);
            float var9 = this.parseAngle(var6);
            boolean var10 = JsonUtils.getBoolean(var6, "rescale", false);
            var5 = new BlockPartRotation(var7, var8, var9, var10);
         }

         return var5;
      }

      private float parseAngle(JsonObject var1) {
         float var5 = JsonUtils.getFloat(var1, "angle");
         if (var5 != 0.0F && MathHelper.abs(var5) != 22.5F && MathHelper.abs(var5) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + var5 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return var5;
         }
      }

      private EnumFacing.Axis parseAxis(JsonObject var1) {
         String var5 = JsonUtils.getString(var1, "axis");
         EnumFacing.Axis var6 = EnumFacing.Axis.byName(var5.toLowerCase());
         if (var6 == null) {
            throw new JsonParseException("Invalid rotation axis: " + var5);
         } else {
            return var6;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext var1, JsonObject var2) {
         Map var6 = this.parseFaces(var1, var2);
         if (var6.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return var6;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext var1, JsonObject var2) {
         EnumMap var6 = Maps.newEnumMap(EnumFacing.class);
         JsonObject var7 = JsonUtils.getJsonObject(var2, "faces");

         for (Entry var9 : var7.entrySet()) {
            EnumFacing var10 = this.parseEnumFacing((String)var9.getKey());
            var6.put(var10, (BlockPartFace)var1.deserialize((JsonElement)var9.getValue(), BlockPartFace.class));
         }

         return var6;
      }

      private EnumFacing parseEnumFacing(String var1) {
         EnumFacing var5 = EnumFacing.byName(var1);
         if (var5 == null) {
            throw new JsonParseException("Unknown facing: " + var1);
         } else {
            return var5;
         }
      }

      private Vector3f parsePositionTo(JsonObject var1) {
         Vector3f var5 = this.parsePosition(var1, "to");
         if (var5.x >= -16.0F && var5.y >= -16.0F && var5.z >= -16.0F && var5.x <= 32.0F && var5.y <= 32.0F && var5.z <= 32.0F) {
            return var5;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + var5);
         }
      }

      private Vector3f parsePositionFrom(JsonObject var1) {
         Vector3f var5 = this.parsePosition(var1, "from");
         if (var5.x >= -16.0F && var5.y >= -16.0F && var5.z >= -16.0F && var5.x <= 32.0F && var5.y <= 32.0F && var5.z <= 32.0F) {
            return var5;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + var5);
         }
      }

      private Vector3f parsePosition(JsonObject var1, String var2) {
         JsonArray var6 = JsonUtils.getJsonArray(var1, var2);
         if (var6.size() != 3) {
            throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var6.size());
         }

         float[] var7 = new float[3];

         for (int var8 = 0; var8 < var7.length; var8++) {
            var7[var8] = JsonUtils.getFloat(var6.get(var8), var2 + "[" + var8 + "]");
         }

         return new Vector3f(var7[0], var7[1], var7[2]);
      }
   }
}
