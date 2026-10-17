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
   private static Map<RegistryDelegate<Item>, Set<String>> customVariantNames = Maps.newHashMap();

   public ModelBakery(IResourceManager iResourceManager, TextureMap textureMap, BlockModelShapes blockModelShapes) {
      this.resourceManager = iResourceManager;
      this.textureMap = textureMap;
      this.blockModelShapes = blockModelShapes;
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
      ResourceLocation resourceLocation = new ResourceLocation("item_frame");
      ModelBlockDefinition modelBlockDefinition = this.getModelBlockDefinition(resourceLocation);
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation, "normal"));
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation, "map"));
      this.loadVariantModels();
      this.loadItemModels();
   }

   private void loadVariants(Collection<ModelResourceLocation> collection) {
      for (ModelResourceLocation modelResourceLocation : collection) {
         try {
            ModelBlockDefinition modelBlockDefinition = this.getModelBlockDefinition(modelResourceLocation);

            try {
               this.registerVariant(modelBlockDefinition, modelResourceLocation);
            } catch (Exception exception) {
               LOGGER.warn("Unable to load variant: " + modelResourceLocation.getVariant() + " from " + modelResourceLocation, exception);
            }
         } catch (Exception exception2) {
            LOGGER.warn("Unable to load definition " + modelResourceLocation, exception2);
         }
      }
   }

   private void registerVariant(ModelBlockDefinition modelBlockDefinition, ModelResourceLocation modelResourceLocation) {
      this.variants.put(modelResourceLocation, modelBlockDefinition.getVariants(modelResourceLocation.getVariant()));
   }

   private ModelBlockDefinition getModelBlockDefinition(ResourceLocation resourceLocation) {
      ResourceLocation resourceLocation2 = this.getBlockStateLocation(resourceLocation);
      ModelBlockDefinition modelBlockDefinition = this.blockDefinitions.get(resourceLocation2);
      if (modelBlockDefinition == null) {
         ArrayList arrayList = Lists.newArrayList();

         try {
            for (IResource iResource : this.resourceManager.getAllResources(resourceLocation2)) {
               InputStream inputStream = null;

               try {
                  inputStream = iResource.getInputStream();
                  ModelBlockDefinition modelBlockDefinition2 = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                  arrayList.add(modelBlockDefinition2);
               } catch (Exception exception2) {
                  throw new RuntimeException(
                     "Encountered an exception when loading model definition of '"
                        + resourceLocation
                        + "' from: '"
                        + iResource.getResourceLocation()
                        + "' in resourcepack: '"
                        + iResource.getResourcePackName()
                        + "'",
                     exception2
                  );
               } finally {
                  IOUtils.closeQuietly(inputStream);
               }
            }
         } catch (IOException ioException) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + resourceLocation2.toString(), ioException);
         }

         modelBlockDefinition = new ModelBlockDefinition((java.util.List<net.minecraft.client.renderer.block.model.ModelBlockDefinition>)(arrayList));
         this.blockDefinitions.put(resourceLocation2, modelBlockDefinition);
      }

      return modelBlockDefinition;
   }

   private ResourceLocation getBlockStateLocation(ResourceLocation resourceLocation) {
      return new ResourceLocation(resourceLocation.getResourceDomain(), "blockstates/" + resourceLocation.getResourcePath() + ".json");
   }

   private void loadVariantModels() {
      for (ModelResourceLocation modelResourceLocation : this.variants.keySet()) {
         for (ModelBlockDefinition.Variant variant2 : this.variants.get(modelResourceLocation).getVariants()) {
            ResourceLocation resourceLocation = variant2.getModelLocation();
            if (this.models.get(resourceLocation) == null) {
               try {
                  ModelBlock modelBlock = this.loadModel(resourceLocation);
                  this.models.put(resourceLocation, modelBlock);
               } catch (Exception exception) {
                  LOGGER.warn("Unable to load block model: '" + resourceLocation + "' for variant: '" + modelResourceLocation + "'", exception);
               }
            }
         }
      }
   }

   private ModelBlock loadModel(ResourceLocation resourceLocation) throws IOException {
      String string = resourceLocation.getResourcePath();
      if ("builtin/generated".equals(string)) {
         return MODEL_GENERATED;
      }

      if ("builtin/compass".equals(string)) {
         return MODEL_COMPASS;
      }

      if ("builtin/clock".equals(string)) {
         return MODEL_CLOCK;
      }

      if ("builtin/entity".equals(string)) {
         return MODEL_ENTITY;
      }

      Reader reader;
      if (string.startsWith("builtin/")) {
         String string2 = string.substring("builtin/".length());
         String string3 = BUILT_IN_MODELS.get(string2);
         if (string3 == null) {
            throw new FileNotFoundException(resourceLocation.toString());
         }

         reader = new StringReader(string3);
      } else {
         resourceLocation = this.getModelLocation(resourceLocation);
         IResource iResource = this.resourceManager.getResource(resourceLocation);
         reader = new InputStreamReader(iResource.getInputStream(), Charsets.UTF_8);
      }

      ModelBlock modelBlock;
      try {
         ModelBlock modelBlock2 = ModelBlock.deserialize(reader);
         modelBlock2.name = resourceLocation.toString();
         modelBlock = modelBlock2;
         String string4 = TextureUtils.getBasePath(resourceLocation.getResourcePath());
         fixModelLocations(modelBlock2, string4);
      } finally {
         reader.close();
      }

      return modelBlock;
   }

   private ResourceLocation getModelLocation(ResourceLocation resourceLocation) {
      ResourceLocation resourceLocation2 = resourceLocation;
      String string = resourceLocation.getResourcePath();
      if (!string.startsWith("mcpatcher") && !string.startsWith("optifine")) {
         return new ResourceLocation(resourceLocation.getResourceDomain(), "models/" + resourceLocation.getResourcePath() + ".json");
      }

      if (!string.endsWith(".json")) {
         resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), string + ".json");
      }

      return resourceLocation2;
   }

   private void loadItemModels() {
      this.registerVariantNames();

      for (Item item2 : Item.itemRegistry) {
         for (String string : this.getVariantNames(item2)) {
            ResourceLocation resourceLocation = this.getItemLocation(string);
            this.itemLocations.put(string, resourceLocation);
            if (this.models.get(resourceLocation) == null) {
               try {
                  ModelBlock modelBlock = this.loadModel(resourceLocation);
                  this.models.put(resourceLocation, modelBlock);
               } catch (Exception exception) {
                  LOGGER.warn("Unable to load item model: '" + resourceLocation + "' for item: '" + Item.itemRegistry.getNameForObject(item2) + "'", exception);
               }
            }
         }
      }
   }

   public void loadItemModel(String string, ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
      this.itemLocations.put(string, resourceLocation);
      if (this.models.get(resourceLocation) == null) {
         try {
            ModelBlock modelBlock = this.loadModel(resourceLocation);
            this.models.put(resourceLocation, modelBlock);
         } catch (Exception exception) {
            LOGGER.warn("Unable to load item model: '{}' for item: '{}'", resourceLocation, resourceLocation2);
            LOGGER.warn(exception.getClass().getName() + ": " + exception.getMessage());
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

      for (Entry entry : customVariantNames.entrySet()) {
         this.variantNames.put((Item)((RegistryDelegate)entry.getKey()).get(), Lists.newArrayList(((Set)entry.getValue()).iterator()));
      }

      CustomItems.update();
      CustomItems.loadModels(this);
   }

   private List<String> getVariantNames(Item item) {
      List list = this.variantNames.get(item);
      if (list == null) {
         list = Collections.singletonList(Item.itemRegistry.getNameForObject(item).toString());
      }

      return list;
   }

   private ResourceLocation getItemLocation(String string) {
      ResourceLocation resourceLocation = new ResourceLocation(string);
      if (Reflector.ForgeHooksClient.exists()) {
         resourceLocation = new ResourceLocation(string.replaceAll("#.*", ""));
      }

      return new ResourceLocation(resourceLocation.getResourceDomain(), "item/" + resourceLocation.getResourcePath());
   }

   private void bakeBlockModels() {
      for (ModelResourceLocation modelResourceLocation : this.variants.keySet()) {
         WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
         int i = 0;

         for (ModelBlockDefinition.Variant variant : this.variants.get(modelResourceLocation).getVariants()) {
            ModelBlock modelBlock = this.models.get(variant.getModelLocation());
            if (modelBlock != null && modelBlock.isResolved()) {
               i++;
               builder.add(this.bakeModel(modelBlock, variant.getRotation(), variant.isUvLocked()), variant.getWeight());
            } else {
               LOGGER.warn("Missing model for: " + modelResourceLocation);
            }
         }

         if (i == 0) {
            LOGGER.warn("No weighted models for: " + modelResourceLocation);
         } else if (i == 1) {
            this.bakedRegistry.putObject(modelResourceLocation, builder.first());
         } else {
            this.bakedRegistry.putObject(modelResourceLocation, builder.build());
         }
      }

      for (Entry entry : this.itemLocations.entrySet()) {
         ResourceLocation resourceLocation = (ResourceLocation)entry.getValue();
         ModelResourceLocation modelResourceLocation2 = new ModelResourceLocation((String)entry.getKey(), "inventory");
         if (Reflector.ModelLoader_getInventoryVariant.exists()) {
            modelResourceLocation2 = (ModelResourceLocation)Reflector.call(Reflector.ModelLoader_getInventoryVariant, entry.getKey());
         }

         ModelBlock modelBlock2 = this.models.get(resourceLocation);
         if (modelBlock2 == null || !modelBlock2.isResolved()) {
            LOGGER.warn("Missing model for: " + resourceLocation);
         } else if (this.isCustomRenderer(modelBlock2)) {
            this.bakedRegistry.putObject(modelResourceLocation2, new BuiltInModel(modelBlock2.getAllTransforms()));
         } else {
            this.bakedRegistry.putObject(modelResourceLocation2, this.bakeModel(modelBlock2, ModelRotation.X0_Y0, false));
         }
      }
   }

   private Set<ResourceLocation> getVariantsTextureLocations() {
      HashSet hashSet = Sets.newHashSet();
      ArrayList arrayList = Lists.newArrayList(this.variants.keySet());
      Collections.sort(arrayList, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation modelResourceLocation, ModelResourceLocation modelResourceLocation2) {
            return modelResourceLocation.toString().compareTo(modelResourceLocation2.toString());
         }
      });

      for (Object object : arrayList) {
         ModelResourceLocation modelResourceLocation3 = (ModelResourceLocation)object;
         ModelBlockDefinition.Variants variants2 = this.variants.get(modelResourceLocation3);

         for (ModelBlockDefinition.Variant variant : variants2.getVariants()) {
            ModelBlock modelBlock = this.models.get(variant.getModelLocation());
            if (modelBlock == null) {
               LOGGER.warn("Missing model for: " + modelResourceLocation3);
            } else {
               hashSet.addAll(this.getTextureLocations(modelBlock));
            }
         }
      }

      hashSet.addAll(LOCATIONS_BUILTIN_TEXTURES);
      return hashSet;
   }

   public IBakedModel bakeModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.y(modelBlockIn, modelRotationIn, uvLocked);
   }

   public IBakedModel y(ModelBlock modelBlock, ITransformation iTransformation, boolean bl) {
      TextureAtlasSprite textureAtlasSprite = this.sprites.get(new ResourceLocation(modelBlock.resolveTextureName("particle")));
      SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(modelBlock).setTexture(textureAtlasSprite);

      for (BlockPart blockPart : modelBlock.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            TextureAtlasSprite textureAtlasSprite2 = this.sprites.get(new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture)));
            boolean bl2 = true;
            if (blockPartFace.cullFace != null && bl2) {
               builder.addFaceQuad(iTransformation.rotate(blockPartFace.cullFace), this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, iTransformation, bl));
            } else {
               builder.addGeneralQuad(this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, iTransformation, bl));
            }
         }
      }

      return builder.makeBakedModel();
   }

   public BakedQuad makeBakedQuad(
      BlockPart blockPart,
      BlockPartFace blockPartFace,
      TextureAtlasSprite textureAtlasSprite,
      EnumFacing enumFacing,
      ITransformation iTransformation,
      boolean bl
   ) {
      return this.faceBakery
         .makeBakedQuad(
            blockPart.positionFrom,
            blockPart.positionTo,
            blockPartFace,
            textureAtlasSprite,
            enumFacing,
            iTransformation,
            blockPart.partRotation,
            bl,
            blockPart.shade
         );
   }

   private void loadModelsCheck() {
      this.loadModels();

      for (ModelBlock modelBlock : this.models.values()) {
         modelBlock.getParentFromMap(this.models);
      }

      ModelBlock.checkModelHierarchy(this.models);
   }

   private void loadModels() {
      ArrayDeque arrayDeque = Queues.newArrayDeque();
      HashSet hashSet = Sets.newHashSet();

      for (ResourceLocation resourceLocation : this.models.keySet()) {
         hashSet.add(resourceLocation);
         ResourceLocation resourceLocation2 = this.models.get(resourceLocation).getParentLocation();
         if (resourceLocation2 != null) {
            arrayDeque.add(resourceLocation2);
         }
      }

      while (!arrayDeque.isEmpty()) {
         ResourceLocation resourceLocation3 = (ResourceLocation)arrayDeque.pop();

         try {
            if (this.models.get(resourceLocation3) != null) {
               continue;
            }

            ModelBlock modelBlock = this.loadModel(resourceLocation3);
            this.models.put(resourceLocation3, modelBlock);
            ResourceLocation resourceLocation4 = modelBlock.getParentLocation();
            if (resourceLocation4 != null && !hashSet.contains(resourceLocation4)) {
               arrayDeque.add(resourceLocation4);
            }
         } catch (Exception exception) {
            LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(resourceLocation3)) + "; unable to load model: '" + resourceLocation3 + "'");
         }

         hashSet.add(resourceLocation3);
      }
   }

   private List<ResourceLocation> getParentPath(ResourceLocation resourceLocation) {
      ArrayList arrayList = Lists.newArrayList(resourceLocation);
      ResourceLocation resourceLocation2 = resourceLocation;

      while ((resourceLocation2 = this.getParentLocation(resourceLocation2)) != null) {
         arrayList.add(0, resourceLocation2);
      }

      return arrayList;
   }

   private ResourceLocation getParentLocation(ResourceLocation resourceLocation) {
      for (Entry entry : this.models.entrySet()) {
         ModelBlock modelBlock = (ModelBlock)entry.getValue();
         if (modelBlock != null && resourceLocation.equals(modelBlock.getParentLocation())) {
            return (ResourceLocation)entry.getKey();
         }
      }

      return null;
   }

   private Set<ResourceLocation> getTextureLocations(ModelBlock modelBlock) {
      HashSet hashSet = Sets.newHashSet();

      for (BlockPart blockPart : modelBlock.getElements()) {
         for (BlockPartFace blockPartFace : blockPart.mapFaces.values()) {
            ResourceLocation resourceLocation = new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture));
            hashSet.add(resourceLocation);
         }
      }

      hashSet.add(new ResourceLocation(modelBlock.resolveTextureName("particle")));
      return hashSet;
   }

   private void loadSprites() {
      final Set set = this.getVariantsTextureLocations();
      set.addAll(this.getItemsTextureLocations());
      set.remove(TextureMap.LOCATION_MISSING_TEXTURE);
      IIconCreator iIconCreator = new IIconCreator() {
         @Override
         public void registerSprites(TextureMap iconRegistry) {
            for (Object object : set) {
               ResourceLocation resourceLocation = (ResourceLocation)object;
               TextureAtlasSprite textureAtlasSprite = iconRegistry.registerSprite(resourceLocation);
               ModelBakery.this.sprites.put(resourceLocation, textureAtlasSprite);
            }
         }
      };
      this.textureMap.loadSprites(this.resourceManager, iIconCreator);
      this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
   }

   private Set<ResourceLocation> getItemsTextureLocations() {
      HashSet hashSet = Sets.newHashSet();

      for (ResourceLocation resourceLocation : this.itemLocations.values()) {
         ModelBlock modelBlock = this.models.get(resourceLocation);
         if (modelBlock != null) {
            hashSet.add(new ResourceLocation(modelBlock.resolveTextureName("particle")));
            if (this.hasItemModel(modelBlock)) {
               for (String string : ItemModelGenerator.LAYERS) {
                  ResourceLocation resourceLocation2 = new ResourceLocation(modelBlock.resolveTextureName(string));
                  if (modelBlock.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation2)) {
                     TextureAtlasSprite.setLocationNameCompass(resourceLocation2.toString());
                  } else if (modelBlock.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation2)) {
                     TextureAtlasSprite.setLocationNameClock(resourceLocation2.toString());
                  }

                  hashSet.add(resourceLocation2);
               }
            } else if (!this.isCustomRenderer(modelBlock)) {
               for (BlockPart blockPart : modelBlock.getElements()) {
                  for (BlockPartFace blockPartFace : blockPart.mapFaces.values()) {
                     ResourceLocation resourceLocation3 = new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture));
                     hashSet.add(resourceLocation3);
                  }
               }
            }
         }
      }

      return hashSet;
   }

   private boolean hasItemModel(ModelBlock modelBlock) {
      if (modelBlock == null) {
         return false;
      }

      ModelBlock modelBlock2 = modelBlock.getRootModel();
      return modelBlock2 == MODEL_GENERATED || modelBlock2 == MODEL_COMPASS || modelBlock2 == MODEL_CLOCK;
   }

   private boolean isCustomRenderer(ModelBlock modelBlock) {
      if (modelBlock == null) {
         return false;
      }

      ModelBlock modelBlock2 = modelBlock.getRootModel();
      return modelBlock2 == MODEL_ENTITY;
   }

   private void bakeItemModels() {
      for (ResourceLocation resourceLocation : this.itemLocations.values()) {
         ModelBlock modelBlock = this.models.get(resourceLocation);
         if (this.hasItemModel(modelBlock)) {
            ModelBlock modelBlock2 = this.makeItemModel(modelBlock);
            if (modelBlock2 != null) {
               modelBlock2.name = resourceLocation.toString();
            }

            this.models.put(resourceLocation, modelBlock2);
         } else if (this.isCustomRenderer(modelBlock)) {
            this.models.put(resourceLocation, modelBlock);
         }
      }

      for (TextureAtlasSprite textureAtlasSprite : this.sprites.values()) {
         if (!textureAtlasSprite.hasAnimationMetadata()) {
            textureAtlasSprite.clearFramesTextureData();
         }
      }
   }

   private ModelBlock makeItemModel(ModelBlock modelBlock) {
      return this.itemModelGenerator.makeItemModel(this.textureMap, modelBlock);
   }

   public ModelBlock getModelBlock(ResourceLocation resourceLocation) {
      return this.models.get(resourceLocation);
   }

   public static void fixModelLocations(ModelBlock modelBlock, String string) {
      ResourceLocation resourceLocation = fixModelLocation(modelBlock.getParentLocation(), string);
      if (resourceLocation != modelBlock.getParentLocation()) {
         Reflector.setFieldValue(modelBlock, Reflector.ModelBlock_parentLocation, resourceLocation);
      }

      Map map = (Map)Reflector.getFieldValue(modelBlock, Reflector.ModelBlock_textures);
      if (map != null) {
         for (Object object : map.entrySet()) {
            Entry entry = (Entry)object;
            String string2 = (String)entry.getValue();
            String string3 = fixResourcePath(string2, string);
            if (string3 != string2) {
               entry.setValue(string3);
            }
         }
      }
   }

   public static ResourceLocation fixModelLocation(ResourceLocation resourceLocation, String string) {
      if (resourceLocation != null && string != null) {
         if (!resourceLocation.getResourceDomain().equals("minecraft")) {
            return resourceLocation;
         }

         String string2 = resourceLocation.getResourcePath();
         String string3 = fixResourcePath(string2, string);
         if (string3 != string2) {
            resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), string3);
         }

         return resourceLocation;
      } else {
         return resourceLocation;
      }
   }

   private static String fixResourcePath(String string, String string2) {
      string = TextureUtils.fixResourcePath(string, string2);
      string = StrUtils.removeSuffix(string, ".json");
      return StrUtils.removeSuffix(string, ".png");
   }

   @Deprecated
   public static void addVariantName(Item item, String... strings) {
      RegistryDelegate registryDelegate = (RegistryDelegate)Reflector.getFieldValue(item, Reflector.ForgeItem_delegate);
      if (customVariantNames.containsKey(registryDelegate)) {
         customVariantNames.get(registryDelegate).addAll(Lists.newArrayList(strings));
      } else {
         customVariantNames.put(registryDelegate, Sets.newHashSet(strings));
      }
   }

   public static <T extends ResourceLocation> void registerItemVariants(Item item, T... resourceLocations) {
      RegistryDelegate registryDelegate = (RegistryDelegate)Reflector.getFieldValue(item, Reflector.ForgeItem_delegate);
      if (!customVariantNames.containsKey(registryDelegate)) {
         customVariantNames.put(registryDelegate, Sets.newHashSet());
      }

      for (ResourceLocation resourceLocation : resourceLocations) {
         customVariantNames.get(registryDelegate).add(resourceLocation.toString());
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
