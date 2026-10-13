package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class ItemTransformVec3f {
   public static final ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;
   private static final long a = 104189392093063L;

   public ItemTransformVec3f(Vector3f var1, Vector3f var2, Vector3f var3) {
      this.rotation = new Vector3f(var1);
      this.translation = new Vector3f(var2);
      this.scale = new Vector3f(var3);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (this.getClass() != var1.getClass()) {
         return false;
      }

      ItemTransformVec3f var5 = (ItemTransformVec3f)var1;
      return !this.rotation.equals(var5.rotation) ? false : (!this.scale.equals(var5.scale) ? false : this.translation.equals(var5.translation));
   }

   @Override
   public int hashCode() {
      int var3 = this.rotation.hashCode();
      var3 = 31 * var3 + this.translation.hashCode();
      return 31 * var3 + this.scale.hashCode();
   }

   public static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      private static final Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);
      private static final long a = 76673069291567L;

      public ItemTransformVec3f deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var7 = var1.getAsJsonObject();
         Vector3f var8 = this.parseVector3f(var7, "rotation", ROTATION_DEFAULT);
         Vector3f var9 = this.parseVector3f(var7, "translation", TRANSLATION_DEFAULT);
         var9.scale(0.0625F);
         var9.x = MathHelper.clamp_float(var9.x, -1.5F, 1.5F);
         var9.y = MathHelper.clamp_float(var9.y, -1.5F, 1.5F);
         var9.z = MathHelper.clamp_float(var9.z, -1.5F, 1.5F);
         Vector3f var10 = this.parseVector3f(var7, "scale", SCALE_DEFAULT);
         var10.x = MathHelper.clamp_float(var10.x, -4.0F, 4.0F);
         var10.y = MathHelper.clamp_float(var10.y, -4.0F, 4.0F);
         var10.z = MathHelper.clamp_float(var10.z, -4.0F, 4.0F);
         return new ItemTransformVec3f(var8, var9, var10);
      }

      private Vector3f parseVector3f(JsonObject var1, String var2, Vector3f var3) {
         if (!var1.has(var2)) {
            return var3;
         }

         JsonArray var7 = JsonUtils.getJsonArray(var1, var2);
         if (var7.size() != 3) {
            throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var7.size());
         }

         float[] var8 = new float[3];

         for (int var9 = 0; var9 < var8.length; var9++) {
            var8[var9] = JsonUtils.getFloat(var7.get(var9), var2 + "[" + var9 + "]");
         }

         return new Vector3f(var8[0], var8[1], var8[2]);
      }
   }
}
