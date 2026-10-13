package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.src.CustomItems;
import net.minecraft.src.Reflector;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   public static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   public T modelLeggings;
   public T modelArmor;
   private final RendererLivingEntity<?> renderer;
   private float alpha = 1.0F;
   private float colorR = 1.0F;
   private float colorG = 1.0F;
   private float colorB = 1.0F;
   private static boolean z;
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private static final long b = 92043053276715L;

   public LayerArmorBase(RendererLivingEntity<?> var1) {
      this.renderer = var1;
      this.initArmor();
   }

   @Override
   public void doRenderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.renderLayer(var1, var2, var3, var4, var5, var6, var7, var8, 4);
      this.renderLayer(var1, var2, var3, var4, var5, var6, var7, var8, 3);
      this.renderLayer(var1, var2, var3, var4, var5, var6, var7, var8, 2);
      this.renderLayer(var1, var2, var3, var4, var5, var6, var7, var8, 1);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      ItemStack var13 = this.getCurrentArmor(var1, var9);
      if (var13 != null && var13.getItem() instanceof ItemArmor) {
         ItemArmor var14 = (ItemArmor)var13.getItem();
         ModelBase var15 = this.getArmorModel(var9);
         var15.setModelAttributes(this.renderer.getMainModel());
         var15.setLivingAnimations(var1, var2, var3, var4);
         if (Reflector.ForgeHooksClient.exists()) {
            var15 = this.getArmorModelHook(var1, var13, var9, var15);
         }

         this.setModelPartVisible((T)var15, var9);
         boolean var16 = this.isSlotForLeggings(var9);
         if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(var13, var16 ? 2 : 1, (String)null)) {
            if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
               this.renderer.bindTexture(this.getArmorResource(var1, var13, var16 ? 2 : 1, (String)null));
            } else {
               this.renderer.bindTexture(this.getArmorResource(var14, var16));
            }
         }

         if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
            int var21 = var14.getColor(var13);
            if (var21 != -1) {
               float var22 = (var21 >> 16 & 0xFF) / 255.0F;
               float var23 = (var21 >> 8 & 0xFF) / 255.0F;
               float var24 = (var21 & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * var22, this.colorG * var23, this.colorB * var24, this.alpha);
               var15.render(var1, var2, var3, var5, var6, var7, var8);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(var13, var16 ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(var1, var13, var16 ? 2 : 1, "overlay"));
               }
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            var15.render(var1, var2, var3, var5, var6, var7, var8);
            if (!z
               && var13.isItemEnchanted()
               && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(var1, var13, var15, var2, var3, var4, var5, var6, var7, var8))) {
               this.renderGlint(var1, (T)var15, var2, var3, var4, var5, var6, var7, var8);
            }

            return;
         }

         switch (var14.getArmorMaterial()) {
            case LEATHER:
               int var17 = var14.getColor(var13);
               float var18 = (var17 >> 16 & 0xFF) / 255.0F;
               float var19 = (var17 >> 8 & 0xFF) / 255.0F;
               float var20 = (var17 & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * var18, this.colorG * var19, this.colorB * var20, this.alpha);
               var15.render(var1, var2, var3, var5, var6, var7, var8);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(var13, var16 ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(var14, var16, "overlay"));
               }
            case CHAIN:
            case IRON:
            case GOLD:
            case DIAMOND:
               GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
               var15.render(var1, var2, var3, var5, var6, var7, var8);
         }

         if (!z
            && var13.isItemEnchanted()
            && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(var1, var13, var15, var2, var3, var4, var5, var6, var7, var8))) {
            this.renderGlint(var1, (T)var15, var2, var3, var4, var5, var6, var7, var8);
         }
      }
   }

   public ItemStack getCurrentArmor(EntityLivingBase var1, int var2) {
      return var1.getCurrentArmor(var2 - 1);
   }

   public T getArmorModel(int var1) {
      return this.isSlotForLeggings(var1) ? this.modelLeggings : this.modelArmor;
   }

   private boolean isSlotForLeggings(int var1) {
      return var1 == 2;
   }

   private void renderGlint(EntityLivingBase var1, T var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      long var10 = b ^ 100992735750836L;
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         float var13 = var1.ticksExisted + var5;
         this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintBegin();
         }

         GlStateManager.enableBlend();
         GlStateManager.depthFunc(514);
         GlStateManager.depthMask(false);
         float var14 = 0.5F;
         GlStateManager.color(var14, var14, var14, 1.0F);

         for (int var15 = 0; var15 < 2; var15++) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float var16 = 0.76F;
            long var10000 = 4694849417834371747L ^ var10;
            GlStateManager.color(0.5F * var16, 0.25F * var16, 0.8F * var16, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float var17 = 0.33333334F;
            GlStateManager.scale(var17, var17, var17);
            GlStateManager.rotate(30.0F - var15 * 60.0F, 0.0F, 0.0F, 1.0F);
            var10000 = 4524731379055971666L ^ var10;
            GlStateManager.translate(0.0F, var13 * (0.001F + var15 * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            var2.render(var1, var3, var4, var6, var7, var8, var9);
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         long var19 = 4643446299531239210L ^ var10;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         var19 = 4594118597153128257L ^ var10;
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }
   }

   private ResourceLocation getArmorResource(ItemArmor var1, boolean var2) {
      return this.getArmorResource(var1, var2, (String)null);
   }

   private ResourceLocation getArmorResource(ItemArmor var1, boolean var2, String var3) {
      String var7 = String.format(
         "textures/models/armor/%s_layer_%d%s.png", var1.getArmorMaterial().getName(), var2 ? 2 : 1, var3 == null ? "" : String.format("_%s", var3)
      );
      ResourceLocation var8 = ARMOR_TEXTURE_RES_MAP.get(var7);
      if (var8 == null) {
         var8 = new ResourceLocation(var7);
         ARMOR_TEXTURE_RES_MAP.put(var7, var8);
      }

      return var8;
   }

   public abstract void initArmor();

   public abstract void setModelPartVisible(T var1, int var2);

   public ModelBase getArmorModelHook(EntityLivingBase var1, ItemStack var2, int var3, ModelBase var4) {
      return var4;
   }

   public ResourceLocation getArmorResource(Entity var1, ItemStack var2, int var3, String var4) {
      ItemArmor var8 = (ItemArmor)var2.getItem();
      String var9 = var8.getArmorMaterial().getName();
      String var10 = "minecraft";
      int var11 = var9.indexOf(58);
      if (var11 != -1) {
         var10 = var9.substring(0, var11);
         var9 = var9.substring(var11 + 1);
      }

      String var12 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", var10, var9, var3 == 2 ? 2 : 1, var4 == null ? "" : String.format("_%s", var4));
      var12 = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, var1, var2, var12, var3, var4);
      ResourceLocation var13 = ARMOR_TEXTURE_RES_MAP.get(var12);
      if (var13 == null) {
         var13 = new ResourceLocation(var12);
         ARMOR_TEXTURE_RES_MAP.put(var12, var13);
      }

      return var13;
   }

   public static void i(boolean var0) {
      z = !var0;
   }
}
