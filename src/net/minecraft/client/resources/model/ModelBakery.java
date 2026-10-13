package net.minecraft.client.resources.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.src.CustomItems;
import net.minecraft.src.Reflector;
import net.minecraft.src.StrUtils;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   private static final Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(
      new ResourceLocation("blocks/water_flow"),
      new ResourceLocation("blocks/water_still"),
      new ResourceLocation("blocks/lava_flow"),
      new ResourceLocation("blocks/lava_still"),
      new ResourceLocation("blocks/destroy_stage_0"),
      new ResourceLocation("blocks/destroy_stage_1"),
      new ResourceLocation("blocks/destroy_stage_2"),
      new ResourceLocation("blocks/destroy_stage_3"),
      new ResourceLocation("blocks/destroy_stage_4"),
      new ResourceLocation("blocks/destroy_stage_5"),
      new ResourceLocation("blocks/destroy_stage_6"),
      new ResourceLocation("blocks/destroy_stage_7"),
      new ResourceLocation("blocks/destroy_stage_8"),
      new ResourceLocation("blocks/destroy_stage_9"),
      new ResourceLocation("items/empty_armor_slot_helmet"),
      new ResourceLocation("items/empty_armor_slot_chestplate"),
      new ResourceLocation("items/empty_armor_slot_leggings"),
      new ResourceLocation("items/empty_armor_slot_boots")
   );
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap();
   private static final Joiner JOINER = Joiner.on(" -> ");
   private final IResourceManager resourceManager;
   private final Map<ResourceLocation, TextureAtlasSprite> sprites = Maps.newHashMap();
   private final Map<ResourceLocation, ModelBlock> models = Maps.newLinkedHashMap();
   private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> variants = Maps.newLinkedHashMap();
   private final TextureMap textureMap;
   private final BlockModelShapes blockModelShapes;
   private final FaceBakery faceBakery = new FaceBakery();
   private final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = new RegistrySimple<>();
   private static final ModelBlock MODEL_GENERATED = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final ModelBlock MODEL_COMPASS = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final ModelBlock MODEL_CLOCK = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final ModelBlock MODEL_ENTITY = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private Map<String, ResourceLocation> itemLocations = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
   private Map<Item, List<String>> variantNames = Maps.newIdentityHashMap();
   private static Map<RegistryDelegate<Item>, Set<String>> p = Maps.newHashMap();
   private static final long b = 7775452443543L;

   public ModelBakery(IResourceManager var1, TextureMap var2, BlockModelShapes var3) {
      this.resourceManager = var1;
      this.textureMap = var2;
      this.blockModelShapes = var3;
   }

   public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry() {
      this.loadVariantItemModels();
      this.loadModelsCheck();
      this.loadSprites();
      this.bakeItemModels();
      this.bakeBlockModels();
      return this.bakedRegistry;
   }

   private void loadVariantItemModels() {
      this.loadVariants(this.blockModelShapes.getBlockStateMapper().putAllStateModelLocations().values());
      this.variants
         .put(
            MODEL_MISSING,
            new ModelBlockDefinition.Variants(
               MODEL_MISSING.getVariant(),
               Lists.newArrayList(new ModelBlockDefinition.Variant(new ResourceLocation(MODEL_MISSING.getResourcePath()), ModelRotation.X0_Y0, false, 1))
            )
         );
      ResourceLocation var3 = new ResourceLocation("item_frame");
      ModelBlockDefinition var4 = this.getModelBlockDefinition(var3);
      this.registerVariant(var4, new ModelResourceLocation(var3, "normal"));
      this.registerVariant(var4, new ModelResourceLocation(var3, "map"));
      this.loadVariantModels();
      this.loadItemModels();
   }

   private void loadVariants(Collection<ModelResourceLocation> var1) {
      for (ModelResourceLocation var6 : var1) {
         try {
            ModelBlockDefinition var7 = this.getModelBlockDefinition(var6);

            try {
               this.registerVariant(var7, var6);
            } catch (Exception var9) {
               LOGGER.warn("Unable to load variant: " + var6.getVariant() + " from " + var6, var9);
            }
         } catch (Exception var10) {
            LOGGER.warn("Unable to load definition " + var6, var10);
         }
      }
   }

   private void registerVariant(ModelBlockDefinition var1, ModelResourceLocation var2) {
      this.variants.put(var2, var1.getVariants(var2.getVariant()));
   }

   private ModelBlockDefinition getModelBlockDefinition(ResourceLocation var1) {
      ResourceLocation var5 = this.getBlockStateLocation(var1);
      ModelBlockDefinition var6 = this.blockDefinitions.get(var5);
      if (var6 == null) {
         ArrayList var7 = Lists.newArrayList();

         try {
            for (IResource var9 : this.resourceManager.getAllResources(var5)) {
               InputStream var10 = null;

               try {
                  var10 = var9.getInputStream();
                  ModelBlockDefinition var11 = ModelBlockDefinition.parseFromReader(new InputStreamReader(var10, Charsets.UTF_8));
                  var7.add(var11);
               } catch (Exception var16) {
                  throw new RuntimeException(
                     "Encountered an exception when loading model definition of '"
                        + var1
                        + "' from: '"
                        + var9.getResourceLocation()
                        + "' in resourcepack: '"
                        + var9.getResourcePackName()
                        + "'",
                     var16
                  );
               } finally {
                  IOUtils.closeQuietly(var10);
               }
            }
         } catch (IOException var18) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + var5.toString(), var18);
         }

         var6 = new ModelBlockDefinition((java.util.List<net.minecraft.client.renderer.block.model.ModelBlockDefinition>)(var7));
         this.blockDefinitions.put(var5, var6);
      }

      return var6;
   }

   private ResourceLocation getBlockStateLocation(ResourceLocation var1) {
      return new ResourceLocation(var1.getResourceDomain(), "blockstates/" + var1.getResourcePath() + ".json");
   }

   private void loadVariantModels() {
      for (ModelResourceLocation var5 : this.variants.keySet()) {
         for (ModelBlockDefinition.Variant var7 : this.variants.get(var5).getVariants()) {
            ResourceLocation var8 = var7.getModelLocation();
            if (this.models.get(var8) == null) {
               try {
                  ModelBlock var9 = this.loadModel(var8);
                  this.models.put(var8, var9);
               } catch (Exception var10) {
                  LOGGER.warn("Unable to load block model: '" + var8 + "' for variant: '" + var5 + "'", var10);
               }
            }
         }
      }
   }

   private ModelBlock loadModel(ResourceLocation var1) throws IOException {
      String var5 = var1.getResourcePath();
      if ("builtin/generated".equals(var5)) {
         return MODEL_GENERATED;
      }

      if ("builtin/compass".equals(var5)) {
         return MODEL_COMPASS;
      }

      if ("builtin/clock".equals(var5)) {
         return MODEL_CLOCK;
      }

      if ("builtin/entity".equals(var5)) {
         return MODEL_ENTITY;
      }

      Reader var6;
      if (var5.startsWith("builtin/")) {
         String var7 = var5.substring("builtin/".length());
         String var8 = BUILT_IN_MODELS.get(var7);
         if (var8 == null) {
            throw new FileNotFoundException(var1.toString());
         }

         var6 = new StringReader(var8);
      } else {
         var1 = this.getModelLocation(var1);
         IResource var13 = this.resourceManager.getResource(var1);
         var6 = new InputStreamReader(var13.getInputStream(), Charsets.UTF_8);
      }

      ModelBlock var14;
      try {
         ModelBlock var15 = ModelBlock.deserialize(var6);
         var15.name = var1.toString();
         var14 = var15;
         String var9 = TextureUtils.getBasePath(var1.getResourcePath());
         M(var15, var9);
      } finally {
         var6.close();
      }

      return var14;
   }

   private ResourceLocation getModelLocation(ResourceLocation var1) {
      ResourceLocation var5 = var1;
      String var6 = var1.getResourcePath();
      if (!var6.startsWith("mcpatcher") && !var6.startsWith("optifine")) {
         return new ResourceLocation(var1.getResourceDomain(), "models/" + var1.getResourcePath() + ".json");
      }

      if (!var6.endsWith(".json")) {
         var5 = new ResourceLocation(var1.getResourceDomain(), var6 + ".json");
      }

      return var5;
   }

   private void loadItemModels() {
      this.registerVariantNames();

      for (Item var5 : Item.itemRegistry) {
         for (String var7 : this.getVariantNames(var5)) {
            ResourceLocation var8 = this.getItemLocation(var7);
            this.itemLocations.put(var7, var8);
            if (this.models.get(var8) == null) {
               try {
                  ModelBlock var9 = this.loadModel(var8);
                  this.models.put(var8, var9);
               } catch (Exception var10) {
                  LOGGER.warn("Unable to load item model: '" + var8 + "' for item: '" + Item.itemRegistry.getNameForObject(var5) + "'", var10);
               }
            }
         }
      }
   }

   public void A(String var1, ResourceLocation var2, ResourceLocation var3) {
      this.itemLocations.put(var1, var2);
      if (this.models.get(var2) == null) {
         try {
            ModelBlock var7 = this.loadModel(var2);
            this.models.put(var2, var7);
         } catch (Exception var8) {
            LOGGER.warn("Unable to load item model: '{}' for item: '{}'", var2, var3);
            LOGGER.warn(var8.getClass().getName() + ": " + var8.getMessage());
         }
      }
   }

   private void registerVariantNames() {
      this.variantNames.clear();
      this.variantNames.clear();
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone),
            Lists.newArrayList("stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth")
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.dirt), Lists.newArrayList("dirt", "coarse_dirt", "podzol"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.planks),
            Lists.newArrayList("oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks")
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.sapling),
            Lists.newArrayList("oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling")
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.sand), Lists.newArrayList("sand", "red_sand"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log), Lists.newArrayList("oak_log", "spruce_log", "birch_log", "jungle_log"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves), Lists.newArrayList("oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sponge), Lists.newArrayList("sponge", "sponge_wet"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sandstone), Lists.newArrayList("sandstone", "chiseled_sandstone", "smooth_sandstone"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.red_sandstone), Lists.newArrayList("red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.tallgrass), Lists.newArrayList("dead_bush", "tall_grass", "fern"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.deadbush), Lists.newArrayList("dead_bush"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wool),
            Lists.newArrayList(
               "black_wool",
               "red_wool",
               "green_wool",
               "brown_wool",
               "blue_wool",
               "purple_wool",
               "cyan_wool",
               "silver_wool",
               "gray_wool",
               "pink_wool",
               "lime_wool",
               "yellow_wool",
               "light_blue_wool",
               "magenta_wool",
               "orange_wool",
               "white_wool"
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.yellow_flower), Lists.newArrayList("dandelion"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.red_flower),
            Lists.newArrayList("poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy")
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone_slab),
            Lists.newArrayList("stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab")
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab2), Lists.newArrayList("red_sandstone_slab"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass),
            Lists.newArrayList(
               "black_stained_glass",
               "red_stained_glass",
               "green_stained_glass",
               "brown_stained_glass",
               "blue_stained_glass",
               "purple_stained_glass",
               "cyan_stained_glass",
               "silver_stained_glass",
               "gray_stained_glass",
               "pink_stained_glass",
               "lime_stained_glass",
               "yellow_stained_glass",
               "light_blue_stained_glass",
               "magenta_stained_glass",
               "orange_stained_glass",
               "white_stained_glass"
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.monster_egg),
            Lists.newArrayList(
               "stone_monster_egg",
               "cobblestone_monster_egg",
               "stone_brick_monster_egg",
               "mossy_brick_monster_egg",
               "cracked_brick_monster_egg",
               "chiseled_brick_monster_egg"
            )
         );
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.stonebrick), Lists.newArrayList("stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wooden_slab),
            Lists.newArrayList("oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab")
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.cobblestone_wall), Lists.newArrayList("cobblestone_wall", "mossy_cobblestone_wall"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.anvil), Lists.newArrayList("anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.quartz_block), Lists.newArrayList("quartz_block", "chiseled_quartz_block", "quartz_column"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_hardened_clay),
            Lists.newArrayList(
               "black_stained_hardened_clay",
               "red_stained_hardened_clay",
               "green_stained_hardened_clay",
               "brown_stained_hardened_clay",
               "blue_stained_hardened_clay",
               "purple_stained_hardened_clay",
               "cyan_stained_hardened_clay",
               "silver_stained_hardened_clay",
               "gray_stained_hardened_clay",
               "pink_stained_hardened_clay",
               "lime_stained_hardened_clay",
               "yellow_stained_hardened_clay",
               "light_blue_stained_hardened_clay",
               "magenta_stained_hardened_clay",
               "orange_stained_hardened_clay",
               "white_stained_hardened_clay"
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass_pane),
            Lists.newArrayList(
               "black_stained_glass_pane",
               "red_stained_glass_pane",
               "green_stained_glass_pane",
               "brown_stained_glass_pane",
               "blue_stained_glass_pane",
               "purple_stained_glass_pane",
               "cyan_stained_glass_pane",
               "silver_stained_glass_pane",
               "gray_stained_glass_pane",
               "pink_stained_glass_pane",
               "lime_stained_glass_pane",
               "yellow_stained_glass_pane",
               "light_blue_stained_glass_pane",
               "magenta_stained_glass_pane",
               "orange_stained_glass_pane",
               "white_stained_glass_pane"
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves2), Lists.newArrayList("acacia_leaves", "dark_oak_leaves"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log2), Lists.newArrayList("acacia_log", "dark_oak_log"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.prismarine), Lists.newArrayList("prismarine", "prismarine_bricks", "dark_prismarine"));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.carpet),
            Lists.newArrayList(
               "black_carpet",
               "red_carpet",
               "green_carpet",
               "brown_carpet",
               "blue_carpet",
               "purple_carpet",
               "cyan_carpet",
               "silver_carpet",
               "gray_carpet",
               "pink_carpet",
               "lime_carpet",
               "yellow_carpet",
               "light_blue_carpet",
               "magenta_carpet",
               "orange_carpet",
               "white_carpet"
            )
         );
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.double_plant), Lists.newArrayList("sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"));
      this.variantNames.put(Items.bow, Lists.newArrayList("bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"));
      this.variantNames.put(Items.coal, Lists.newArrayList("coal", "charcoal"));
      this.variantNames.put(Items.fishing_rod, Lists.newArrayList("fishing_rod", "fishing_rod_cast"));
      this.variantNames.put(Items.fish, Lists.newArrayList("cod", "salmon", "clownfish", "pufferfish"));
      this.variantNames.put(Items.cooked_fish, Lists.newArrayList("cooked_cod", "cooked_salmon"));
      this.variantNames
         .put(
            Items.dye,
            Lists.newArrayList(
               "dye_black",
               "dye_red",
               "dye_green",
               "dye_brown",
               "dye_blue",
               "dye_purple",
               "dye_cyan",
               "dye_silver",
               "dye_gray",
               "dye_pink",
               "dye_lime",
               "dye_yellow",
               "dye_light_blue",
               "dye_magenta",
               "dye_orange",
               "dye_white"
            )
         );
      this.variantNames.put(Items.potionitem, Lists.newArrayList("bottle_drinkable", "bottle_splash"));
      this.variantNames.put(Items.skull, Lists.newArrayList("skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence_gate), Lists.newArrayList("oak_fence_gate"));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence), Lists.newArrayList("oak_fence"));
      this.variantNames.put(Items.oak_door, Lists.newArrayList("oak_door"));

      for (Entry var5 : p.entrySet()) {
         this.variantNames.put((Item)((RegistryDelegate)var5.getKey()).f(), Lists.newArrayList(((Set)var5.getValue()).iterator()));
      }

      CustomItems.N();
      CustomItems.F(this);
   }

   private List<String> getVariantNames(Item var1) {
      List var5 = this.variantNames.get(var1);
      if (var5 == null) {
         var5 = Collections.singletonList(Item.itemRegistry.getNameForObject(var1).toString());
      }

      return var5;
   }

   private ResourceLocation getItemLocation(String var1) {
      ResourceLocation var4 = new ResourceLocation(var1);
      if (Reflector.ForgeHooksClient.exists()) {
         var4 = new ResourceLocation(var1.replaceAll("#.*", ""));
      }

      return new ResourceLocation(var4.getResourceDomain(), "item/" + var4.getResourcePath());
   }

   private void bakeBlockModels() {
      for (ModelResourceLocation var5 : this.variants.keySet()) {
         WeightedBakedModel.Builder var6 = new WeightedBakedModel.Builder();
         int var7 = 0;

         for (ModelBlockDefinition.Variant var9 : this.variants.get(var5).getVariants()) {
            ModelBlock var10 = this.models.get(var9.getModelLocation());
            if (var10 != null && var10.isResolved()) {
               var7++;
               var6.add(this.bakeModel(var10, var9.getRotation(), var9.isUvLocked()), var9.getWeight());
            } else {
               LOGGER.warn("Missing model for: " + var5);
            }
         }

         if (var7 == 0) {
            LOGGER.warn("No weighted models for: " + var5);
         } else if (var7 == 1) {
            this.bakedRegistry.putObject(var5, var6.first());
         } else {
            this.bakedRegistry.putObject(var5, var6.build());
         }
      }

      for (Entry var12 : this.itemLocations.entrySet()) {
         ResourceLocation var13 = (ResourceLocation)var12.getValue();
         ModelResourceLocation var14 = new ModelResourceLocation((String)var12.getKey(), "inventory");
         if (Reflector.BW.exists()) {
            var14 = (ModelResourceLocation)Reflector.call(Reflector.BW, var12.getKey());
         }

         ModelBlock var15 = this.models.get(var13);
         if (var15 == null || !var15.isResolved()) {
            LOGGER.warn("Missing model for: " + var13);
         } else if (this.isCustomRenderer(var15)) {
            this.bakedRegistry.putObject(var14, new BuiltInModel(var15.getAllTransforms()));
         } else {
            this.bakedRegistry.putObject(var14, this.bakeModel(var15, ModelRotation.X0_Y0, false));
         }
      }
   }

   private Set<ResourceLocation> getVariantsTextureLocations() {
      HashSet var4 = Sets.newHashSet();
      ArrayList var5 = Lists.newArrayList(this.variants.keySet());
      Collections.sort(var5, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation var1, ModelResourceLocation var2) {
            return var1.toString().compareTo(var2.toString());
         }
      });

      for (Object var7$raw : var5) {
         ModelResourceLocation var7 = (ModelResourceLocation)var7$raw;
         ModelBlockDefinition.Variants var8 = this.variants.get(var7);

         for (ModelBlockDefinition.Variant var10 : var8.getVariants()) {
            ModelBlock var11 = this.models.get(var10.getModelLocation());
            if (var11 == null) {
               LOGGER.warn("Missing model for: " + var7);
            } else {
               var4.addAll(this.getTextureLocations(var11));
            }
         }
      }

      var4.addAll(LOCATIONS_BUILTIN_TEXTURES);
      return var4;
   }

   public IBakedModel bakeModel(ModelBlock var1, ModelRotation var2, boolean var3) {
      return this.y(var1, var2, var3);
   }

   public IBakedModel y(ModelBlock var1, ITransformation var2, boolean var3) {
      TextureAtlasSprite var7 = this.sprites.get(new ResourceLocation(var1.resolveTextureName("particle")));
      SimpleBakedModel.Builder var8 = new SimpleBakedModel.Builder(var1).setTexture(var7);

      for (BlockPart var10 : var1.getElements()) {
         for (EnumFacing var12 : var10.mapFaces.keySet()) {
            BlockPartFace var13 = var10.mapFaces.get(var12);
            TextureAtlasSprite var14 = this.sprites.get(new ResourceLocation(var1.resolveTextureName(var13.texture)));
            boolean var15 = true;
            if (var13.cullFace != null && var15) {
               var8.addFaceQuad(var2.E(var13.cullFace), this.A(var10, var13, var14, var12, var2, var3));
            } else {
               var8.addGeneralQuad(this.A(var10, var13, var14, var12, var2, var3));
            }
         }
      }

      return var8.makeBakedModel();
   }

   public BakedQuad A(BlockPart var1, BlockPartFace var2, TextureAtlasSprite var3, EnumFacing var4, ITransformation var5, boolean var6) {
      return this.faceBakery.makeBakedQuad(var1.positionFrom, var1.positionTo, var2, var3, var4, var5, var1.partRotation, var6, var1.shade);
   }

   private void loadModelsCheck() {
      this.loadModels();

      for (ModelBlock var5 : this.models.values()) {
         var5.getParentFromMap(this.models);
      }

      ModelBlock.checkModelHierarchy(this.models);
   }

   private void loadModels() {
      ArrayDeque var4 = Queues.newArrayDeque();
      HashSet var5 = Sets.newHashSet();

      for (ResourceLocation var7 : this.models.keySet()) {
         var5.add(var7);
         ResourceLocation var8 = this.models.get(var7).getParentLocation();
         if (var8 != null) {
            var4.add(var8);
         }
      }

      while (!var4.isEmpty()) {
         ResourceLocation var10 = (ResourceLocation)var4.pop();

         try {
            if (this.models.get(var10) != null) {
               continue;
            }

            ModelBlock var11 = this.loadModel(var10);
            this.models.put(var10, var11);
            ResourceLocation var12 = var11.getParentLocation();
            if (var12 != null && !var5.contains(var12)) {
               var4.add(var12);
            }
         } catch (Exception var9) {
            LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(var10)) + "; unable to load model: '" + var10 + "'");
         }

         var5.add(var10);
      }
   }

   private List<ResourceLocation> getParentPath(ResourceLocation var1) {
      ArrayList var5 = Lists.newArrayList(var1);
      ResourceLocation var6 = var1;

      while ((var6 = this.getParentLocation(var6)) != null) {
         var5.add(0, var6);
      }

      return var5;
   }

   private ResourceLocation getParentLocation(ResourceLocation var1) {
      for (Entry var6 : this.models.entrySet()) {
         ModelBlock var7 = (ModelBlock)var6.getValue();
         if (var7 != null && var1.equals(var7.getParentLocation())) {
            return (ResourceLocation)var6.getKey();
         }
      }

      return null;
   }

   private Set<ResourceLocation> getTextureLocations(ModelBlock var1) {
      HashSet var5 = Sets.newHashSet();

      for (BlockPart var7 : var1.getElements()) {
         for (BlockPartFace var9 : var7.mapFaces.values()) {
            ResourceLocation var10 = new ResourceLocation(var1.resolveTextureName(var9.texture));
            var5.add(var10);
         }
      }

      var5.add(new ResourceLocation(var1.resolveTextureName("particle")));
      return var5;
   }

   private void loadSprites() {
      final Set var3 = this.getVariantsTextureLocations();
      var3.addAll(this.getItemsTextureLocations());
      var3.remove(TextureMap.LOCATION_MISSING_TEXTURE);
      IIconCreator var4 = new IIconCreator() {
         private static final long a = 62218832642803L;

         @Override
         public void registerSprites(TextureMap var1) {
            for (Object var6$raw : var3) {
               ResourceLocation var6 = (ResourceLocation)var6$raw;
               TextureAtlasSprite var7 = var1.registerSprite(var6);
               ModelBakery.this.sprites.put(var6, var7);
            }
         }
      };
      this.textureMap.loadSprites(this.resourceManager, var4);
      this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
   }

   private Set<ResourceLocation> getItemsTextureLocations() {
      HashSet var4 = Sets.newHashSet();

      for (ResourceLocation var6 : this.itemLocations.values()) {
         ModelBlock var7 = this.models.get(var6);
         if (var7 != null) {
            var4.add(new ResourceLocation(var7.resolveTextureName("particle")));
            if (this.hasItemModel(var7)) {
               for (String var14 : ItemModelGenerator.LAYERS) {
                  ResourceLocation var15 = new ResourceLocation(var7.resolveTextureName(var14));
                  if (var7.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(var15)) {
                     TextureAtlasSprite.setLocationNameCompass(var15.toString());
                  } else if (var7.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(var15)) {
                     TextureAtlasSprite.setLocationNameClock(var15.toString());
                  }

                  var4.add(var15);
               }
            } else if (!this.isCustomRenderer(var7)) {
               for (BlockPart var9 : var7.getElements()) {
                  for (BlockPartFace var11 : var9.mapFaces.values()) {
                     ResourceLocation var12 = new ResourceLocation(var7.resolveTextureName(var11.texture));
                     var4.add(var12);
                  }
               }
            }
         }
      }

      return var4;
   }

   private boolean hasItemModel(ModelBlock var1) {
      if (var1 == null) {
         return false;
      }

      ModelBlock var5 = var1.getRootModel();
      return var5 == MODEL_GENERATED || var5 == MODEL_COMPASS || var5 == MODEL_CLOCK;
   }

   private boolean isCustomRenderer(ModelBlock var1) {
      if (var1 == null) {
         return false;
      }

      ModelBlock var5 = var1.getRootModel();
      return var5 == MODEL_ENTITY;
   }

   private void bakeItemModels() {
      for (ResourceLocation var5 : this.itemLocations.values()) {
         ModelBlock var6 = this.models.get(var5);
         if (this.hasItemModel(var6)) {
            ModelBlock var7 = this.makeItemModel(var6);
            if (var7 != null) {
               var7.name = var5.toString();
            }

            this.models.put(var5, var7);
         } else if (this.isCustomRenderer(var6)) {
            this.models.put(var5, var6);
         }
      }

      for (TextureAtlasSprite var9 : this.sprites.values()) {
         if (!var9.hasAnimationMetadata()) {
            var9.clearFramesTextureData();
         }
      }
   }

   private ModelBlock makeItemModel(ModelBlock var1) {
      return this.itemModelGenerator.makeItemModel(this.textureMap, var1);
   }

   public ModelBlock v(ResourceLocation var1) {
      return this.models.get(var1);
   }

   public static void M(ModelBlock var0, String var1) {
      ResourceLocation var5 = l(var0.getParentLocation(), var1);
      if (var5 != var0.getParentLocation()) {
         Reflector.setFieldValue(var0, Reflector.um, var5);
      }

      Map var6 = (Map)Reflector.getFieldValue(var0, Reflector.ko);
      if (var6 != null) {
         for (Object var8$raw : var6.entrySet()) {
            Entry var8 = (Entry)var8$raw;
            String var9 = (String)var8.getValue();
            String var10 = m(var9, var1);
            if (var10 != var9) {
               var8.setValue(var10);
            }
         }
      }
   }

   public static ResourceLocation l(ResourceLocation var0, String var1) {
      if (var0 != null && var1 != null) {
         if (!var0.getResourceDomain().equals("minecraft")) {
            return var0;
         }

         String var5 = var0.getResourcePath();
         String var6 = m(var5, var1);
         if (var6 != var5) {
            var0 = new ResourceLocation(var0.getResourceDomain(), var6);
         }

         return var0;
      } else {
         return var0;
      }
   }

   private static String m(String var0, String var1) {
      var0 = TextureUtils.fixResourcePath(var0, var1);
      var0 = StrUtils.removeSuffix(var0, ".json");
      return StrUtils.removeSuffix(var0, ".png");
   }

   @Deprecated
   public static void I(Item var0, String... var1) {
      RegistryDelegate var5 = (RegistryDelegate)Reflector.getFieldValue(var0, Reflector.NW);
      if (p.containsKey(var5)) {
         p.get(var5).addAll(Lists.newArrayList(var1));
      } else {
         p.put(var5, Sets.newHashSet(var1));
      }
   }

   public static <T extends ResourceLocation> void b(Item var0, T... var1) {
      RegistryDelegate var5 = (RegistryDelegate)Reflector.getFieldValue(var0, Reflector.NW);
      if (!p.containsKey(var5)) {
         p.put(var5, Sets.newHashSet());
      }

      for (ResourceLocation var9 : var1) {
         p.get(var5).add(var9.toString());
      }
   }

   static {
      BUILT_IN_MODELS.put(
         "missing",
         "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}"
      );
      MODEL_GENERATED.name = "generation marker";
      MODEL_COMPASS.name = "compass generation marker";
      MODEL_CLOCK.name = "class generation marker";
      MODEL_ENTITY.name = "block entity marker";
   }
}
