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
   public String e = null;
   public Map<String, String> B = null;
   public RangeListInt damage = null;
   public boolean damagePercent = false;
   public int M = 0;
   public RangeListInt stackSize = null;
   public RangeListInt enchantmentIds = null;
   public RangeListInt enchantmentLevels = null;
   public NbtTagValue[] nbtTagValues = null;
   public int T = 0;
   public int a = 1;
   public float speed = 0.0F;
   public float rotation = 0.0F;
   public int layer = 0;
   public float duration = 1.0F;
   public int weight = 0;
   public ResourceLocation textureLocation = null;
   public Map mapTextureLocations = null;
   public TextureAtlasSprite sprite = null;
   public Map mapSprites = null;
   public IBakedModel c = null;
   public Map<String, IBakedModel> L = null;
   public IBakedModel x = null;
   public Map<String, IBakedModel> b = null;
   private int textureWidth = 0;
   private int textureHeight = 0;
   public static final int o = 0;
   public static final int Q = 1;
   public static final int d = 2;
   public static final int m = 3;
   public static final int E = 0;
   public static final int j = 1;
   public static final int s = 2;
   public static final String R = "inventory";
   private static final long h = 33447728426410L;

   public CustomItemProperties(Properties var1, String var2) {
      this.name = parseName(var2);
      this.basePath = parseBasePath(var2);
      this.type = this.parseType(var1.getProperty("type"));
      this.items = this.parseItems(var1.getProperty("items"), var1.getProperty("matchItems"));
      this.B = p(var1, this.basePath);
      this.e = Z(var1.getProperty("model"), var2, this.basePath, this.type, this.B);
      this.mapTextures = parseTextures(var1, this.basePath);
      boolean var7 = this.B == null && this.e == null;
      this.texture = r(
         var1.getProperty("texture"), var1.getProperty("tile"), var1.getProperty("source"), var2, this.basePath, this.type, this.mapTextures, var7
      );
      String var8 = var1.getProperty("damage");
      if (var8 != null) {
         this.damagePercent = var8.contains("%");
         var8 = var8.replace("%", "");
         this.damage = this.parseRangeListInt(var8);
         this.M = this.parseInt(var1.getProperty("damageMask"), 0);
      }

      this.stackSize = this.parseRangeListInt(var1.getProperty("stackSize"));
      this.enchantmentIds = this.t(var1.getProperty("enchantmentIDs"), new ParserEnchantmentId());
      this.enchantmentLevels = this.parseRangeListInt(var1.getProperty("enchantmentLevels"));
      this.nbtTagValues = this.parseNbtTagValues(var1);
      this.T = this.q(var1.getProperty("hand"));
      this.a = Blender.parseBlend(var1.getProperty("blend"));
      this.speed = this.parseFloat(var1.getProperty("speed"), 0.0F);
      this.rotation = this.parseFloat(var1.getProperty("rotation"), 0.0F);
      this.layer = this.parseInt(var1.getProperty("layer"), 0);
      this.weight = this.parseInt(var1.getProperty("weight"), 0);
      this.duration = this.parseFloat(var1.getProperty("duration"), 1.0F);
   }

   private static String parseName(String var0) {
      String var4 = var0;
      int var5 = var0.lastIndexOf(47);
      if (var5 >= 0) {
         var4 = var0.substring(var5 + 1);
      }

      int var6 = var4.lastIndexOf(46);
      if (var6 >= 0) {
         var4 = var4.substring(0, var6);
      }

      return var4;
   }

   private static String parseBasePath(String var0) {
      int var3 = var0.lastIndexOf(47);
      return var3 < 0 ? "" : var0.substring(0, var3);
   }

   private int parseType(String var1) {
      if (var1 == null) {
         return 1;
      }

      if (var1.equals("item")) {
         return 1;
      }

      if (var1.equals("enchantment")) {
         return 2;
      }

      if (var1.equals("armor")) {
         return 3;
      }

      Config.warn("Unknown method: " + var1);
      return 0;
   }

   private int[] parseItems(String var1, String var2) {
      if (var1 == null) {
         var1 = var2;
      }

      if (var1 == null) {
         return null;
      }

      var1 = var1.trim();
      TreeSet var6 = new TreeSet();
      String[] var7 = Config.tokenize(var1, " ");

      for (int var8 = 0; var8 < var7.length; var8++) {
         String var9 = var7[var8];
         int var10 = Config.parseInt(var9, -1);
         if (var10 >= 0) {
            var6.add(new Integer(var10));
         } else {
            if (var9.contains("-")) {
               String[] var11 = Config.tokenize(var9, "-");
               if (var11.length == 2) {
                  int var12 = Config.parseInt(var11[0], -1);
                  int var13 = Config.parseInt(var11[1], -1);
                  if (var12 >= 0 && var13 >= 0) {
                     int var14 = Math.min(var12, var13);
                     int var15 = Math.max(var12, var13);

                     for (int var16 = var14; var16 <= var15; var16++) {
                        var6.add(new Integer(var16));
                     }
                     continue;
                  }
               }
            }

            Item var21 = Item.getByNameOrId(var9);
            if (var21 == null) {
               Config.warn("Item not found: " + var9);
            } else {
               int var22 = Item.getIdFromItem(var21);
               if (var22 <= 0) {
                  Config.warn("Item not found: " + var9);
               } else {
                  var6.add(new Integer(var22));
               }
            }
         }
      }

      Integer[] var18 = (java.lang.Integer[])(var6.toArray(new Integer[var6.size()]));
      int[] var19 = new int[var18.length];

      for (int var20 = 0; var20 < var19.length; var20++) {
         var19[var20] = var18[var20];
      }

      return var19;
   }

   private static String r(String var0, String var1, String var2, String var3, String var4, int var5, Map<String, String> var6, boolean var7) {
      if (var0 == null) {
         var0 = var1;
      }

      if (var0 == null) {
         var0 = var2;
      }

      if (var0 != null) {
         String var15 = ".png";
         if (var0.endsWith(var15)) {
            var0 = var0.substring(0, var0.length() - var15.length());
         }

         return fixTextureName(var0, var4);
      } else {
         if (var5 == 3) {
            return null;
         }

         if (var6 != null) {
            String var11 = (String)var6.get("texture.bow_standby");
            if (var11 != null) {
               return var11;
            }
         }

         if (!var7) {
            return null;
         }

         String var14 = var3;
         int var12 = var3.lastIndexOf(47);
         if (var12 >= 0) {
            var14 = var3.substring(var12 + 1);
         }

         int var13 = var14.lastIndexOf(46);
         if (var13 >= 0) {
            var14 = var14.substring(0, var13);
         }

         return fixTextureName(var14, var4);
      }
   }

   private static Map parseTextures(Properties var0, String var1) {
      String var5 = "texture.";
      Map var6 = getMatchingProperties(var0, var5);
      if (var6.size() <= 0) {
         return null;
      }

      Set var7 = var6.keySet();
      LinkedHashMap var8 = new LinkedHashMap();

      for (Object var10 : var7) {
         String var11 = (String)var10;
         String var12 = (String)var6.get(var11);
         var12 = fixTextureName(var12, var1);
         var8.put(var11, var12);
      }

      return var8;
   }

   private static String fixTextureName(String var0, String var1) {
      var0 = TextureUtils.fixResourcePath(var0, var1);
      if (!var0.startsWith(var1) && !var0.startsWith("textures/") && !var0.startsWith("mcpatcher/")) {
         var0 = var1 + "/" + var0;
      }

      if (var0.endsWith(".png")) {
         var0 = var0.substring(0, var0.length() - 4);
      }

      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      return var0;
   }

   private static String Z(String var0, String var1, String var2, int var3, Map<String, String> var4) {
      if (var0 != null) {
         String var9 = ".json";
         if (var0.endsWith(var9)) {
            var0 = var0.substring(0, var0.length() - var9.length());
         }

         return N(var0, var2);
      } else {
         if (var3 == 3) {
            return null;
         }

         if (var4 != null) {
            String var8 = (String)var4.get("model.bow_standby");
            if (var8 != null) {
               return var8;
            }
         }

         return var0;
      }
   }

   private static Map p(Properties var0, String var1) {
      String var5 = "model.";
      Map var6 = getMatchingProperties(var0, var5);
      if (var6.size() <= 0) {
         return null;
      }

      Set var7 = var6.keySet();
      LinkedHashMap var8 = new LinkedHashMap();

      for (Object var10 : var7) {
         String var11 = (String)var10;
         String var12 = (String)var6.get(var11);
         var12 = N(var12, var1);
         var8.put(var11, var12);
      }

      return var8;
   }

   private static String N(String var0, String var1) {
      var0 = TextureUtils.fixResourcePath(var0, var1);
      boolean var5 = var0.startsWith("block/") || var0.startsWith("item/");
      if (!var0.startsWith(var1) && !var5 && !var0.startsWith("mcpatcher/")) {
         var0 = var1 + "/" + var0;
      }

      String var6 = ".json";
      if (var0.endsWith(var6)) {
         var0 = var0.substring(0, var0.length() - var6.length());
      }

      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      return var0;
   }

   private int parseInt(String var1, int var2) {
      if (var1 == null) {
         return var2;
      } else {
         var1 = var1.trim();
         int var6 = Config.parseInt(var1, Integer.MIN_VALUE);
         if (var6 == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + var1);
            return var2;
         } else {
            return var6;
         }
      }
   }

   private float parseFloat(String var1, float var2) {
      if (var1 == null) {
         return var2;
      } else {
         var1 = var1.trim();
         float var6 = Config.parseFloat(var1, Float.MIN_VALUE);
         if (var6 == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + var1);
            return var2;
         } else {
            return var6;
         }
      }
   }

   private RangeListInt parseRangeListInt(String var1) {
      return this.t(var1, (IParserInt)null);
   }

   private RangeListInt t(String var1, IParserInt var2) {
      if (var1 == null) {
         return null;
      }

      String[] var6 = Config.tokenize(var1, " ");
      RangeListInt var7 = new RangeListInt();

      for (int var8 = 0; var8 < var6.length; var8++) {
         String var9 = var6[var8];
         if (var2 != null) {
            int var10 = var2.E(var9, Integer.MIN_VALUE);
            if (var10 != Integer.MIN_VALUE) {
               var7.addRange(new RangeInt(var10, var10));
               continue;
            }
         }

         RangeInt var11 = this.parseRangeInt(var9);
         if (var11 == null) {
            Config.warn("Invalid range list: " + var1);
            return null;
         }

         var7.addRange(var11);
      }

      return var7;
   }

   private RangeInt parseRangeInt(String var1) {
      if (var1 == null) {
         return null;
      }

      var1 = var1.trim();
      int var5 = var1.length() - var1.replace("-", "").length();
      if (var5 > 1) {
         Config.warn("Invalid range: " + var1);
         return null;
      }

      String[] var6 = Config.tokenize(var1, "- ");
      int[] var7 = new int[var6.length];

      for (int var8 = 0; var8 < var6.length; var8++) {
         String var9 = var6[var8];
         int var10 = Config.parseInt(var9, -1);
         if (var10 < 0) {
            Config.warn("Invalid range: " + var1);
            return null;
         }

         var7[var8] = var10;
      }

      if (var7.length == 1) {
         int var13 = var7[0];
         if (var1.startsWith("-")) {
            return new RangeInt(0, var13);
         } else {
            return var1.endsWith("-") ? new RangeInt(var13, 65535) : new RangeInt(var13, var13);
         }
      } else if (var7.length == 2) {
         int var12 = Math.min(var7[0], var7[1]);
         int var14 = Math.max(var7[0], var7[1]);
         return new RangeInt(var12, var14);
      } else {
         Config.warn("Invalid range: " + var1);
         return null;
      }
   }

   private NbtTagValue[] parseNbtTagValues(Properties var1) {
      String var5 = "nbt.";
      Map var6 = getMatchingProperties(var1, var5);
      if (var6.size() <= 0) {
         return null;
      }

      ArrayList var7 = new ArrayList();

      for (Object var9 : var6.keySet()) {
         String var10 = (String)var9;
         String var11 = (String)var6.get(var10);
         String var12 = var10.substring(var5.length());
         NbtTagValue var13 = new NbtTagValue(var12, var11);
         var7.add(var13);
      }

      return (net.minecraft.src.NbtTagValue[])(var7.toArray(new NbtTagValue[var7.size()]));
   }

   private static Map getMatchingProperties(Properties var0, String var1) {
      LinkedHashMap var5 = new LinkedHashMap();

      for (Object var7 : var0.keySet()) {
         String var8 = (String)var7;
         String var9 = var0.getProperty(var8);
         if (var8.startsWith(var1)) {
            var5.put(var8, var9);
         }
      }

      return var5;
   }

   private int q(String var1) {
      if (var1 == null) {
         return 0;
      }

      var1 = var1.toLowerCase();
      if (var1.equals("any")) {
         return 0;
      }

      if (var1.equals("main")) {
         return 1;
      }

      if (var1.equals("off")) {
         return 2;
      }

      Config.warn("Invalid hand: " + var1);
      return 0;
   }

   public boolean isValid(String var1) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + var1);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + var1);
         return false;
      }

      if (this.type == 0) {
         Config.warn("No type defined: " + var1);
         return false;
      }

      if (this.type == 1 || this.type == 3) {
         if (this.items == null) {
            this.items = this.detectItems();
         }

         if (this.items == null) {
            Config.warn("No items defined: " + var1);
            return false;
         }
      }

      if (this.texture == null && this.mapTextures == null && this.e == null && this.B == null) {
         Config.warn("No texture or model specified: " + var1);
         return false;
      } else if (this.type == 2 && this.enchantmentIds == null) {
         Config.warn("No enchantmentIDs specified: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private int[] detectItems() {
      Item var4 = Item.getByNameOrId(this.name);
      if (var4 == null) {
         return null;
      }

      int var5 = Item.getIdFromItem(var4);
      return var5 <= 0 ? null : new int[]{var5};
   }

   public void updateIcons(TextureMap var1) {
      if (this.texture != null) {
         this.textureLocation = this.getTextureLocation(this.texture);
         if (this.type == 1) {
            ResourceLocation var5 = this.getSpriteLocation(this.textureLocation);
            this.sprite = var1.registerSprite(var5);
         }
      }

      if (this.mapTextures != null) {
         this.mapTextureLocations = new HashMap();
         this.mapSprites = new HashMap();

         for (String var6 : this.mapTextures.keySet()) {
            String var7 = this.mapTextures.get(var6);
            ResourceLocation var8 = this.getTextureLocation(var7);
            this.mapTextureLocations.put(var6, var8);
            if (this.type == 1) {
               ResourceLocation var9 = this.getSpriteLocation(var8);
               TextureAtlasSprite var10 = var1.registerSprite(var9);
               this.mapSprites.put(var6, var10);
            }
         }
      }
   }

   private ResourceLocation getTextureLocation(String var1) {
      if (var1 == null) {
         return null;
      }

      ResourceLocation var5 = new ResourceLocation(var1);
      String var6 = var5.getResourceDomain();
      String var7 = var5.getResourcePath();
      if (!var7.contains("/")) {
         var7 = "textures/items/" + var7;
      }

      String var8 = var7 + ".png";
      ResourceLocation var9 = new ResourceLocation(var6, var8);
      boolean var10 = Config.hasResource(var9);
      if (!var10) {
         Config.warn("File not found: " + var8);
      }

      return var9;
   }

   private ResourceLocation getSpriteLocation(ResourceLocation var1) {
      String var4 = var1.getResourcePath();
      var4 = StrUtils.removePrefix(var4, "textures/");
      var4 = StrUtils.removeSuffix(var4, ".png");
      return new ResourceLocation(var1.getResourceDomain(), var4);
   }

   public void updateModel(TextureMap var1, ItemModelGenerator var2) {
      if (this.texture != null || this.mapTextures != null) {
         String[] var6 = this.getModelTextures();
         boolean var7 = this.isUseTint();
         this.c = makeBakedModel(var1, var2, var6, var7);
         if (this.type == 1 && this.mapTextures != null) {
            for (String var9 : this.mapTextures.keySet()) {
               String var10 = this.mapTextures.get(var9);
               String var11 = StrUtils.removePrefix(var9, "texture.");
               if (var11.startsWith("bow") || var11.startsWith("fishing_rod") || var11.startsWith("shield")) {
                  String[] var12 = new String[]{var10};
                  IBakedModel var13 = makeBakedModel(var1, var2, var12, var7);
                  if (this.L == null) {
                     this.L = new HashMap<>();
                  }

                  String var14 = "item/" + var11;
                  this.L.put(var14, var13);
               }
            }
         }
      }
   }

   private boolean isUseTint() {
      return true;
   }

   private static IBakedModel makeBakedModel(TextureMap var0, ItemModelGenerator var1, String[] var2, boolean var3) {
      String[] var7 = new String[var2.length];

      for (int var8 = 0; var8 < var7.length; var8++) {
         String var9 = var2[var8];
         var7[var8] = StrUtils.removePrefix(var9, "textures/");
      }

      ModelBlock var11 = makeModelBlock(var7);
      ModelBlock var12 = var1.makeItemModel(var0, var11);
      return bakeModel(var0, var12, var3);
   }

   private String[] getModelTextures() {
      if (this.type == 1 && this.items.length == 1) {
         Item var4 = Item.getItemById(this.items[0]);
         if (var4 == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
            RangeInt var11 = this.damage.getRange(0);
            int var12 = var11.getMin();
            boolean var13 = (var12 & 16384) != 0;
            String var14 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
            String var15 = null;
            if (var13) {
               var15 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
            } else {
               var15 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
            }

            return new String[]{var14, var15};
         }

         if (var4 instanceof ItemArmor) {
            ItemArmor var5 = (ItemArmor)var4;
            if (var5.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
               String var6 = "leather";
               String var7 = "helmet";
               if (var5.armorType == 0) {
                  var7 = "helmet";
               }

               if (var5.armorType == 1) {
                  var7 = "chestplate";
               }

               if (var5.armorType == 2) {
                  var7 = "leggings";
               }

               if (var5.armorType == 3) {
                  var7 = "boots";
               }

               String var8 = var6 + "_" + var7;
               String var9 = this.getMapTexture(this.mapTextures, "texture." + var8, "items/" + var8);
               String var10 = this.getMapTexture(this.mapTextures, "texture." + var8 + "_overlay", "items/" + var8 + "_overlay");
               return new String[]{var9, var10};
            }
         }
      }

      return new String[]{this.texture};
   }

   private String getMapTexture(Map<String, String> var1, String var2, String var3) {
      if (var1 == null) {
         return var3;
      }

      String var7 = (String)var1.get(var2);
      return var7 == null ? var3 : var7;
   }

   private static ModelBlock makeModelBlock(String[] var0) {
      StringBuffer var4 = new StringBuffer();
      var4.append("{\"parent\": \"builtin/generated\",\"textures\": {");

      for (int var5 = 0; var5 < var0.length; var5++) {
         String var6 = var0[var5];
         if (var5 > 0) {
            var4.append(", ");
         }

         var4.append("\"layer" + var5 + "\": \"" + var6 + "\"");
      }

      var4.append("}}");
      String var7 = var4.toString();
      return ModelBlock.deserialize(var7);
   }

   private static IBakedModel bakeModel(TextureMap var0, ModelBlock var1, boolean var2) {
      ModelRotation var6 = ModelRotation.X0_Y0;
      boolean var7 = false;
      String var8 = var1.resolveTextureName("particle");
      TextureAtlasSprite var9 = var0.getAtlasSprite(new ResourceLocation(var8).toString());
      SimpleBakedModel.Builder var10 = new SimpleBakedModel.Builder(var1).setTexture(var9);

      for (BlockPart var12 : var1.getElements()) {
         for (EnumFacing var14 : var12.mapFaces.keySet()) {
            BlockPartFace var15 = var12.mapFaces.get(var14);
            if (!var2) {
               var15 = new BlockPartFace(var15.cullFace, -1, var15.texture, var15.blockFaceUV);
            }

            String var16 = var1.resolveTextureName(var15.texture);
            TextureAtlasSprite var17 = var0.getAtlasSprite(new ResourceLocation(var16).toString());
            BakedQuad var18 = makeBakedQuad(var12, var15, var17, var14, var6, var7);
            if (var15.cullFace == null) {
               var10.addGeneralQuad(var18);
            } else {
               var10.addFaceQuad(var6.s(var15.cullFace), var18);
            }
         }
      }

      return var10.makeBakedModel();
   }

   private static BakedQuad makeBakedQuad(BlockPart var0, BlockPartFace var1, TextureAtlasSprite var2, EnumFacing var3, ModelRotation var4, boolean var5) {
      FaceBakery var6 = new FaceBakery();
      return var6.makeBakedQuad(var0.positionFrom, var0.positionTo, var1, var2, var3, var4, var0.partRotation, var5, var0.shade);
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
   }

   public float getTextureWidth(TextureManager var1) {
      if (this.textureWidth <= 0) {
         if (this.textureLocation != null) {
            ITextureObject var5 = var1.getTexture(this.textureLocation);
            int var6 = var5.getGlTextureId();
            int var7 = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(var6);
            this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            GlStateManager.bindTexture(var7);
         }

         if (this.textureWidth <= 0) {
            this.textureWidth = 16;
         }
      }

      return this.textureWidth;
   }

   public float getTextureHeight(TextureManager var1) {
      if (this.textureHeight <= 0) {
         if (this.textureLocation != null) {
            ITextureObject var5 = var1.getTexture(this.textureLocation);
            int var6 = var5.getGlTextureId();
            int var7 = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(var6);
            this.textureHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
            GlStateManager.bindTexture(var7);
         }

         if (this.textureHeight <= 0) {
            this.textureHeight = 16;
         }
      }

      return this.textureHeight;
   }

   public IBakedModel T(ResourceLocation var1, boolean var2) {
      IBakedModel var6;
      Map var7;
      if (var2) {
         var6 = this.x;
         var7 = this.b;
      } else {
         var6 = this.c;
         var7 = this.L;
      }

      if (var1 != null && var7 != null) {
         String var8 = var1.getResourcePath();
         IBakedModel var9 = (IBakedModel)var7.get(var8);
         if (var9 != null) {
            return var9;
         }
      }

      return var6;
   }

   public void X(ModelBakery var1) {
      if (this.e != null) {
         L(var1, this.e);
      }

      if (this.type == 1 && this.B != null) {
         for (String var6 : this.B.keySet()) {
            String var7 = this.B.get(var6);
            String var8 = StrUtils.removePrefix(var6, "model.");
            if (var8.startsWith("bow") || var8.startsWith("fishing_rod") || var8.startsWith("shield")) {
               L(var1, var7);
            }
         }
      }
   }

   public void h() {
      ModelManager var4 = Config.getModelManager();
      IBakedModel var5 = var4.getMissingModel();
      if (this.e != null) {
         ResourceLocation var6 = y(this.e);
         ModelResourceLocation var7 = new ModelResourceLocation(var6, "inventory");
         this.x = var4.getModel(var7);
         if (this.x == var5) {
            Config.warn("Custom Items: Model not found " + var7.getResourcePath());
            this.x = null;
         }
      }

      if (this.type == 1 && this.B != null) {
         for (String var15 : this.B.keySet()) {
            String var8 = this.B.get(var15);
            String var9 = StrUtils.removePrefix(var15, "model.");
            if (var9.startsWith("bow") || var9.startsWith("fishing_rod") || var9.startsWith("shield")) {
               ResourceLocation var10 = y(var8);
               ModelResourceLocation var11 = new ModelResourceLocation(var10, "inventory");
               IBakedModel var12 = var4.getModel(var11);
               if (var12 == var5) {
                  Config.warn("Custom Items: Model not found " + var11.getResourcePath());
               } else {
                  if (this.b == null) {
                     this.b = new HashMap<>();
                  }

                  String var13 = "item/" + var9;
                  this.b.put(var13, var12);
               }
            }
         }
      }
   }

   private static void L(ModelBakery var0, String var1) {
      ResourceLocation var4 = y(var1);
      ModelResourceLocation var5 = new ModelResourceLocation(var4, "inventory");
      var0.A(var4.toString(), var5, var4);
   }

   private static ResourceLocation y(String var0) {
      return Reflector.ModelLoader.exists() && !var0.startsWith("mcpatcher/") && !var0.startsWith("optifine/")
         ? new ResourceLocation("models/" + var0)
         : new ResourceLocation(var0);
   }

   static {
      long var11 = h ^ 92130644525583L;
      long var10000 = var11 ^ 9056037918860405563L;
      var10000 = var11 ^ 4326873559655952389L;
      var10000 = var11 ^ 4326873559655952389L;
   }
}
