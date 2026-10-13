package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class CustomItems {
   private static CustomItemProperties[][] itemProperties = (CustomItemProperties[][])null;
   private static CustomItemProperties[][] enchantmentProperties = (CustomItemProperties[][])null;
   private static Map mapPotionIds = null;
   private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private static boolean useGlint = true;
   private static boolean w = false;
   public static final int u = 16384;
   public static final int C = 63;
   public static final int r = 64;
   public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
   public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
   public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
   public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
   public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
   public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
   private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
   private static final long b = 117985789368265L;

   public static void N() {
      itemProperties = (CustomItemProperties[][])null;
      enchantmentProperties = (CustomItemProperties[][])null;
      useGlint = true;
      if (Config.isCustomItems()) {
         readCitProperties("mcpatcher/cit.properties");
         IResourcePack[] var3 = Config.getResourcePacks();

         for (int var4 = var3.length - 1; var4 >= 0; var4--) {
            IResourcePack var5 = var3[var4];
            x(var5);
         }

         x(Config.getDefaultResourcePack());
         if (itemProperties.length <= 0) {
            itemProperties = (CustomItemProperties[][])null;
         }

         if (enchantmentProperties.length <= 0) {
            enchantmentProperties = (CustomItemProperties[][])null;
         }
      }
   }

   private static void readCitProperties(String var0) {
      try {
         ResourceLocation var4 = new ResourceLocation(var0);
         InputStream var5 = Config.getResourceStream(var4);
         if (var5 == null) {
            return;
         }

         Config.dbg("CustomItems: Loading " + var0);
         PropertiesOrdered var6 = new PropertiesOrdered();
         var6.load(var5);
         var5.close();
         useGlint = Config.parseBoolean(var6.getProperty("useGlint"), true);
      } catch (FileNotFoundException var7) {
         return;
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   private static void x(IResourcePack var0) {
      String[] var4 = ResUtils.collectFiles(var0, "mcpatcher/cit/", ".properties", (String[])null);
      Map var5 = makeAutoImageProperties(var0);
      if (var5.size() > 0) {
         Set var6 = var5.keySet();
         String[] var7 = (java.lang.String[])(var6.toArray(new String[var6.size()]));
         var4 = (String[])Config.addObjectsToArray(var4, var7);
      }

      Arrays.sort(var4);
      List var16 = makePropertyList(itemProperties);
      List var17 = makePropertyList(enchantmentProperties);

      for (int var8 = 0; var8 < var4.length; var8++) {
         String var9 = var4[var8];
         Config.dbg("CustomItems: " + var9);

         try {
            CustomItemProperties var10 = null;
            if (var5.containsKey(var9)) {
               var10 = (CustomItemProperties)var5.get(var9);
            }

            if (var10 == null) {
               ResourceLocation var11 = new ResourceLocation(var9);
               InputStream var12 = var0.getInputStream(var11);
               if (var12 == null) {
                  Config.warn("CustomItems file not found: " + var9);
                  continue;
               }

               PropertiesOrdered var13 = new PropertiesOrdered();
               var13.load(var12);
               var10 = new CustomItemProperties(var13, var9);
            }

            if (var10.isValid(var9)) {
               addToItemList(var10, var16);
               addToEnchantmentList(var10, var17);
            }
         } catch (FileNotFoundException var14) {
            Config.warn("CustomItems file not found: " + var9);
         } catch (Exception var15) {
            var15.printStackTrace();
         }
      }

      itemProperties = propertyListToArray(var16);
      enchantmentProperties = propertyListToArray(var17);
      Comparator var18 = getPropertiesComparator();

      for (int var19 = 0; var19 < itemProperties.length; var19++) {
         CustomItemProperties[] var21 = itemProperties[var19];
         if (var21 != null) {
            Arrays.sort(var21, var18);
         }
      }

      for (int var20 = 0; var20 < enchantmentProperties.length; var20++) {
         CustomItemProperties[] var22 = enchantmentProperties[var20];
         if (var22 != null) {
            Arrays.sort(var22, var18);
         }
      }
   }

   private static Comparator getPropertiesComparator() {
      return new Comparator() {
         private static final long a = 31755220076810L;

         @Override
         public int compare(Object var1, Object var2) {
            CustomItemProperties var6 = (CustomItemProperties)var1;
            CustomItemProperties var7 = (CustomItemProperties)var2;
            return var6.layer != var7.layer
               ? var6.layer - var7.layer
               : (
                  var6.weight != var7.weight
                     ? var7.weight - var6.weight
                     : (!var6.basePath.equals(var7.basePath) ? var6.basePath.compareTo(var7.basePath) : var6.name.compareTo(var7.name))
               );
         }
      };
   }

   public static void updateIcons(TextureMap var0) {
      for (CustomItemProperties var5 : Z()) {
         var5.updateIcons(var0);
      }
   }

   public static void F(ModelBakery var0) {
      for (CustomItemProperties var5 : Z()) {
         var5.X(var0);
      }
   }

   public static void G() {
      for (CustomItemProperties var4 : Z()) {
         if (var4.type == 1) {
            TextureMap var5 = Minecraft.getMinecraft().getTextureMapBlocks();
            var4.updateModel(var5, itemModelGenerator);
            var4.h();
         }
      }
   }

   private static List<CustomItemProperties> Z() {
      ArrayList var0 = new ArrayList();
      V(itemProperties, var0);
      V(enchantmentProperties, var0);
      return var0;
   }

   private static void V(CustomItemProperties[][] var0, List<CustomItemProperties> var1) {
      if (var0 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            CustomItemProperties[] var6 = var0[var5];
            if (var6 != null) {
               for (int var7 = 0; var7 < var6.length; var7++) {
                  CustomItemProperties var8 = var6[var7];
                  if (var8 != null) {
                     var1.add(var8);
                  }
               }
            }
         }
      }
   }

   private static Map makeAutoImageProperties(IResourcePack var0) {
      HashMap var3 = new HashMap();
      var3.putAll(q(var0, "normal", Item.getIdFromItem(Items.potionitem)));
      var3.putAll(q(var0, "splash", Item.getIdFromItem(Items.potionitem)));
      var3.putAll(q(var0, "linger", Item.getIdFromItem(Items.potionitem)));
      return var3;
   }

   private static Map q(IResourcePack var0, String var1, int var2) {
      HashMap var6 = new HashMap();
      String var7 = var1 + "/";
      String[] var8 = new String[]{"mcpatcher/cit/potion/" + var7, "mcpatcher/cit/Potion/" + var7};
      String[] var9 = new String[]{".png"};
      String[] var10 = ResUtils.collectFiles(var0, var8, var9);

      for (int var11 = 0; var11 < var10.length; var11++) {
         String var12 = var10[var11];
         String var13 = StrUtils.removePrefixSuffix(var12, var8, var9);
         Properties var14 = Q(var13, var1, var2, var12);
         if (var14 != null) {
            String var15 = StrUtils.removeSuffix(var12, var9) + ".properties";
            CustomItemProperties var16 = new CustomItemProperties(var14, var15);
            var6.put(var15, var16);
         }
      }

      return var6;
   }

   private static Properties Q(String var0, String var1, int var2, String var3) {
      if (StrUtils.endsWith(var0, new String[]{"_n", "_s"})) {
         return null;
      }

      if (var0.equals("empty") && var1.equals("normal")) {
         var2 = Item.getIdFromItem(Items.glass_bottle);
         PropertiesOrdered var12 = new PropertiesOrdered();
         var12.put("type", "item");
         var12.put("items", "" + var2);
         return var12;
      }

      int[] var7 = (int[])getMapPotionIds().get(var0);
      if (var7 == null) {
         Config.warn("Potion not found for image: " + var3);
         return null;
      }

      StringBuffer var8 = new StringBuffer();

      for (int var9 = 0; var9 < var7.length; var9++) {
         int var10 = var7[var9];
         if (var1.equals("splash")) {
            var10 |= 16384;
         }

         if (var9 > 0) {
            var8.append(" ");
         }

         var8.append(var10);
      }

      int var13 = 16447;
      if (var0.equals("water") || var0.equals("mundane")) {
         var13 |= 64;
      }

      PropertiesOrdered var14 = new PropertiesOrdered();
      var14.put("type", "item");
      var14.put("items", "" + var2);
      var14.put("damage", "" + var8.toString());
      var14.put("damageMask", "" + var13);
      if (var1.equals("splash")) {
         var14.put("texture.potion_bottle_splash", var0);
      } else {
         var14.put("texture.potion_bottle_drinkable", var0);
      }

      return var14;
   }

   private static Map getMapPotionIds() {
      if (mapPotionIds == null) {
         mapPotionIds = new LinkedHashMap();
         mapPotionIds.put("water", G(0, 0));
         mapPotionIds.put("awkward", G(0, 1));
         mapPotionIds.put("thick", G(0, 2));
         mapPotionIds.put("potent", G(0, 3));
         mapPotionIds.put("regeneration", getPotionIds(1));
         mapPotionIds.put("movespeed", getPotionIds(2));
         mapPotionIds.put("fireresistance", getPotionIds(3));
         mapPotionIds.put("poison", getPotionIds(4));
         mapPotionIds.put("heal", getPotionIds(5));
         mapPotionIds.put("nightvision", getPotionIds(6));
         mapPotionIds.put("clear", G(7, 0));
         mapPotionIds.put("bungling", G(7, 1));
         mapPotionIds.put("charming", G(7, 2));
         mapPotionIds.put("rank", G(7, 3));
         mapPotionIds.put("weakness", getPotionIds(8));
         mapPotionIds.put("damageboost", getPotionIds(9));
         mapPotionIds.put("moveslowdown", getPotionIds(10));
         mapPotionIds.put("leaping", getPotionIds(11));
         mapPotionIds.put("harm", getPotionIds(12));
         mapPotionIds.put("waterbreathing", getPotionIds(13));
         mapPotionIds.put("invisibility", getPotionIds(14));
         mapPotionIds.put("thin", G(15, 0));
         mapPotionIds.put("debonair", G(15, 1));
         mapPotionIds.put("sparkling", G(15, 2));
         mapPotionIds.put("stinky", G(15, 3));
         mapPotionIds.put("mundane", G(0, 4));
         mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
         mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
         mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
         mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
         mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
         mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
         mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
         mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
      }

      return mapPotionIds;
   }

   private static int[] getPotionIds(int var0) {
      return new int[]{var0, var0 + 16, var0 + 32, var0 + 48};
   }

   private static int[] G(int var0, int var1) {
      return new int[]{var0 + var1 * 16};
   }

   private static List makePropertyList(CustomItemProperties[][] var0) {
      ArrayList var4 = new ArrayList();
      if (var0 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            CustomItemProperties[] var6 = var0[var5];
            ArrayList var7 = null;
            if (var6 != null) {
               var7 = new ArrayList<>(Arrays.asList(var6));
            }

            var4.add(var7);
         }
      }

      return var4;
   }

   private static CustomItemProperties[][] propertyListToArray(List var0) {
      CustomItemProperties[][] var4 = new CustomItemProperties[var0.size()][];

      for (int var5 = 0; var5 < var0.size(); var5++) {
         List var6 = (List)var0.get(var5);
         if (var6 != null) {
            CustomItemProperties[] var7 = (net.minecraft.src.CustomItemProperties[])(var6.toArray(new CustomItemProperties[var6.size()]));
            Arrays.sort(var7, new CustomItemsComparator());
            var4[var5] = var7;
         }
      }

      return var4;
   }

   private static void addToItemList(CustomItemProperties var0, List var1) {
      if (var0.items != null) {
         for (int var5 = 0; var5 < var0.items.length; var5++) {
            int var6 = var0.items[var5];
            if (var6 <= 0) {
               Config.warn("Invalid item ID: " + var6);
            } else {
               addToList(var0, var1, var6);
            }
         }
      }
   }

   private static void addToEnchantmentList(CustomItemProperties var0, List var1) {
      if (var0.type == 2 && var0.enchantmentIds != null) {
         for (int var5 = 0; var5 < 256; var5++) {
            if (var0.enchantmentIds.isInRange(var5)) {
               addToList(var0, var1, var5);
            }
         }
      }
   }

   private static void addToList(CustomItemProperties var0, List var1, int var2) {
      while (var2 >= var1.size()) {
         var1.add(null);
      }

      List var6 = (List)var1.get(var2);
      if (var6 == null) {
         var6 = new ArrayList();
         var1.set(var2, var6);
      }

      var6.add(var0);
   }

   public static IBakedModel r(ItemStack var0, IBakedModel var1, ResourceLocation var2, boolean var3) {
      if (!var3 && var1.isGui3d()) {
         return var1;
      }

      if (itemProperties == null) {
         return var1;
      }

      CustomItemProperties var7 = getCustomItemProperties(var0, 1);
      if (var7 == null) {
         return var1;
      }

      IBakedModel var8 = var7.T(var2, var3);
      return var8 != null ? var8 : var1;
   }

   public static boolean bindCustomArmorTexture(ItemStack var0, int var1, String var2) {
      if (itemProperties == null) {
         return false;
      }

      ResourceLocation var6 = getCustomArmorLocation(var0, var1, var2);
      if (var6 == null) {
         return false;
      }

      Config.getTextureManager().bindTexture(var6);
      return true;
   }

   private static ResourceLocation getCustomArmorLocation(ItemStack var0, int var1, String var2) {
      CustomItemProperties var6 = getCustomItemProperties(var0, 3);
      if (var6 == null) {
         return null;
      }

      if (var6.mapTextureLocations == null) {
         return var6.textureLocation;
      }

      Item var7 = var0.getItem();
      if (!(var7 instanceof ItemArmor)) {
         return null;
      }

      ItemArmor var8 = (ItemArmor)var7;
      String var9 = var8.getArmorMaterial().getName();
      StringBuffer var10 = new StringBuffer();
      var10.append("texture.");
      var10.append(var9);
      var10.append("_layer_");
      var10.append(var1);
      if (var2 != null) {
         var10.append("_");
         var10.append(var2);
      }

      String var11 = var10.toString();
      ResourceLocation var12 = (ResourceLocation)var6.mapTextureLocations.get(var11);
      return var12 == null ? var6.textureLocation : var12;
   }

   private static CustomItemProperties getCustomItemProperties(ItemStack var0, int var1) {
      if (itemProperties == null) {
         return null;
      }

      if (var0 == null) {
         return null;
      }

      Item var5 = var0.getItem();
      int var6 = Item.getIdFromItem(var5);
      if (var6 >= 0 && var6 < itemProperties.length) {
         CustomItemProperties[] var7 = itemProperties[var6];
         if (var7 != null) {
            for (int var8 = 0; var8 < var7.length; var8++) {
               CustomItemProperties var9 = var7[var8];
               if (var9.type == var1 && matchesProperties(var9, var0, (int[][])null)) {
                  return var9;
               }
            }
         }
      }

      return null;
   }

   private static boolean matchesProperties(CustomItemProperties var0, ItemStack var1, int[][] var2) {
      Item var6 = var1.getItem();
      if (var0.damage != null) {
         int var7 = var1.getItemDamage();
         if (var0.M != 0) {
            var7 &= var0.M;
         }

         if (var0.damagePercent) {
            int var8 = var6.getMaxDamage();
            var7 = (int)((double)(var7 * 100) / var8);
         }

         if (!var0.damage.isInRange(var7)) {
            return false;
         }
      }

      if (var0.stackSize != null && !var0.stackSize.isInRange(var1.stackSize)) {
         return false;
      }

      int[][] var11 = var2;
      if (var0.enchantmentIds != null) {
         if (var2 == null) {
            var11 = getEnchantmentIdLevels(var1);
         }

         boolean var12 = false;

         for (int var9 = 0; var9 < var11.length; var9++) {
            int var10 = var11[var9][0];
            if (var0.enchantmentIds.isInRange(var10)) {
               var12 = true;
               break;
            }
         }

         if (!var12) {
            return false;
         }
      }

      if (var0.enchantmentLevels != null) {
         if (var11 == null) {
            var11 = getEnchantmentIdLevels(var1);
         }

         boolean var13 = false;

         for (int var15 = 0; var15 < var11.length; var15++) {
            int var17 = var11[var15][1];
            if (var0.enchantmentLevels.isInRange(var17)) {
               var13 = true;
               break;
            }
         }

         if (!var13) {
            return false;
         }
      }

      if (var0.nbtTagValues != null) {
         NBTTagCompound var14 = var1.getTagCompound();

         for (int var16 = 0; var16 < var0.nbtTagValues.length; var16++) {
            NbtTagValue var18 = var0.nbtTagValues[var16];
            if (!var18.S(var14)) {
               return false;
            }
         }
      }

      if (var0.T != 0) {
         if (var0.T == 1 && w) {
            return false;
         }

         if (var0.T == 2 && !w) {
            return false;
         }
      }

      return true;
   }

   private static int[][] getEnchantmentIdLevels(ItemStack var0) {
      Item var4 = var0.getItem();
      NBTTagList var5 = var4 == Items.enchanted_book ? Items.enchanted_book.getEnchantments(var0) : var0.getEnchantmentTagList();
      if (var5 != null && var5.tagCount() > 0) {
         int[][] var6 = new int[var5.tagCount()][2];

         for (int var7 = 0; var7 < var5.tagCount(); var7++) {
            NBTTagCompound var8 = var5.getCompoundTagAt(var7);
            short var9 = var8.getShort("id");
            short var10 = var8.getShort("lvl");
            var6[var7][0] = var9;
            var6[var7][1] = var10;
         }

         return var6;
      } else {
         return EMPTY_INT2_ARRAY;
      }
   }

   public static boolean renderCustomEffect(RenderItem var0, ItemStack var1, IBakedModel var2) {
      long var3 = b ^ 83978114677339L;
      if (enchantmentProperties == null) {
         return false;
      }

      if (var1 == null) {
         return false;
      }

      int[][] var6 = getEnchantmentIdLevels(var1);
      if (var6.length <= 0) {
         return false;
      }

      HashSet var7 = null;
      boolean var8 = false;
      TextureManager var9 = Config.getTextureManager();

      for (int var10 = 0; var10 < var6.length; var10++) {
         int var11 = var6[var10][0];
         if (var11 >= 0 && var11 < enchantmentProperties.length) {
            CustomItemProperties[] var12 = enchantmentProperties[var11];
            if (var12 != null) {
               for (int var13 = 0; var13 < var12.length; var13++) {
                  CustomItemProperties var14 = var12[var13];
                  if (var7 == null) {
                     var7 = new HashSet();
                  }

                  if (var7.add(var11) && matchesProperties(var14, var1, var6) && var14.textureLocation != null) {
                     var9.bindTexture(var14.textureLocation);
                     float var15 = var14.getTextureWidth(var9);
                     if (!var8) {
                        var8 = true;
                        GlStateManager.depthMask(false);
                        long var10000 = 5327203315832794785L ^ var3;
                        GlStateManager.depthFunc(514);
                        GlStateManager.disableLighting();
                        var10000 = 3277289407484518067L ^ var3;
                        GlStateManager.matrixMode(5890);
                     }

                     Blender.setupBlend(var14.a, 1.0F);
                     GlStateManager.pushMatrix();
                     GlStateManager.scale(var15 / 2.0F, var15 / 2.0F, var15 / 2.0F);
                     float var16 = var14.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(var16, 0.0F, 0.0F);
                     GlStateManager.rotate(var14.rotation, 0.0F, 0.0F, 1.0F);
                     var0.renderModel(var2, -1);
                     GlStateManager.popMatrix();
                  }
               }
            }
         }
      }

      if (var8) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         long var18 = 3070177632880269204L ^ var3;
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         var18 = 6835243359347191745L ^ var3;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         var18 = 3989969245204080495L ^ var3;
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         var9.bindTexture(TextureMap.locationBlocksTexture);
      }

      return var8;
   }

   public static boolean renderCustomArmorEffect(
      EntityLivingBase var0, ItemStack var1, ModelBase var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9
   ) {
      long var10 = b ^ 47331842571197L;
      if (enchantmentProperties == null) {
         return false;
      }

      if (Config.isShaders() && Shaders.isShadowPass) {
         return false;
      }

      if (var1 == null) {
         return false;
      }

      int[][] var13 = getEnchantmentIdLevels(var1);
      if (var13.length <= 0) {
         return false;
      }

      HashSet var14 = null;
      boolean var15 = false;
      TextureManager var16 = Config.getTextureManager();

      for (int var17 = 0; var17 < var13.length; var17++) {
         int var18 = var13[var17][0];
         if (var18 >= 0 && var18 < enchantmentProperties.length) {
            CustomItemProperties[] var19 = enchantmentProperties[var18];
            if (var19 != null) {
               for (int var20 = 0; var20 < var19.length; var20++) {
                  CustomItemProperties var21 = var19[var20];
                  if (var14 == null) {
                     var14 = new HashSet();
                  }

                  if (var14.add(var18) && matchesProperties(var21, var1, var13) && var21.textureLocation != null) {
                     var16.bindTexture(var21.textureLocation);
                     float var22 = var21.getTextureWidth(var16);
                     if (!var15) {
                        var15 = true;
                        if (Config.isShaders()) {
                           ShadersRender.renderEnchantedGlintBegin();
                        }

                        GlStateManager.enableBlend();
                        GlStateManager.depthFunc(514);
                        GlStateManager.depthMask(false);
                     }

                     Blender.setupBlend(var21.a, 1.0F);
                     GlStateManager.disableLighting();
                     long var10000 = 5822200774183856347L ^ var10;
                     GlStateManager.matrixMode(5890);
                     GlStateManager.loadIdentity();
                     var10000 = 806422018449028303L ^ var10;
                     GlStateManager.rotate(var21.rotation, 0.0F, 0.0F, 1.0F);
                     float var23 = var22 / 8.0F;
                     GlStateManager.scale(var23, var23 / 2.0F, var23);
                     float var24 = var21.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(0.0F, var24, 0.0F);
                     GlStateManager.matrixMode(5888);
                     var2.render(var0, var3, var4, var6, var7, var8, var9);
                  }
               }
            }
         }
      }

      if (var15) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         long var26 = 7623064047634169961L ^ var10;
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         var26 = 5822200774183856347L ^ var10;
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         var26 = 806422018449028303L ^ var10;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         var26 = 6156567031697064803L ^ var10;
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }

      return var15;
   }

   public static boolean isUseGlint() {
      return useGlint;
   }

   static {
      long var11 = b ^ 43584037636005L;
      long var10000 = var11 ^ 205739534042558113L;
      var10000 = var11 ^ 2063618468754496992L;
      var10000 = var11 ^ 8422810505225007722L;
   }
}
