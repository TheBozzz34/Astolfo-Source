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

   public LayerArmorBase(RendererLivingEntity<?> rendererIn) {
      this.renderer = rendererIn;
      this.initArmor();
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      this.renderLayer(entitylivingbaseIn, f, g, partialTicks, h, f2, f3, scale, 4);
      this.renderLayer(entitylivingbaseIn, f, g, partialTicks, h, f2, f3, scale, 3);
      this.renderLayer(entitylivingbaseIn, f, g, partialTicks, h, f2, f3, scale, 2);
      this.renderLayer(entitylivingbaseIn, f, g, partialTicks, h, f2, f3, scale, 1);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderLayer(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale, int armorSlot) {
      ItemStack itemStack = this.getCurrentArmor(entitylivingbaseIn, armorSlot);
      if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
         ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
         ModelBase modelBase = this.getArmorModel(armorSlot);
         modelBase.setModelAttributes(this.renderer.getMainModel());
         modelBase.setLivingAnimations(entitylivingbaseIn, f, g, partialTicks);
         if (Reflector.ForgeHooksClient.exists()) {
            modelBase = this.getArmorModelHook(entitylivingbaseIn, itemStack, armorSlot, modelBase);
         }

         this.setModelPartVisible((T)modelBase, armorSlot);
         boolean bl = this.isSlotForLeggings(armorSlot);
         if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemStack, bl ? 2 : 1, (String)null)) {
            if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
               this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemStack, bl ? 2 : 1, (String)null));
            } else {
               this.renderer.bindTexture(this.getArmorResource(itemArmor, bl));
            }
         }

         if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
            int i = itemArmor.getColor(itemStack);
            if (i != -1) {
               float f4 = (i >> 16 & 0xFF) / 255.0F;
               float f5 = (i >> 8 & 0xFF) / 255.0F;
               float f6 = (i & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f4, this.colorG * f5, this.colorB * f6, this.alpha);
               modelBase.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemStack, bl ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemStack, bl ? 2 : 1, "overlay"));
               }
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            modelBase.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
            if (!z
               && itemStack.isItemEnchanted()
               && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemStack, modelBase, f, g, partialTicks, h, f2, f3, scale))) {
               this.renderGlint(entitylivingbaseIn, (T)modelBase, f, g, partialTicks, h, f2, f3, scale);
            }

            return;
         }

         switch (itemArmor.getArmorMaterial()) {
            case LEATHER:
               int j = itemArmor.getColor(itemStack);
               float f7 = (j >> 16 & 0xFF) / 255.0F;
               float f8 = (j >> 8 & 0xFF) / 255.0F;
               float f9 = (j & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f7, this.colorG * f8, this.colorB * f9, this.alpha);
               modelBase.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemStack, bl ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(itemArmor, bl, "overlay"));
               }
            case CHAIN:
            case IRON:
            case GOLD:
            case DIAMOND:
               GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
               modelBase.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         }

         if (!z
            && itemStack.isItemEnchanted()
            && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemStack, modelBase, f, g, partialTicks, h, f2, f3, scale))) {
            this.renderGlint(entitylivingbaseIn, (T)modelBase, f, g, partialTicks, h, f2, f3, scale);
         }
      }
   }

   public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot) {
      return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
   }

   public T getArmorModel(int armorSlot) {
      return this.isSlotForLeggings(armorSlot) ? this.modelLeggings : this.modelArmor;
   }

   private boolean isSlotForLeggings(int armorSlot) {
      return armorSlot == 2;
   }

   private void renderGlint(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         float f4 = entitylivingbaseIn.ticksExisted + partialTicks;
         this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintBegin();
         }

         GlStateManager.enableBlend();
         GlStateManager.depthFunc(514);
         GlStateManager.depthMask(false);
         float f5 = 0.5F;
         GlStateManager.color(f5, f5, f5, 1.0F);

         for (int i = 0; i < 2; i++) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float f6 = 0.76F;
            GlStateManager.color(0.5F * f6, 0.25F * f6, 0.8F * f6, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f7 = 0.33333334F;
            GlStateManager.scale(f7, f7, f7);
            GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f4 * (0.001F + i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }
   }

   private ResourceLocation getArmorResource(ItemArmor itemArmor, boolean bl) {
      return this.getArmorResource(itemArmor, bl, (String)null);
   }

   private ResourceLocation getArmorResource(ItemArmor itemArmor, boolean bl, String string) {
      String string2 = String.format(
         "textures/models/armor/%s_layer_%d%s.png", itemArmor.getArmorMaterial().getName(), bl ? 2 : 1, string == null ? "" : String.format("_%s", string)
      );
      ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(string2);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(string2);
         ARMOR_TEXTURE_RES_MAP.put(string2, resourceLocation);
      }

      return resourceLocation;
   }

   public abstract void initArmor();

   public abstract void setModelPartVisible(T model, int armorSlot);

   public ModelBase getArmorModelHook(EntityLivingBase entityLivingBase, ItemStack itemStack, int i, ModelBase modelBase) {
      return modelBase;
   }

   public ResourceLocation getArmorResource(Entity entity, ItemStack itemStack, int i, String string) {
      ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
      String string2 = itemArmor.getArmorMaterial().getName();
      String string3 = "minecraft";
      int j = string2.indexOf(58);
      if (j != -1) {
         string3 = string2.substring(0, j);
         string2 = string2.substring(j + 1);
      }

      String string4 = String.format(
         "%s:textures/models/armor/%s_layer_%d%s.png", string3, string2, i == 2 ? 2 : 1, string == null ? "" : String.format("_%s", string)
      );
      string4 = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, entity, itemStack, string4, i, string);
      ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(string4);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(string4);
         ARMOR_TEXTURE_RES_MAP.put(string4, resourceLocation);
      }

      return resourceLocation;
   }

   public static void setRenderEnchantGlint(boolean bl) {
      z = !bl;
   }
}
