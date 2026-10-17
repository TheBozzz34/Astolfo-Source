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
   private static boolean renderOffHand = false;
   public static final int MASK_POTION_SPLASH = 16384;
   public static final int MASK_POTION_NAME = 63;
   public static final int MASK_POTION_EXTENDED = 64;
   public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
   public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
   public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
   public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
   public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
   public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
   private static final int[][] EMPTY_INT2_ARRAY = new int[0][];

   public static void update() {
      itemProperties = (CustomItemProperties[][])null;
      enchantmentProperties = (CustomItemProperties[][])null;
      useGlint = true;
      if (Config.isCustomItems()) {
         readCitProperties("mcpatcher/cit.properties");
         IResourcePack[] iResourcePacks = Config.getResourcePacks();

         for (int i = iResourcePacks.length - 1; i >= 0; i--) {
            IResourcePack iResourcePack = iResourcePacks[i];
            x(iResourcePack);
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

   private static void readCitProperties(String fileName) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(fileName);
         InputStream inputStream = Config.getResourceStream(resourceLocation);
         if (inputStream == null) {
            return;
         }

         Config.dbg("CustomItems: Loading " + fileName);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(inputStream);
         inputStream.close();
         useGlint = Config.parseBoolean(propertiesOrdered.getProperty("useGlint"), true);
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private static void x(IResourcePack iResourcePack) {
      String[] strings = ResUtils.collectFiles(iResourcePack, "mcpatcher/cit/", ".properties", (String[])null);
      Map map = makeAutoImageProperties(iResourcePack);
      if (map.size() > 0) {
         Set set = map.keySet();
         String[] strings2 = (java.lang.String[])(set.toArray(new String[set.size()]));
         strings = (String[])Config.addObjectsToArray(strings, strings2);
      }

      Arrays.sort(strings);
      List list = makePropertyList(itemProperties);
      List list2 = makePropertyList(enchantmentProperties);

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         Config.dbg("CustomItems: " + string);

         try {
            CustomItemProperties customItemProperties = null;
            if (map.containsKey(string)) {
               customItemProperties = (CustomItemProperties)map.get(string);
            }

            if (customItemProperties == null) {
               ResourceLocation resourceLocation = new ResourceLocation(string);
               InputStream inputStream = iResourcePack.getInputStream(resourceLocation);
               if (inputStream == null) {
                  Config.warn("CustomItems file not found: " + string);
                  continue;
               }

               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               customItemProperties = new CustomItemProperties(propertiesOrdered, string);
            }

            if (customItemProperties.isValid(string)) {
               addToItemList(customItemProperties, list);
               addToEnchantmentList(customItemProperties, list2);
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("CustomItems file not found: " + string);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      itemProperties = propertyListToArray(list);
      enchantmentProperties = propertyListToArray(list2);
      Comparator comparator = getPropertiesComparator();

      for (int j = 0; j < itemProperties.length; j++) {
         CustomItemProperties[] customItemPropertiess = itemProperties[j];
         if (customItemPropertiess != null) {
            Arrays.sort(customItemPropertiess, comparator);
         }
      }

      for (int k = 0; k < enchantmentProperties.length; k++) {
         CustomItemProperties[] customItemPropertiess2 = enchantmentProperties[k];
         if (customItemPropertiess2 != null) {
            Arrays.sort(customItemPropertiess2, comparator);
         }
      }
   }

   private static Comparator getPropertiesComparator() {
      return new Comparator() {
         @Override
         public int compare(Object o1, Object o2) {
            CustomItemProperties customItemProperties = (CustomItemProperties)o1;
            CustomItemProperties customItemProperties2 = (CustomItemProperties)o2;
            return customItemProperties.layer != customItemProperties2.layer
               ? customItemProperties.layer - customItemProperties2.layer
               : (
                  customItemProperties.weight != customItemProperties2.weight
                     ? customItemProperties2.weight - customItemProperties.weight
                     : (!customItemProperties.basePath.equals(customItemProperties2.basePath) ? customItemProperties.basePath.compareTo(customItemProperties2.basePath) : customItemProperties.name.compareTo(customItemProperties2.name))
               );
         }
      };
   }

   public static void updateIcons(TextureMap textureMap) {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         customItemProperties.updateIcons(textureMap);
      }
   }

   public static void loadModels(ModelBakery modelBakery) {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         customItemProperties.loadModels(modelBakery);
      }
   }

   public static void updateModels() {
      for (CustomItemProperties customItemProperties : getAllProperties()) {
         if (customItemProperties.type == 1) {
            TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
            customItemProperties.updateModel(textureMap, itemModelGenerator);
            customItemProperties.updateModelsFull();
         }
      }
   }

   private static List<CustomItemProperties> getAllProperties() {
      ArrayList arrayList = new ArrayList();
      addAll(itemProperties, arrayList);
      addAll(enchantmentProperties, arrayList);
      return arrayList;
   }

   private static void addAll(CustomItemProperties[][] customItemPropertiess, List<CustomItemProperties> list) {
      if (customItemPropertiess != null) {
         for (int i = 0; i < customItemPropertiess.length; i++) {
            CustomItemProperties[] customItemPropertiess2 = customItemPropertiess[i];
            if (customItemPropertiess2 != null) {
               for (int j = 0; j < customItemPropertiess2.length; j++) {
                  CustomItemProperties customItemProperties = customItemPropertiess2[j];
                  if (customItemProperties != null) {
                     list.add(customItemProperties);
                  }
               }
            }
         }
      }
   }

   private static Map makeAutoImageProperties(IResourcePack rp) {
      HashMap hashMap = new HashMap();
      hashMap.putAll(makePotionImageProperties(rp, "normal", Item.getIdFromItem(Items.potionitem)));
      hashMap.putAll(makePotionImageProperties(rp, "splash", Item.getIdFromItem(Items.potionitem)));
      hashMap.putAll(makePotionImageProperties(rp, "linger", Item.getIdFromItem(Items.potionitem)));
      return hashMap;
   }

   private static Map makePotionImageProperties(IResourcePack iResourcePack, String string, int i) {
      HashMap hashMap = new HashMap();
      String string2 = string + "/";
      String[] strings = new String[]{"mcpatcher/cit/potion/" + string2, "mcpatcher/cit/Potion/" + string2};
      String[] strings2 = new String[]{".png"};
      String[] strings3 = ResUtils.collectFiles(iResourcePack, strings, strings2);

      for (int j = 0; j < strings3.length; j++) {
         String string3 = strings3[j];
         String string4 = StrUtils.removePrefixSuffix(string3, strings, strings2);
         Properties properties2 = makePotionProperties(string4, string, i, string3);
         if (properties2 != null) {
            String string5 = StrUtils.removeSuffix(string3, strings2) + ".properties";
            CustomItemProperties customItemProperties = new CustomItemProperties(properties2, string5);
            hashMap.put(string5, customItemProperties);
         }
      }

      return hashMap;
   }

   private static Properties makePotionProperties(String string, String string2, int i, String string3) {
      if (StrUtils.endsWith(string, new String[]{"_n", "_s"})) {
         return null;
      }

      if (string.equals("empty") && string2.equals("normal")) {
         i = Item.getIdFromItem(Items.glass_bottle);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.put("type", "item");
         propertiesOrdered.put("items", "" + i);
         return propertiesOrdered;
      }

      int[] ints = (int[])getMapPotionIds().get(string);
      if (ints == null) {
         Config.warn("Potion not found for image: " + string3);
         return null;
      }

      StringBuffer stringBuffer = new StringBuffer();

      for (int j = 0; j < ints.length; j++) {
         int k = ints[j];
         if (string2.equals("splash")) {
            k |= 16384;
         }

         if (j > 0) {
            stringBuffer.append(" ");
         }

         stringBuffer.append(k);
      }

      int l = 16447;
      if (string.equals("water") || string.equals("mundane")) {
         l |= 64;
      }

      PropertiesOrdered propertiesOrdered2 = new PropertiesOrdered();
      propertiesOrdered2.put("type", "item");
      propertiesOrdered2.put("items", "" + i);
      propertiesOrdered2.put("damage", "" + stringBuffer.toString());
      propertiesOrdered2.put("damageMask", "" + l);
      if (string2.equals("splash")) {
         propertiesOrdered2.put("texture.potion_bottle_splash", string);
      } else {
         propertiesOrdered2.put("texture.potion_bottle_drinkable", string);
      }

      return propertiesOrdered2;
   }

   private static Map getMapPotionIds() {
      if (mapPotionIds == null) {
         mapPotionIds = new LinkedHashMap();
         mapPotionIds.put("water", getPotionId(0, 0));
         mapPotionIds.put("awkward", getPotionId(0, 1));
         mapPotionIds.put("thick", getPotionId(0, 2));
         mapPotionIds.put("potent", getPotionId(0, 3));
         mapPotionIds.put("regeneration", getPotionIds(1));
         mapPotionIds.put("movespeed", getPotionIds(2));
         mapPotionIds.put("fireresistance", getPotionIds(3));
         mapPotionIds.put("poison", getPotionIds(4));
         mapPotionIds.put("heal", getPotionIds(5));
         mapPotionIds.put("nightvision", getPotionIds(6));
         mapPotionIds.put("clear", getPotionId(7, 0));
         mapPotionIds.put("bungling", getPotionId(7, 1));
         mapPotionIds.put("charming", getPotionId(7, 2));
         mapPotionIds.put("rank", getPotionId(7, 3));
         mapPotionIds.put("weakness", getPotionIds(8));
         mapPotionIds.put("damageboost", getPotionIds(9));
         mapPotionIds.put("moveslowdown", getPotionIds(10));
         mapPotionIds.put("leaping", getPotionIds(11));
         mapPotionIds.put("harm", getPotionIds(12));
         mapPotionIds.put("waterbreathing", getPotionIds(13));
         mapPotionIds.put("invisibility", getPotionIds(14));
         mapPotionIds.put("thin", getPotionId(15, 0));
         mapPotionIds.put("debonair", getPotionId(15, 1));
         mapPotionIds.put("sparkling", getPotionId(15, 2));
         mapPotionIds.put("stinky", getPotionId(15, 3));
         mapPotionIds.put("mundane", getPotionId(0, 4));
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

   private static int[] getPotionIds(int baseId) {
      return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
   }

   private static int[] getPotionId(int i, int j) {
      return new int[]{i + j * 16};
   }

   private static List makePropertyList(CustomItemProperties[][] propsArr) {
      ArrayList arrayList = new ArrayList();
      if (propsArr != null) {
         for (int i = 0; i < propsArr.length; i++) {
            CustomItemProperties[] customItemPropertiess = propsArr[i];
            ArrayList arrayList2 = null;
            if (customItemPropertiess != null) {
               arrayList2 = new ArrayList<>(Arrays.asList(customItemPropertiess));
            }

            arrayList.add(arrayList2);
         }
      }

      return arrayList;
   }

   private static CustomItemProperties[][] propertyListToArray(List list) {
      CustomItemProperties[][] customItemPropertiesss = new CustomItemProperties[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            CustomItemProperties[] customItemPropertiess = (net.minecraft.src.CustomItemProperties[])(list2.toArray(new CustomItemProperties[list2.size()]));
            Arrays.sort(customItemPropertiess, new CustomItemsComparator());
            customItemPropertiesss[i] = customItemPropertiess;
         }
      }

      return customItemPropertiesss;
   }

   private static void addToItemList(CustomItemProperties cp, List itemList) {
      if (cp.items != null) {
         for (int i = 0; i < cp.items.length; i++) {
            int j = cp.items[i];
            if (j <= 0) {
               Config.warn("Invalid item ID: " + j);
            } else {
               addToList(cp, itemList, j);
            }
         }
      }
   }

   private static void addToEnchantmentList(CustomItemProperties cp, List enchantmentList) {
      if (cp.type == 2 && cp.enchantmentIds != null) {
         for (int i = 0; i < 256; i++) {
            if (cp.enchantmentIds.isInRange(i)) {
               addToList(cp, enchantmentList, i);
            }
         }
      }
   }

   private static void addToList(CustomItemProperties cp, List list, int id) {
      while (id >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(id);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(id, list2);
      }

      list2.add(cp);
   }

   public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel iBakedModel, ResourceLocation resourceLocation, boolean bl) {
      if (!bl && iBakedModel.isGui3d()) {
         return iBakedModel;
      }

      if (itemProperties == null) {
         return iBakedModel;
      }

      CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 1);
      if (customItemProperties == null) {
         return iBakedModel;
      }

      IBakedModel iBakedModel2 = customItemProperties.getBakedModel(resourceLocation, bl);
      return iBakedModel2 != null ? iBakedModel2 : iBakedModel;
   }

   public static boolean bindCustomArmorTexture(ItemStack itemStack, int layer, String overlay) {
      if (itemProperties == null) {
         return false;
      }

      ResourceLocation resourceLocation = getCustomArmorLocation(itemStack, layer, overlay);
      if (resourceLocation == null) {
         return false;
      }

      Config.getTextureManager().bindTexture(resourceLocation);
      return true;
   }

   private static ResourceLocation getCustomArmorLocation(ItemStack itemStack, int layer, String overlay) {
      CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 3);
      if (customItemProperties == null) {
         return null;
      }

      if (customItemProperties.mapTextureLocations == null) {
         return customItemProperties.textureLocation;
      }

      Item item = itemStack.getItem();
      if (!(item instanceof ItemArmor)) {
         return null;
      }

      ItemArmor itemArmor = (ItemArmor)item;
      String string = itemArmor.getArmorMaterial().getName();
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("texture.");
      stringBuffer.append(string);
      stringBuffer.append("_layer_");
      stringBuffer.append(layer);
      if (overlay != null) {
         stringBuffer.append("_");
         stringBuffer.append(overlay);
      }

      String string2 = stringBuffer.toString();
      ResourceLocation resourceLocation = (ResourceLocation)customItemProperties.mapTextureLocations.get(string2);
      return resourceLocation == null ? customItemProperties.textureLocation : resourceLocation;
   }

   private static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int typeIn) {
      if (itemProperties == null) {
         return null;
      }

      if (itemStack == null) {
         return null;
      }

      Item item = itemStack.getItem();
      int i = Item.getIdFromItem(item);
      if (i >= 0 && i < itemProperties.length) {
         CustomItemProperties[] customItemPropertiess = itemProperties[i];
         if (customItemPropertiess != null) {
            for (int j = 0; j < customItemPropertiess.length; j++) {
               CustomItemProperties customItemProperties = customItemPropertiess[j];
               if (customItemProperties.type == typeIn && matchesProperties(customItemProperties, itemStack, (int[][])null)) {
                  return customItemProperties;
               }
            }
         }
      }

      return null;
   }

   private static boolean matchesProperties(CustomItemProperties cip, ItemStack itemStack, int[][] enchantmentIdLevels) {
      Item item = itemStack.getItem();
      if (cip.damage != null) {
         int i = itemStack.getItemDamage();
         if (cip.damageMask != 0) {
            i &= cip.damageMask;
         }

         if (cip.damagePercent) {
            int j = item.getMaxDamage();
            i = (int)((double)(i * 100) / j);
         }

         if (!cip.damage.isInRange(i)) {
            return false;
         }
      }

      if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.stackSize)) {
         return false;
      }

      int[][] intss = enchantmentIdLevels;
      if (cip.enchantmentIds != null) {
         if (enchantmentIdLevels == null) {
            intss = getEnchantmentIdLevels(itemStack);
         }

         boolean bl = false;

         for (int k = 0; k < intss.length; k++) {
            int l = intss[k][0];
            if (cip.enchantmentIds.isInRange(l)) {
               bl = true;
               break;
            }
         }

         if (!bl) {
            return false;
         }
      }

      if (cip.enchantmentLevels != null) {
         if (intss == null) {
            intss = getEnchantmentIdLevels(itemStack);
         }

         boolean bl2 = false;

         for (int m = 0; m < intss.length; m++) {
            int n = intss[m][1];
            if (cip.enchantmentLevels.isInRange(n)) {
               bl2 = true;
               break;
            }
         }

         if (!bl2) {
            return false;
         }
      }

      if (cip.nbtTagValues != null) {
         NBTTagCompound nbtTagCompound = itemStack.getTagCompound();

         for (int i2 = 0; i2 < cip.nbtTagValues.length; i2++) {
            NbtTagValue nbtTagValue = cip.nbtTagValues[i2];
            if (!nbtTagValue.matches(nbtTagCompound)) {
               return false;
            }
         }
      }

      if (cip.hand != 0) {
         if (cip.hand == 1 && renderOffHand) {
            return false;
         }

         if (cip.hand == 2 && !renderOffHand) {
            return false;
         }
      }

      return true;
   }

   private static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
      Item item = itemStack.getItem();
      NBTTagList nbtTagList = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();
      if (nbtTagList != null && nbtTagList.tagCount() > 0) {
         int[][] intss = new int[nbtTagList.tagCount()][2];

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
            short s = nbtTagCompound.getShort("id");
            short s2 = nbtTagCompound.getShort("lvl");
            intss[i][0] = s;
            intss[i][1] = s2;
         }

         return intss;
      } else {
         return EMPTY_INT2_ARRAY;
      }
   }

   public static boolean renderCustomEffect(RenderItem renderItem, ItemStack itemStack, IBakedModel model) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] intss = getEnchantmentIdLevels(itemStack);
      if (intss.length <= 0) {
         return false;
      }

      HashSet hashSet = null;
      boolean bl = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int i = 0; i < intss.length; i++) {
         int j = intss[i][0];
         if (j >= 0 && j < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertiess = enchantmentProperties[j];
            if (customItemPropertiess != null) {
               for (int k = 0; k < customItemPropertiess.length; k++) {
                  CustomItemProperties customItemProperties = customItemPropertiess[k];
                  if (hashSet == null) {
                     hashSet = new HashSet();
                  }

                  if (hashSet.add(j) && matchesProperties(customItemProperties, itemStack, intss) && customItemProperties.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties.textureLocation);
                     float f = customItemProperties.getTextureWidth(textureManager);
                     if (!bl) {
                        bl = true;
                        GlStateManager.depthMask(false);
                        GlStateManager.depthFunc(514);
                        GlStateManager.disableLighting();
                        GlStateManager.matrixMode(5890);
                     }

                     Blender.setupBlend(customItemProperties.blend, 1.0F);
                     GlStateManager.pushMatrix();
                     GlStateManager.scale(f / 2.0F, f / 2.0F, f / 2.0F);
                     float g = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(g, 0.0F, 0.0F);
                     GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                     renderItem.renderModel(model, -1);
                     GlStateManager.popMatrix();
                  }
               }
            }
         }
      }

      if (bl) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         textureManager.bindTexture(TextureMap.locationBlocksTexture);
      }

      return bl;
   }

   public static boolean renderCustomArmorEffect(
      EntityLivingBase entity,
      ItemStack itemStack,
      ModelBase model,
      float limbSwing,
      float prevLimbSwing,
      float partialTicks,
      float timeLimbSwing,
      float yaw,
      float pitch,
      float scale
   ) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (Config.isShaders() && Shaders.isShadowPass) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] intss = getEnchantmentIdLevels(itemStack);
      if (intss.length <= 0) {
         return false;
      }

      HashSet hashSet = null;
      boolean bl = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int i = 0; i < intss.length; i++) {
         int j = intss[i][0];
         if (j >= 0 && j < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertiess = enchantmentProperties[j];
            if (customItemPropertiess != null) {
               for (int k = 0; k < customItemPropertiess.length; k++) {
                  CustomItemProperties customItemProperties = customItemPropertiess[k];
                  if (hashSet == null) {
                     hashSet = new HashSet();
                  }

                  if (hashSet.add(j) && matchesProperties(customItemProperties, itemStack, intss) && customItemProperties.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties.textureLocation);
                     float f = customItemProperties.getTextureWidth(textureManager);
                     if (!bl) {
                        bl = true;
                        if (Config.isShaders()) {
                           ShadersRender.renderEnchantedGlintBegin();
                        }

                        GlStateManager.enableBlend();
                        GlStateManager.depthFunc(514);
                        GlStateManager.depthMask(false);
                     }

                     Blender.setupBlend(customItemProperties.blend, 1.0F);
                     GlStateManager.disableLighting();
                     GlStateManager.matrixMode(5890);
                     GlStateManager.loadIdentity();
                     GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                     float g = f / 8.0F;
                     GlStateManager.scale(g, g / 2.0F, g);
                     float h = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(0.0F, h, 0.0F);
                     GlStateManager.matrixMode(5888);
                     model.render(entity, limbSwing, prevLimbSwing, timeLimbSwing, yaw, pitch, scale);
                  }
               }
            }
         }
      }

      if (bl) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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

      return bl;
   }

   public static boolean isUseGlint() {
      return useGlint;
   }
}
