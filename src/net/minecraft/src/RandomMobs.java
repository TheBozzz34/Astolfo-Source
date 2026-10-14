package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RandomMobs {
   private static Map locationProperties = new HashMap();
   private static RenderGlobal renderGlobal = null;
   private static boolean initialized = false;
   private static Random random = new Random();
   private static boolean working = false;
   public static final String SUFFIX_PNG = ".png";
   public static final String SUFFIX_PROPERTIES = ".properties";
   public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
   public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
   private static final String[] DEPENDANT_SUFFIXES = new String[]{
      "_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"
   };

   public static void entityLoaded(Entity entity, World world) {
      if (entity instanceof EntityLiving && world != null) {
         EntityLiving entityLiving = (EntityLiving)entity;
         entityLiving.spawnPosition = entityLiving.getPosition();
         entityLiving.spawnBiome = world.getBiomeGenForCoords(entityLiving.spawnPosition);
         WorldServer worldServer = Config.getWorldServer();
         if (worldServer != null) {
            Entity entity2 = worldServer.getEntityByID(entity.getEntityId());
            if (entity2 instanceof EntityLiving) {
               EntityLiving entityLiving2 = (EntityLiving)entity2;
               UUID uuid = entityLiving2.getUniqueID();
               long l = uuid.getLeastSignificantBits();
               int i = (int)(l & 2147483647L);
               entityLiving.randomMobsId = i;
            }
         }
      }
   }

   public static void worldChanged(World oldWorld, World newWorld) {
      if (newWorld != null) {
         List list = newWorld.getLoadedEntityList();

         for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            entityLoaded(entity, newWorld);
         }
      }
   }

   public static ResourceLocation getTextureLocation(ResourceLocation loc) {
      if (working) {
         return loc;
      }

      ResourceLocation resourceLocation;
      try {
         working = true;
         if (!initialized) {
            initialize();
         }

         if (renderGlobal != null) {
            Entity entity2 = renderGlobal.renderedEntity;
            if (!(entity2 instanceof EntityLiving)) {
               return loc;
            }

            EntityLiving entityLiving = (EntityLiving)entity2;
            String string = loc.getResourcePath();
            if (!string.startsWith("textures/entity/")) {
               return loc;
            }

            RandomMobsProperties randomMobsProperties = getProperties(loc);
            if (randomMobsProperties == null) {
               return loc;
            }

            return randomMobsProperties.getTextureLocation(loc, entityLiving);
         }

         resourceLocation = loc;
      } finally {
         working = false;
      }

      return resourceLocation;
   }

   private static RandomMobsProperties getProperties(ResourceLocation loc) {
      String string = loc.getResourcePath();
      RandomMobsProperties randomMobsProperties = (RandomMobsProperties)locationProperties.get(string);
      if (randomMobsProperties == null) {
         randomMobsProperties = makeProperties(loc);
         locationProperties.put(string, randomMobsProperties);
      }

      return randomMobsProperties;
   }

   private static RandomMobsProperties makeProperties(ResourceLocation loc) {
      String string = loc.getResourcePath();
      ResourceLocation resourceLocation = getPropertyLocation(loc);
      if (resourceLocation != null) {
         RandomMobsProperties randomMobsProperties = parseProperties(resourceLocation, loc);
         if (randomMobsProperties != null) {
            return randomMobsProperties;
         }
      }

      ResourceLocation[] resourceLocations = getTextureVariants(loc);
      return new RandomMobsProperties(string, resourceLocations);
   }

   private static RandomMobsProperties parseProperties(ResourceLocation propLoc, ResourceLocation resLoc) {
      try {
         String string = propLoc.getResourcePath();
         Config.dbg("RandomMobs: " + resLoc.getResourcePath() + ", variants: " + string);
         InputStream inputStream = Config.getResourceStream(propLoc);
         if (inputStream == null) {
            Config.warn("RandomMobs properties not found: " + string);
            return null;
         } else {
            Properties properties2 = new Properties();
            properties2.load(inputStream);
            inputStream.close();
            RandomMobsProperties randomMobsProperties = new RandomMobsProperties(properties2, string, resLoc);
            return !randomMobsProperties.isValid(string) ? null : randomMobsProperties;
         }
      } catch (FileNotFoundException fileNotFoundException) {
         Config.warn("RandomMobs file not found: " + resLoc.getResourcePath());
         return null;
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return null;
      }
   }

   private static ResourceLocation getPropertyLocation(ResourceLocation loc) {
      ResourceLocation resourceLocation = getMcpatcherLocation(loc);
      if (resourceLocation == null) {
         return null;
      }

      String string = resourceLocation.getResourceDomain();
      String string2 = resourceLocation.getResourcePath();
      String string3 = string2;
      if (string2.endsWith(".png")) {
         string3 = string2.substring(0, string2.length() - ".png".length());
      }

      String string4 = string3 + ".properties";
      ResourceLocation resourceLocation2 = new ResourceLocation(string, string4);
      if (Config.hasResource(resourceLocation2)) {
         return resourceLocation2;
      }

      String string5 = getParentPath(string3);
      if (string5 == null) {
         return null;
      }

      ResourceLocation resourceLocation3 = new ResourceLocation(string, string5 + ".properties");
      return Config.hasResource(resourceLocation3) ? resourceLocation3 : null;
   }

   public static ResourceLocation getMcpatcherLocation(ResourceLocation loc) {
      String string = loc.getResourcePath();
      if (!string.startsWith("textures/entity/")) {
         return null;
      }

      String string2 = "mcpatcher/mob/" + string.substring("textures/entity/".length());
      return new ResourceLocation(loc.getResourceDomain(), string2);
   }

   public static ResourceLocation getLocationIndexed(ResourceLocation loc, int index) {
      if (loc == null) {
         return null;
      }

      String string = loc.getResourcePath();
      int i = string.lastIndexOf(46);
      if (i < 0) {
         return null;
      }

      String string2 = string.substring(0, i);
      String string3 = string.substring(i);
      String string4 = string2 + index + string3;
      return new ResourceLocation(loc.getResourceDomain(), string4);
   }

   private static String getParentPath(String path) {
      for (int i = 0; i < DEPENDANT_SUFFIXES.length; i++) {
         String string = DEPENDANT_SUFFIXES[i];
         if (path.endsWith(string)) {
            return path.substring(0, path.length() - string.length());
         }
      }

      return null;
   }

   private static ResourceLocation[] getTextureVariants(ResourceLocation loc) {
      ArrayList arrayList = new ArrayList();
      arrayList.add(loc);
      ResourceLocation resourceLocation = getMcpatcherLocation(loc);
      if (resourceLocation == null) {
         return null;
      }

      for (int i = 1; i < arrayList.size() + 10; i++) {
         int j = i + 1;
         ResourceLocation resourceLocation2 = getLocationIndexed(resourceLocation, j);
         if (Config.hasResource(resourceLocation2)) {
            arrayList.add(resourceLocation2);
         }
      }

      if (arrayList.size() <= 1) {
         return null;
      }

      ResourceLocation[] resourceLocations = (net.minecraft.util.ResourceLocation[])(arrayList.toArray(new ResourceLocation[arrayList.size()]));
      Config.dbg("RandomMobs: " + loc.getResourcePath() + ", variants: " + resourceLocations.length);
      return resourceLocations;
   }

   public static void resetTextures() {
      locationProperties.clear();
      if (Config.isRandomMobs()) {
         initialize();
      }
   }

   private static void initialize() {
      renderGlobal = Config.getRenderGlobal();
      if (renderGlobal != null) {
         initialized = true;
         ArrayList arrayList = new ArrayList();
         arrayList.add("bat");
         arrayList.add("blaze");
         arrayList.add("cat/black");
         arrayList.add("cat/ocelot");
         arrayList.add("cat/red");
         arrayList.add("cat/siamese");
         arrayList.add("chicken");
         arrayList.add("cow/cow");
         arrayList.add("cow/mooshroom");
         arrayList.add("creeper/creeper");
         arrayList.add("enderman/enderman");
         arrayList.add("enderman/enderman_eyes");
         arrayList.add("ghast/ghast");
         arrayList.add("ghast/ghast_shooting");
         arrayList.add("iron_golem");
         arrayList.add("pig/pig");
         arrayList.add("sheep/sheep");
         arrayList.add("sheep/sheep_fur");
         arrayList.add("silverfish");
         arrayList.add("skeleton/skeleton");
         arrayList.add("skeleton/wither_skeleton");
         arrayList.add("slime/slime");
         arrayList.add("slime/magmacube");
         arrayList.add("snowman");
         arrayList.add("spider/cave_spider");
         arrayList.add("spider/spider");
         arrayList.add("spider_eyes");
         arrayList.add("squid");
         arrayList.add("villager/villager");
         arrayList.add("villager/butcher");
         arrayList.add("villager/farmer");
         arrayList.add("villager/librarian");
         arrayList.add("villager/priest");
         arrayList.add("villager/smith");
         arrayList.add("wither/wither");
         arrayList.add("wither/wither_armor");
         arrayList.add("wither/wither_invulnerable");
         arrayList.add("wolf/wolf");
         arrayList.add("wolf/wolf_angry");
         arrayList.add("wolf/wolf_collar");
         arrayList.add("wolf/wolf_tame");
         arrayList.add("zombie_pigman");
         arrayList.add("zombie/zombie");
         arrayList.add("zombie/zombie_villager");

         for (int i = 0; i < arrayList.size(); i++) {
            String string = (String)arrayList.get(i);
            String string2 = "textures/entity/" + string + ".png";
            ResourceLocation resourceLocation = new ResourceLocation(string2);
            if (!Config.hasResource(resourceLocation)) {
               Config.warn("Not found: " + resourceLocation);
            }

            getProperties(resourceLocation);
         }
      }
   }
}
