package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
   public String name = null;
   public String basePath = null;
   public int type = 1;
   public int[] items = null;
   public String texture = null;
   public Map<String, String> mapTextures = null;
   public String modelName = null;
   public Map<String, String> mapModelNames = null;
   public RangeListInt damage = null;
   public boolean damagePercent = false;
   public int damageMask = 0;
   public RangeListInt stackSize = null;
   public RangeListInt enchantmentIds = null;
   public RangeListInt enchantmentLevels = null;
   public NbtTagValue[] nbtTagValues = null;
   public int hand = 0;
   public int blend = 1;
   public float speed = 0.0F;
   public float rotation = 0.0F;
   public int layer = 0;
   public float duration = 1.0F;
   public int weight = 0;
   public ResourceLocation textureLocation = null;
   public Map mapTextureLocations = null;
   public TextureAtlasSprite sprite = null;
   public Map mapSprites = null;
   public IBakedModel model = null;
   public Map<String, IBakedModel> mapModels = null;
   public IBakedModel x = null;
   public Map<String, IBakedModel> mapBakedModelsFull = null;
   private int textureWidth = 0;
   private int textureHeight = 0;
   public static final int HAND_ANY = 0;
   public static final int TYPE_UNKNOWN = 1;
   public static final int HAND_MAIN = 2;
   public static final int TYPE_ARMOR = 3;
   public static final int TYPE_ITEM = 0;
   public static final int TYPE_ENCHANTMENT = 1;
   public static final int HAND_OFF = 2;
   public static final String INVENTORY = "inventory";

   public CustomItemProperties(Properties props, String path) {
      this.name = parseName(path);
      this.basePath = parseBasePath(path);
      this.type = this.parseType(props.getProperty("type"));
      this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
      this.mapModelNames = parseModels(props, this.basePath);
      this.modelName = parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModelNames);
      this.mapTextures = parseTextures(props, this.basePath);
      boolean bl = this.mapModelNames == null && this.modelName == null;
      this.texture = parseTexture(
         props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, bl
      );
      String string = props.getProperty("damage");
      if (string != null) {
         this.damagePercent = string.contains("%");
         string = string.replace("%", "");
         this.damage = this.parseRangeListInt(string);
         this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
      }

      this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
      this.enchantmentIds = this.parseRangeListIntWith(props.getProperty("enchantmentIDs"), new ParserEnchantmentId());
      this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
      this.nbtTagValues = this.parseNbtTagValues(props);
      this.hand = this.parseHand(props.getProperty("hand"));
      this.blend = Blender.parseBlend(props.getProperty("blend"));
      this.speed = this.parseFloat(props.getProperty("speed"), 0.0F);
      this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0F);
      this.layer = this.parseInt(props.getProperty("layer"), 0);
      this.weight = this.parseInt(props.getProperty("weight"), 0);
      this.duration = this.parseFloat(props.getProperty("duration"), 1.0F);
   }

   private static String parseName(String path) {
      String string = path;
      int i = path.lastIndexOf(47);
      if (i >= 0) {
         string = path.substring(i + 1);
      }

      int j = string.lastIndexOf(46);
      if (j >= 0) {
         string = string.substring(0, j);
      }

      return string;
   }

   private static String parseBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   private int parseType(String str) {
      if (str == null) {
         return 1;
      }

      if (str.equals("item")) {
         return 1;
      }

      if (str.equals("enchantment")) {
         return 2;
      }

      if (str.equals("armor")) {
         return 3;
      }

      Config.warn("Unknown method: " + str);
      return 0;
   }

   private int[] parseItems(String str, String str2) {
      if (str == null) {
         str = str2;
      }

      if (str == null) {
         return null;
      }

      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] strings = Config.tokenize(str, " ");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         int j = Config.parseInt(string, -1);
         if (j >= 0) {
            treeSet.add(new Integer(j));
         } else {
            if (string.contains("-")) {
               String[] strings2 = Config.tokenize(string, "-");
               if (strings2.length == 2) {
                  int k = Config.parseInt(strings2[0], -1);
                  int l = Config.parseInt(strings2[1], -1);
                  if (k >= 0 && l >= 0) {
                     int m = Math.min(k, l);
                     int n = Math.max(k, l);

                     for (int i2 = m; i2 <= n; i2++) {
                        treeSet.add(new Integer(i2));
                     }
                     continue;
                  }
               }
            }

            Item item = Item.getByNameOrId(string);
            if (item == null) {
               Config.warn("Item not found: " + string);
            } else {
               int i3 = Item.getIdFromItem(item);
               if (i3 <= 0) {
                  Config.warn("Item not found: " + string);
               } else {
                  treeSet.add(new Integer(i3));
               }
            }
         }
      }

      Integer[] integers = (java.lang.Integer[])(treeSet.toArray(new Integer[treeSet.size()]));
      int[] ints = new int[integers.length];

      for (int i4 = 0; i4 < ints.length; i4++) {
         ints[i4] = integers[i4];
      }

      return ints;
   }

   private static String parseTexture(String string, String string2, String string3, String string4, String string5, int i, Map<String, String> map, boolean bl) {
      if (string == null) {
         string = string2;
      }

      if (string == null) {
         string = string3;
      }

      if (string != null) {
         String string6 = ".png";
         if (string.endsWith(string6)) {
            string = string.substring(0, string.length() - string6.length());
         }

         return fixTextureName(string, string5);
      } else {
         if (i == 3) {
            return null;
         }

         if (map != null) {
            String string7 = (String)map.get("texture.bow_standby");
            if (string7 != null) {
               return string7;
            }
         }

         if (!bl) {
            return null;
         }

         String string8 = string4;
         int j = string4.lastIndexOf(47);
         if (j >= 0) {
            string8 = string4.substring(j + 1);
         }

         int k = string8.lastIndexOf(46);
         if (k >= 0) {
            string8 = string8.substring(0, k);
         }

         return fixTextureName(string8, string5);
      }
   }

   private static Map parseTextures(Properties props, String basePath) {
      String string = "texture.";
      Map map = getMatchingProperties(props, string);
      if (map.size() <= 0) {
         return null;
      }

      Set set = map.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object object : set) {
         String string2 = (String)object;
         String string3 = (String)map.get(string2);
         string3 = fixTextureName(string3, basePath);
         linkedHashMap.put(string2, string3);
      }

      return linkedHashMap;
   }

   private static String fixTextureName(String iconName, String basePath) {
      iconName = TextureUtils.fixResourcePath(iconName, basePath);
      if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
         iconName = basePath + "/" + iconName;
      }

      if (iconName.endsWith(".png")) {
         iconName = iconName.substring(0, iconName.length() - 4);
      }

      if (iconName.startsWith("/")) {
         iconName = iconName.substring(1);
      }

      return iconName;
   }

   private static String parseModel(String string, String string2, String string3, int i, Map<String, String> map) {
      if (string != null) {
         String string4 = ".json";
         if (string.endsWith(string4)) {
            string = string.substring(0, string.length() - string4.length());
         }

         return fixModelName(string, string3);
      } else {
         if (i == 3) {
            return null;
         }

         if (map != null) {
            String string5 = (String)map.get("model.bow_standby");
            if (string5 != null) {
               return string5;
            }
         }

         return string;
      }
   }

   private static Map parseModels(Properties properties, String string) {
      String string2 = "model.";
      Map map = getMatchingProperties(properties, string2);
      if (map.size() <= 0) {
         return null;
      }

      Set set = map.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object object : set) {
         String string3 = (String)object;
         String string4 = (String)map.get(string3);
         string4 = fixModelName(string4, string);
         linkedHashMap.put(string3, string4);
      }

      return linkedHashMap;
   }

   private static String fixModelName(String string, String string2) {
      string = TextureUtils.fixResourcePath(string, string2);
      boolean bl = string.startsWith("block/") || string.startsWith("item/");
      if (!string.startsWith(string2) && !bl && !string.startsWith("mcpatcher/")) {
         string = string2 + "/" + string;
      }

      String string3 = ".json";
      if (string.endsWith(string3)) {
         string = string.substring(0, string.length() - string3.length());
      }

      if (string.startsWith("/")) {
         string = string.substring(1);
      }

      return string;
   }

   private int parseInt(String str, int defVal) {
      if (str == null) {
         return defVal;
      } else {
         str = str.trim();
         int i = Config.parseInt(str, Integer.MIN_VALUE);
         if (i == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + str);
            return defVal;
         } else {
            return i;
         }
      }
   }

   private float parseFloat(String str, float defVal) {
      if (str == null) {
         return defVal;
      } else {
         str = str.trim();
         float f = Config.parseFloat(str, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + str);
            return defVal;
         } else {
            return f;
         }
      }
   }

   private RangeListInt parseRangeListInt(String str) {
      return this.parseRangeListIntWith(str, (IParserInt)null);
   }

   private RangeListInt parseRangeListIntWith(String string, IParserInt iParserInt) {
      if (string == null) {
         return null;
      }

      String[] strings = Config.tokenize(string, " ");
      RangeListInt rangeListInt = new RangeListInt();

      for (int i = 0; i < strings.length; i++) {
         String string2 = strings[i];
         if (iParserInt != null) {
            int j = iParserInt.parse(string2, Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
               rangeListInt.addRange(new RangeInt(j, j));
               continue;
            }
         }

         RangeInt rangeInt = this.parseRangeInt(string2);
         if (rangeInt == null) {
            Config.warn("Invalid range list: " + string);
            return null;
         }

         rangeListInt.addRange(rangeInt);
      }

      return rangeListInt;
   }

   private RangeInt parseRangeInt(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      int i = str.length() - str.replace("-", "").length();
      if (i > 1) {
         Config.warn("Invalid range: " + str);
         return null;
      }

      String[] strings = Config.tokenize(str, "- ");
      int[] ints = new int[strings.length];

      for (int j = 0; j < strings.length; j++) {
         String string = strings[j];
         int k = Config.parseInt(string, -1);
         if (k < 0) {
            Config.warn("Invalid range: " + str);
            return null;
         }

         ints[j] = k;
      }

      if (ints.length == 1) {
         int l = ints[0];
         if (str.startsWith("-")) {
            return new RangeInt(0, l);
         } else {
            return str.endsWith("-") ? new RangeInt(l, 65535) : new RangeInt(l, l);
         }
      } else if (ints.length == 2) {
         int m = Math.min(ints[0], ints[1]);
         int n = Math.max(ints[0], ints[1]);
         return new RangeInt(m, n);
      } else {
         Config.warn("Invalid range: " + str);
         return null;
      }
   }

   private NbtTagValue[] parseNbtTagValues(Properties props) {
      String string = "nbt.";
      Map map = getMatchingProperties(props, string);
      if (map.size() <= 0) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (Object object : map.keySet()) {
         String string2 = (String)object;
         String string3 = (String)map.get(string2);
         String string4 = string2.substring(string.length());
         NbtTagValue nbtTagValue = new NbtTagValue(string4, string3);
         arrayList.add(nbtTagValue);
      }

      return (net.minecraft.src.NbtTagValue[])(arrayList.toArray(new NbtTagValue[arrayList.size()]));
   }

   private static Map getMatchingProperties(Properties props, String keyPrefix) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object object : props.keySet()) {
         String string = (String)object;
         String string2 = props.getProperty(string);
         if (string.startsWith(keyPrefix)) {
            linkedHashMap.put(string, string2);
         }
      }

      return linkedHashMap;
   }

   private int parseHand(String string) {
      if (string == null) {
         return 0;
      }

      string = string.toLowerCase();
      if (string.equals("any")) {
         return 0;
      }

      if (string.equals("main")) {
         return 1;
      }

      if (string.equals("off")) {
         return 2;
      }

      Config.warn("Invalid hand: " + string);
      return 0;
   }

   public boolean isValid(String path) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + path);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + path);
         return false;
      }

      if (this.type == 0) {
         Config.warn("No type defined: " + path);
         return false;
      }

      if (this.type == 1 || this.type == 3) {
         if (this.items == null) {
            this.items = this.detectItems();
         }

         if (this.items == null) {
            Config.warn("No items defined: " + path);
            return false;
         }
      }

      if (this.texture == null && this.mapTextures == null && this.modelName == null && this.mapModelNames == null) {
         Config.warn("No texture or model specified: " + path);
         return false;
      } else if (this.type == 2 && this.enchantmentIds == null) {
         Config.warn("No enchantmentIDs specified: " + path);
         return false;
      } else {
         return true;
      }
   }

   private int[] detectItems() {
      Item item = Item.getByNameOrId(this.name);
      if (item == null) {
         return null;
      }

      int i = Item.getIdFromItem(item);
      return i <= 0 ? null : new int[]{i};
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.texture != null) {
         this.textureLocation = this.getTextureLocation(this.texture);
         if (this.type == 1) {
            ResourceLocation resourceLocation = this.getSpriteLocation(this.textureLocation);
            this.sprite = textureMap.registerSprite(resourceLocation);
         }
      }

      if (this.mapTextures != null) {
         this.mapTextureLocations = new HashMap();
         this.mapSprites = new HashMap();

         for (String string : this.mapTextures.keySet()) {
            String string2 = this.mapTextures.get(string);
            ResourceLocation resourceLocation2 = this.getTextureLocation(string2);
            this.mapTextureLocations.put(string, resourceLocation2);
            if (this.type == 1) {
               ResourceLocation resourceLocation3 = this.getSpriteLocation(resourceLocation2);
               TextureAtlasSprite textureAtlasSprite = textureMap.registerSprite(resourceLocation3);
               this.mapSprites.put(string, textureAtlasSprite);
            }
         }
      }
   }

   private ResourceLocation getTextureLocation(String texName) {
      if (texName == null) {
         return null;
      }

      ResourceLocation resourceLocation = new ResourceLocation(texName);
      String string = resourceLocation.getResourceDomain();
      String string2 = resourceLocation.getResourcePath();
      if (!string2.contains("/")) {
         string2 = "textures/items/" + string2;
      }

      String string3 = string2 + ".png";
      ResourceLocation resourceLocation2 = new ResourceLocation(string, string3);
      boolean bl = Config.hasResource(resourceLocation2);
      if (!bl) {
         Config.warn("File not found: " + string3);
      }

      return resourceLocation2;
   }

   private ResourceLocation getSpriteLocation(ResourceLocation resLoc) {
      String string = resLoc.getResourcePath();
      string = StrUtils.removePrefix(string, "textures/");
      string = StrUtils.removeSuffix(string, ".png");
      return new ResourceLocation(resLoc.getResourceDomain(), string);
   }

   public void updateModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
      if (this.texture != null || this.mapTextures != null) {
         String[] strings = this.getModelTextures();
         boolean bl = this.isUseTint();
         this.model = makeBakedModel(textureMap, itemModelGenerator, strings, bl);
         if (this.type == 1 && this.mapTextures != null) {
            for (String string : this.mapTextures.keySet()) {
               String string2 = this.mapTextures.get(string);
               String string3 = StrUtils.removePrefix(string, "texture.");
               if (string3.startsWith("bow") || string3.startsWith("fishing_rod") || string3.startsWith("shield")) {
                  String[] strings2 = new String[]{string2};
                  IBakedModel iBakedModel = makeBakedModel(textureMap, itemModelGenerator, strings2, bl);
                  if (this.mapModels == null) {
                     this.mapModels = new HashMap<>();
                  }

                  String string4 = "item/" + string3;
                  this.mapModels.put(string4, iBakedModel);
               }
            }
         }
      }
   }

   private boolean isUseTint() {
      return true;
   }

   private static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint) {
      String[] strings = new String[textures.length];

      for (int i = 0; i < strings.length; i++) {
         String string = textures[i];
         strings[i] = StrUtils.removePrefix(string, "textures/");
      }

      ModelBlock modelBlock = makeModelBlock(strings);
      ModelBlock modelBlock2 = itemModelGenerator.makeItemModel(textureMap, modelBlock);
      return bakeModel(textureMap, modelBlock2, useTint);
   }

   private String[] getModelTextures() {
      if (this.type == 1 && this.items.length == 1) {
         Item item = Item.getItemById(this.items[0]);
         if (item == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
            RangeInt rangeInt = this.damage.getRange(0);
            int i = rangeInt.getMin();
            boolean bl = (i & 16384) != 0;
            String string = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
            String string2 = null;
            if (bl) {
               string2 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
            } else {
               string2 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
            }

            return new String[]{string, string2};
         }

         if (item instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)item;
            if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
               String string3 = "leather";
               String string4 = "helmet";
               if (itemArmor.armorType == 0) {
                  string4 = "helmet";
               }

               if (itemArmor.armorType == 1) {
                  string4 = "chestplate";
               }

               if (itemArmor.armorType == 2) {
                  string4 = "leggings";
               }

               if (itemArmor.armorType == 3) {
                  string4 = "boots";
               }

               String string5 = string3 + "_" + string4;
               String string6 = this.getMapTexture(this.mapTextures, "texture." + string5, "items/" + string5);
               String string7 = this.getMapTexture(this.mapTextures, "texture." + string5 + "_overlay", "items/" + string5 + "_overlay");
               return new String[]{string6, string7};
            }
         }
      }

      return new String[]{this.texture};
   }

   private String getMapTexture(Map<String, String> map, String key, String def) {
      if (map == null) {
         return def;
      }

      String string = (String)map.get(key);
      return string == null ? def : string;
   }

   private static ModelBlock makeModelBlock(String[] modelTextures) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");

      for (int i = 0; i < modelTextures.length; i++) {
         String string = modelTextures[i];
         if (i > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append("\"layer" + i + "\": \"" + string + "\"");
      }

      stringBuffer.append("}}");
      String string2 = stringBuffer.toString();
      return ModelBlock.deserialize(string2);
   }

   private static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlockIn, boolean useTint) {
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      boolean bl = false;
      String string = modelBlockIn.resolveTextureName("particle");
      TextureAtlasSprite textureAtlasSprite = textureMap.getAtlasSprite(new ResourceLocation(string).toString());
      SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(modelBlockIn).setTexture(textureAtlasSprite);

      for (BlockPart blockPart : modelBlockIn.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            if (!useTint) {
               blockPartFace = new BlockPartFace(blockPartFace.cullFace, -1, blockPartFace.texture, blockPartFace.blockFaceUV);
            }

            String string2 = modelBlockIn.resolveTextureName(blockPartFace.texture);
            TextureAtlasSprite textureAtlasSprite2 = textureMap.getAtlasSprite(new ResourceLocation(string2).toString());
            BakedQuad bakedQuad = makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, modelRotation, bl);
            if (blockPartFace.cullFace == null) {
               builder.addGeneralQuad(bakedQuad);
            } else {
               builder.addFaceQuad(modelRotation.rotateFace(blockPartFace.cullFace), bakedQuad);
            }
         }
      }

      return builder.makeBakedModel();
   }

   private static BakedQuad makeBakedQuad(
      BlockPart blockPart,
      BlockPartFace blockPartFace,
      TextureAtlasSprite textureAtlasSprite,
      EnumFacing enumFacing,
      ModelRotation modelRotation,
      boolean uvLocked
   ) {
      FaceBakery faceBakery = new FaceBakery();
      return faceBakery.makeBakedQuad(
         blockPart.positionFrom,
         blockPart.positionTo,
         blockPartFace,
         textureAtlasSprite,
         enumFacing,
         modelRotation,
         blockPart.partRotation,
         uvLocked,
         blockPart.shade
      );
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
   }

   public float getTextureWidth(TextureManager textureManager) {
      if (this.textureWidth <= 0) {
         if (this.textureLocation != null) {
            ITextureObject iTextureObject = textureManager.getTexture(this.textureLocation);
            int i = iTextureObject.getGlTextureId();
            int j = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(i);
            this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            GlStateManager.bindTexture(j);
         }

         if (this.textureWidth <= 0) {
            this.textureWidth = 16;
         }
      }

      return this.textureWidth;
   }

   public float getTextureHeight(TextureManager textureManager) {
      if (this.textureHeight <= 0) {
         if (this.textureLocation != null) {
            ITextureObject iTextureObject = textureManager.getTexture(this.textureLocation);
            int i = iTextureObject.getGlTextureId();
            int j = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(i);
            this.textureHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
            GlStateManager.bindTexture(j);
         }

         if (this.textureHeight <= 0) {
            this.textureHeight = 16;
         }
      }

      return this.textureHeight;
   }

   public IBakedModel getBakedModel(ResourceLocation resourceLocation, boolean bl) {
      IBakedModel iBakedModel;
      Map map;
      if (bl) {
         iBakedModel = this.x;
         map = this.mapBakedModelsFull;
      } else {
         iBakedModel = this.model;
         map = this.mapModels;
      }

      if (resourceLocation != null && map != null) {
         String string = resourceLocation.getResourcePath();
         IBakedModel iBakedModel2 = (IBakedModel)map.get(string);
         if (iBakedModel2 != null) {
            return iBakedModel2;
         }
      }

      return iBakedModel;
   }

   public void loadModels(ModelBakery modelBakery) {
      if (this.modelName != null) {
         loadItemModel(modelBakery, this.modelName);
      }

      if (this.type == 1 && this.mapModelNames != null) {
         for (String string : this.mapModelNames.keySet()) {
            String string2 = this.mapModelNames.get(string);
            String string3 = StrUtils.removePrefix(string, "model.");
            if (string3.startsWith("bow") || string3.startsWith("fishing_rod") || string3.startsWith("shield")) {
               loadItemModel(modelBakery, string2);
            }
         }
      }
   }

   public void updateModelsFull() {
      ModelManager modelManager = Config.getModelManager();
      IBakedModel iBakedModel = modelManager.getMissingModel();
      if (this.modelName != null) {
         ResourceLocation resourceLocation = y(this.modelName);
         ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourceLocation, "inventory");
         this.x = modelManager.getModel(modelResourceLocation);
         if (this.x == iBakedModel) {
            Config.warn("Custom Items: Model not found " + modelResourceLocation.getResourcePath());
            this.x = null;
         }
      }

      if (this.type == 1 && this.mapModelNames != null) {
         for (String string : this.mapModelNames.keySet()) {
            String string2 = this.mapModelNames.get(string);
            String string3 = StrUtils.removePrefix(string, "model.");
            if (string3.startsWith("bow") || string3.startsWith("fishing_rod") || string3.startsWith("shield")) {
               ResourceLocation resourceLocation2 = y(string2);
               ModelResourceLocation modelResourceLocation2 = new ModelResourceLocation(resourceLocation2, "inventory");
               IBakedModel iBakedModel2 = modelManager.getModel(modelResourceLocation2);
               if (iBakedModel2 == iBakedModel) {
                  Config.warn("Custom Items: Model not found " + modelResourceLocation2.getResourcePath());
               } else {
                  if (this.mapBakedModelsFull == null) {
                     this.mapBakedModelsFull = new HashMap<>();
                  }

                  String string4 = "item/" + string3;
                  this.mapBakedModelsFull.put(string4, iBakedModel2);
               }
            }
         }
      }
   }

   private static void loadItemModel(ModelBakery modelBakery, String string) {
      ResourceLocation resourceLocation = y(string);
      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourceLocation, "inventory");
      modelBakery.loadItemModel(resourceLocation.toString(), modelResourceLocation, resourceLocation);
   }

   private static ResourceLocation y(String string) {
      return Reflector.ModelLoader.exists() && !string.startsWith("mcpatcher/") && !string.startsWith("optifine/")
         ? new ResourceLocation("models/" + string)
         : new ResourceLocation(string);
   }
}
