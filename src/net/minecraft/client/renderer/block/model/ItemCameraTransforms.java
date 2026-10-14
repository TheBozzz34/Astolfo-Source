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

   public ItemCameraTransforms(ItemCameraTransforms transforms) {
      this.thirdPerson = transforms.thirdPerson;
      this.firstPerson = transforms.firstPerson;
      this.head = transforms.head;
      this.gui = transforms.gui;
      this.ground = transforms.ground;
      this.fixed = transforms.fixed;
   }

   public ItemCameraTransforms(
      ItemTransformVec3f thirdPersonIn,
      ItemTransformVec3f firstPersonIn,
      ItemTransformVec3f headIn,
      ItemTransformVec3f guiIn,
      ItemTransformVec3f groundIn,
      ItemTransformVec3f fixedIn
   ) {
      this.thirdPerson = thirdPersonIn;
      this.firstPerson = firstPersonIn;
      this.head = headIn;
      this.gui = guiIn;
      this.ground = groundIn;
      this.fixed = fixedIn;
   }

   public void applyTransform(ItemCameraTransforms.TransformType typeIn) {
      ItemTransformVec3f itemTransformVec3f = this.getTransform(typeIn);
      if (itemTransformVec3f != ItemTransformVec3f.DEFAULT) {
         GlStateManager.translate(itemTransformVec3f.translation.x + field_181690_b, itemTransformVec3f.translation.y + field_181691_c, itemTransformVec3f.translation.z + field_181692_d);
         GlStateManager.rotate(itemTransformVec3f.rotation.y + field_181694_f, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(itemTransformVec3f.rotation.x + field_181693_e, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(itemTransformVec3f.rotation.z + field_181695_g, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(itemTransformVec3f.scale.x + field_181696_h, itemTransformVec3f.scale.y + field_181697_i, itemTransformVec3f.scale.z + field_181698_j);
      }
   }

   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType typeIn) {
      switch (typeIn) {
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

   public boolean func_181687_c(ItemCameraTransforms.TransformType typeIn) {
      return !this.getTransform(typeIn).equals(ItemTransformVec3f.DEFAULT);
   }

   public static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      public ItemCameraTransforms deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         ItemTransformVec3f itemTransformVec3f = this.func_181683_a(jsonDeserializationContext, jsonObject, "thirdperson");
         ItemTransformVec3f itemTransformVec3f2 = this.func_181683_a(jsonDeserializationContext, jsonObject, "firstperson");
         ItemTransformVec3f itemTransformVec3f3 = this.func_181683_a(jsonDeserializationContext, jsonObject, "head");
         ItemTransformVec3f itemTransformVec3f4 = this.func_181683_a(jsonDeserializationContext, jsonObject, "gui");
         ItemTransformVec3f itemTransformVec3f5 = this.func_181683_a(jsonDeserializationContext, jsonObject, "ground");
         ItemTransformVec3f itemTransformVec3f6 = this.func_181683_a(jsonDeserializationContext, jsonObject, "fixed");
         return new ItemCameraTransforms(itemTransformVec3f, itemTransformVec3f2, itemTransformVec3f3, itemTransformVec3f4, itemTransformVec3f5, itemTransformVec3f6);
      }

      private ItemTransformVec3f func_181683_a(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
         return jsonObject.has(string) ? jsonDeserializationContext.deserialize(jsonObject.get(string), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
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
