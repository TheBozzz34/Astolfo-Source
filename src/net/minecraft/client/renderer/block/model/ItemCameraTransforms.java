package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms {
   public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
   public static float field_181690_b = 0.0F;
   public static float field_181691_c = 0.0F;
   public static float field_181692_d = 0.0F;
   public static float field_181693_e = 0.0F;
   public static float field_181694_f = 0.0F;
   public static float field_181695_g = 0.0F;
   public static float field_181696_h = 0.0F;
   public static float field_181697_i = 0.0F;
   public static float field_181698_j = 0.0F;
   public final ItemTransformVec3f thirdPerson;
   public final ItemTransformVec3f firstPerson;
   public final ItemTransformVec3f head;
   public final ItemTransformVec3f gui;
   public final ItemTransformVec3f ground;
   public final ItemTransformVec3f fixed;
   private static final long a = 15596198035923L;

   private ItemCameraTransforms() {
      this(
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT
      );
   }

   public ItemCameraTransforms(ItemCameraTransforms var1) {
      this.thirdPerson = var1.thirdPerson;
      this.firstPerson = var1.firstPerson;
      this.head = var1.head;
      this.gui = var1.gui;
      this.ground = var1.ground;
      this.fixed = var1.fixed;
   }

   public ItemCameraTransforms(
      ItemTransformVec3f var1, ItemTransformVec3f var2, ItemTransformVec3f var3, ItemTransformVec3f var4, ItemTransformVec3f var5, ItemTransformVec3f var6
   ) {
      this.thirdPerson = var1;
      this.firstPerson = var2;
      this.head = var3;
      this.gui = var4;
      this.ground = var5;
      this.fixed = var6;
   }

   public void applyTransform(ItemCameraTransforms.TransformType var1) {
      ItemTransformVec3f var5 = this.getTransform(var1);
      if (var5 != ItemTransformVec3f.DEFAULT) {
         GlStateManager.translate(var5.translation.x + field_181690_b, var5.translation.y + field_181691_c, var5.translation.z + field_181692_d);
         GlStateManager.rotate(var5.rotation.y + field_181694_f, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(var5.rotation.x + field_181693_e, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(var5.rotation.z + field_181695_g, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(var5.scale.x + field_181696_h, var5.scale.y + field_181697_i, var5.scale.z + field_181698_j);
      }
   }

   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType var1) {
      switch (var1) {
         case THIRD_PERSON:
            return this.thirdPerson;
         case FIRST_PERSON:
            return this.firstPerson;
         case HEAD:
            return this.head;
         case GUI:
            return this.gui;
         case GROUND:
            return this.ground;
         case FIXED:
            return this.fixed;
         default:
            return ItemTransformVec3f.DEFAULT;
      }
   }

   public boolean func_181687_c(ItemCameraTransforms.TransformType var1) {
      return !this.getTransform(var1).equals(ItemTransformVec3f.DEFAULT);
   }

   public static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      private static final long a = 4675101033706L;

      public ItemCameraTransforms deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var6 = var1.getAsJsonObject();
         ItemTransformVec3f var7 = this.func_181683_a(var3, var6, "thirdperson");
         ItemTransformVec3f var8 = this.func_181683_a(var3, var6, "firstperson");
         ItemTransformVec3f var9 = this.func_181683_a(var3, var6, "head");
         ItemTransformVec3f var10 = this.func_181683_a(var3, var6, "gui");
         ItemTransformVec3f var11 = this.func_181683_a(var3, var6, "ground");
         ItemTransformVec3f var12 = this.func_181683_a(var3, var6, "fixed");
         return new ItemCameraTransforms(var7, var8, var9, var10, var11, var12);
      }

      private ItemTransformVec3f func_181683_a(JsonDeserializationContext var1, JsonObject var2, String var3) {
         return var2.has(var3) ? var1.deserialize(var2.get(var3), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
      }
   }

   public enum TransformType {
      NONE,
      THIRD_PERSON,
      FIRST_PERSON,
      HEAD,
      GUI,
      GROUND,
      FIXED;
   }
}
