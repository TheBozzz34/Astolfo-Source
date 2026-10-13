package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderLiving<EntityHorse> {
   private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();
   private static final ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
   private static final ResourceLocation muleTextures = new ResourceLocation("textures/entity/horse/mule.png");
   private static final ResourceLocation donkeyTextures = new ResourceLocation("textures/entity/horse/donkey.png");
   private static final ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
   private static final ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");
   private static final long d = 123736414345568L;

   public RenderHorse(RenderManager var1, ModelHorse var2, float var3) {
      super(var1, var2, var3);
   }

   public void preRenderCallback(EntityHorse var1, float var2) {
      float var6 = 1.0F;
      int var7 = var1.getHorseType();
      if (var7 == 1) {
         var6 *= 0.87F;
      } else if (var7 == 2) {
         var6 *= 0.92F;
      }

      GlStateManager.scale(var6, var6, var6);
      super.preRenderCallback(var1, var2);
   }

   public ResourceLocation getEntityTexture(EntityHorse var1) {
      if (!var1.func_110239_cn()) {
         switch (var1.getHorseType()) {
            case 0:
            default:
               return whiteHorseTextures;
            case 1:
               return donkeyTextures;
            case 2:
               return muleTextures;
            case 3:
               return zombieHorseTextures;
            case 4:
               return skeletonHorseTextures;
         }
      } else {
         return this.func_110848_b(var1);
      }
   }

   private ResourceLocation func_110848_b(EntityHorse var1) {
      String var5 = var1.getHorseTexture();
      if (!var1.func_175507_cI()) {
         return null;
      }

      ResourceLocation var6 = field_110852_a.get(var5);
      if (var6 == null) {
         var6 = new ResourceLocation(var5);
         Minecraft.getMinecraft().getTextureManager().loadTexture(var6, new LayeredTexture(var1.getVariantTexturePaths()));
         field_110852_a.put(var5, var6);
      }

      return var6;
   }
}
