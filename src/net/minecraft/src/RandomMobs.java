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
   private static final long a = 10826246533459L;

   public static void entityLoaded(Entity var0, World var1) {
      if (var0 instanceof EntityLiving && var1 != null) {
         EntityLiving var5 = (EntityLiving)var0;
         var5.spawnPosition = var5.getPosition();
         var5.spawnBiome = var1.getBiomeGenForCoords(var5.spawnPosition);
         WorldServer var6 = Config.getWorldServer();
         if (var6 != null) {
            Entity var7 = var6.getEntityByID(var0.getEntityId());
            if (var7 instanceof EntityLiving) {
               EntityLiving var8 = (EntityLiving)var7;
               UUID var9 = var8.getUniqueID();
               long var10 = var9.getLeastSignificantBits();
               int var12 = (int)(var10 & 2147483647L);
               var5.randomMobsId = var12;
            }
         }
      }
   }

   public static void worldChanged(World var0, World var1) {
      if (var1 != null) {
         List var5 = var1.getLoadedEntityList();

         for (int var6 = 0; var6 < var5.size(); var6++) {
            Entity var7 = (Entity)var5.get(var6);
            entityLoaded(var7, var1);
         }
      }
   }

   public static ResourceLocation getTextureLocation(ResourceLocation var0) {
      if (working) {
         return var0;
      }

      ResourceLocation var4;
      try {
         working = true;
         if (!initialized) {
            initialize();
         }

         if (renderGlobal != null) {
            Entity var5 = renderGlobal.renderedEntity;
            if (!(var5 instanceof EntityLiving)) {
               return var0;
            }

            EntityLiving var6 = (EntityLiving)var5;
            String var7 = var0.getResourcePath();
            if (!var7.startsWith("textures/entity/")) {
               return var0;
            }

            RandomMobsProperties var8 = getProperties(var0);
            if (var8 == null) {
               return var0;
            }

            return var8.getTextureLocation(var0, var6);
         }

         var4 = var0;
      } finally {
         working = false;
      }

      return var4;
   }

   private static RandomMobsProperties getProperties(ResourceLocation var0) {
      String var4 = var0.getResourcePath();
      RandomMobsProperties var5 = (RandomMobsProperties)locationProperties.get(var4);
      if (var5 == null) {
         var5 = makeProperties(var0);
         locationProperties.put(var4, var5);
      }

      return var5;
   }

   private static RandomMobsProperties makeProperties(ResourceLocation var0) {
      String var4 = var0.getResourcePath();
      ResourceLocation var5 = getPropertyLocation(var0);
      if (var5 != null) {
         RandomMobsProperties var6 = parseProperties(var5, var0);
         if (var6 != null) {
            return var6;
         }
      }

      ResourceLocation[] var7 = getTextureVariants(var0);
      return new RandomMobsProperties(var4, var7);
   }

   private static RandomMobsProperties parseProperties(ResourceLocation var0, ResourceLocation var1) {
      try {
         String var5 = var0.getResourcePath();
         Config.dbg("RandomMobs: " + var1.getResourcePath() + ", variants: " + var5);
         InputStream var6 = Config.getResourceStream(var0);
         if (var6 == null) {
            Config.warn("RandomMobs properties not found: " + var5);
            return null;
         } else {
            Properties var7 = new Properties();
            var7.load(var6);
            var6.close();
            RandomMobsProperties var8 = new RandomMobsProperties(var7, var5, var1);
            return !var8.isValid(var5) ? null : var8;
         }
      } catch (FileNotFoundException var9) {
         Config.warn("RandomMobs file not found: " + var1.getResourcePath());
         return null;
      } catch (IOException var10) {
         var10.printStackTrace();
         return null;
      }
   }

   private static ResourceLocation getPropertyLocation(ResourceLocation var0) {
      ResourceLocation var4 = getMcpatcherLocation(var0);
      if (var4 == null) {
         return null;
      }

      String var5 = var4.getResourceDomain();
      String var6 = var4.getResourcePath();
      String var7 = var6;
      if (var6.endsWith(".png")) {
         var7 = var6.substring(0, var6.length() - ".png".length());
      }

      String var8 = var7 + ".properties";
      ResourceLocation var9 = new ResourceLocation(var5, var8);
      if (Config.hasResource(var9)) {
         return var9;
      }

      String var10 = getParentPath(var7);
      if (var10 == null) {
         return null;
      }

      ResourceLocation var11 = new ResourceLocation(var5, var10 + ".properties");
      return Config.hasResource(var11) ? var11 : null;
   }

   public static ResourceLocation getMcpatcherLocation(ResourceLocation var0) {
      String var4 = var0.getResourcePath();
      if (!var4.startsWith("textures/entity/")) {
         return null;
      }

      String var5 = "mcpatcher/mob/" + var4.substring("textures/entity/".length());
      return new ResourceLocation(var0.getResourceDomain(), var5);
   }

   public static ResourceLocation getLocationIndexed(ResourceLocation var0, int var1) {
      if (var0 == null) {
         return null;
      }

      String var5 = var0.getResourcePath();
      int var6 = var5.lastIndexOf(46);
      if (var6 < 0) {
         return null;
      }

      String var7 = var5.substring(0, var6);
      String var8 = var5.substring(var6);
      String var9 = var7 + var1 + var8;
      return new ResourceLocation(var0.getResourceDomain(), var9);
   }

   private static String getParentPath(String var0) {
      for (int var4 = 0; var4 < DEPENDANT_SUFFIXES.length; var4++) {
         String var5 = DEPENDANT_SUFFIXES[var4];
         if (var0.endsWith(var5)) {
            return var0.substring(0, var0.length() - var5.length());
         }
      }

      return null;
   }

   private static ResourceLocation[] getTextureVariants(ResourceLocation var0) {
      ArrayList var4 = new ArrayList();
      var4.add(var0);
      ResourceLocation var5 = getMcpatcherLocation(var0);
      if (var5 == null) {
         return null;
      }

      for (int var6 = 1; var6 < var4.size() + 10; var6++) {
         int var7 = var6 + 1;
         ResourceLocation var8 = getLocationIndexed(var5, var7);
         if (Config.hasResource(var8)) {
            var4.add(var8);
         }
      }

      if (var4.size() <= 1) {
         return null;
      }

      ResourceLocation[] var9 = (net.minecraft.util.ResourceLocation[])(var4.toArray(new ResourceLocation[var4.size()]));
      Config.dbg("RandomMobs: " + var0.getResourcePath() + ", variants: " + var9.length);
      return var9;
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
         ArrayList var3 = new ArrayList();
         var3.add("bat");
         var3.add("blaze");
         var3.add("cat/black");
         var3.add("cat/ocelot");
         var3.add("cat/red");
         var3.add("cat/siamese");
         var3.add("chicken");
         var3.add("cow/cow");
         var3.add("cow/mooshroom");
         var3.add("creeper/creeper");
         var3.add("enderman/enderman");
         var3.add("enderman/enderman_eyes");
         var3.add("ghast/ghast");
         var3.add("ghast/ghast_shooting");
         var3.add("iron_golem");
         var3.add("pig/pig");
         var3.add("sheep/sheep");
         var3.add("sheep/sheep_fur");
         var3.add("silverfish");
         var3.add("skeleton/skeleton");
         var3.add("skeleton/wither_skeleton");
         var3.add("slime/slime");
         var3.add("slime/magmacube");
         var3.add("snowman");
         var3.add("spider/cave_spider");
         var3.add("spider/spider");
         var3.add("spider_eyes");
         var3.add("squid");
         var3.add("villager/villager");
         var3.add("villager/butcher");
         var3.add("villager/farmer");
         var3.add("villager/librarian");
         var3.add("villager/priest");
         var3.add("villager/smith");
         var3.add("wither/wither");
         var3.add("wither/wither_armor");
         var3.add("wither/wither_invulnerable");
         var3.add("wolf/wolf");
         var3.add("wolf/wolf_angry");
         var3.add("wolf/wolf_collar");
         var3.add("wolf/wolf_tame");
         var3.add("zombie_pigman");
         var3.add("zombie/zombie");
         var3.add("zombie/zombie_villager");

         for (int var4 = 0; var4 < var3.size(); var4++) {
            String var5 = (String)var3.get(var4);
            String var6 = "textures/entity/" + var5 + ".png";
            ResourceLocation var7 = new ResourceLocation(var6);
            if (!Config.hasResource(var7)) {
               Config.warn("Not found: " + var7);
            }

            getProperties(var7);
         }
      }
   }

   static {
      long var20 = a ^ 78617163711618L;
      long var10000 = 6767033909335402634L ^ var20;
   }
}
