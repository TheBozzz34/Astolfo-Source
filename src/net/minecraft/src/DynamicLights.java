package net.minecraft.src;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class DynamicLights {
   private static Map<Integer, DynamicLight> f = new HashMap<>();
   private static long r = 0L;
   private static final double MAX_DIST = 7.5;
   private static final double MAX_DIST_SQ = 56.25;
   private static final int LIGHT_LEVEL_MAX = 15;
   private static final int LIGHT_LEVEL_FIRE = 15;
   private static final int LIGHT_LEVEL_BLAZE = 10;
   private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
   private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
   private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
   private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;

   public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
   }

   public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
      synchronized (f) {
         DynamicLight dynamicLight = f.remove(net.minecraft.util.IntegerCache.getInteger(entityIn.getEntityId()));
         if (dynamicLight != null) {
            dynamicLight.updateLitChunks(renderGlobal);
         }
      }
   }

   public static void update(RenderGlobal renderGlobal) {
      long l = System.currentTimeMillis();
      if (l >= r + 50L) {
         r = l;
         synchronized (f) {
            updateMapDynamicLights(renderGlobal);
            if (f.size() > 0) {
               for (DynamicLight dynamicLight : f.values()) {
                  dynamicLight.update(renderGlobal);
               }
            }
         }
      }
   }

   private static void updateMapDynamicLights(RenderGlobal renderGlobal) {
      WorldClient worldClient = renderGlobal.getWorld();
      if (worldClient != null) {
         for (Entity entity : worldClient.getLoadedEntityList()) {
            int i = getLightLevel(entity);
            if (i > 0) {
               Integer integer = net.minecraft.util.IntegerCache.getInteger(entity.getEntityId());
               DynamicLight dynamicLight = f.get(integer);
               if (dynamicLight == null) {
                  dynamicLight = new DynamicLight(entity);
                  f.put(integer, dynamicLight);
               }
            } else {
               Integer integer2 = net.minecraft.util.IntegerCache.getInteger(entity.getEntityId());
               DynamicLight dynamicLight2 = f.remove(integer2);
               if (dynamicLight2 != null) {
                  dynamicLight2.updateLitChunks(renderGlobal);
               }
            }
         }
      }
   }

   public static int getCombinedLight(BlockPos pos, int combinedLight) {
      double lightPlayer = getLightLevel(pos);
      return getCombinedLight(lightPlayer, combinedLight);
   }

   public static int getCombinedLight(Entity entity, int combinedLight) {
      double lightPlayer = getLightLevel(entity);
      return getCombinedLight(lightPlayer, combinedLight);
   }

   public static int getCombinedLight(double lightPlayer, int combinedLight) {
      if (lightPlayer > 0.0) {
         int i = (int)(lightPlayer * 16.0);
         int j = combinedLight & 0xFF;
         if (i > j) {
            combinedLight &= -256;
            combinedLight |= i;
         }
      }

      return combinedLight;
   }

   public static double getLightLevel(BlockPos pos) {
      double d = 0.0;
      synchronized (f) {
         for (DynamicLight dynamicLight : f.values()) {
            int i = dynamicLight.getLastLightLevel();
            if (i > 0) {
               double e = dynamicLight.getLastPosX();
               double d2 = dynamicLight.getLastPosY();
               double d3 = dynamicLight.getLastPosZ();
               double d4 = pos.getX() - e;
               double d5 = pos.getY() - d2;
               double d6 = pos.getZ() - d3;
               double d7 = d4 * d4 + d5 * d5 + d6 * d6;
               if (dynamicLight.isUnderwater() && !Config.isClearWater()) {
                  i = Config.limit(i - 2, 0, 15);
                  d7 *= 2.0;
               }

               if (d7 <= 56.25) {
                  double d8 = Math.sqrt(d7);
                  double d9 = 1.0 - d8 / 7.5;
                  double d10 = d9 * i;
                  if (d10 > d) {
                     d = d10;
                  }
               }
            }
         }
      }

      return Config.limit(d, 0.0, 15.0);
   }

   public static int getLightLevel(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      }

      Item item = itemStack.getItem();
      if (item instanceof ItemBlock) {
         ItemBlock itemBlock = (ItemBlock)item;
         Block block = itemBlock.getBlock();
         if (block != null) {
            return block.getLightValue();
         }
      }

      return item == Items.lava_bucket
         ? Blocks.lava.getLightValue()
         : (
            item == Items.blaze_rod || item == Items.blaze_powder
               ? 10
               : (
                  item == Items.glowstone_dust
                     ? 8
                     : (
                        item == Items.prismarine_crystals
                           ? 8
                           : (item == Items.magma_cream ? 8 : (item == Items.nether_star ? Blocks.beacon.getLightValue() / 2 : 0))
                     )
               )
         );
   }

   public static int getLightLevel(Entity entity) {
      if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
         return 0;
      }

      if (entity instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entity;
         if (entityPlayer.isSpectator()) {
            return 0;
         }
      }

      if (entity.isBurning()) {
         return 15;
      }

      if (entity instanceof EntityFireball) {
         return 15;
      }

      if (entity instanceof EntityTNTPrimed) {
         return 15;
      }

      if (entity instanceof EntityBlaze) {
         EntityBlaze entityBlaze = (EntityBlaze)entity;
         return entityBlaze.func_70845_n() ? 15 : 10;
      }

      if (entity instanceof EntityMagmaCube) {
         EntityMagmaCube entityMagmaCube = (EntityMagmaCube)entity;
         return entityMagmaCube.squishFactor > 0.6 ? 13 : 8;
      }

      if (entity instanceof EntityCreeper) {
         EntityCreeper entityCreeper = (EntityCreeper)entity;
         if (entityCreeper.getCreeperFlashIntensity(0.0F) > 0.001) {
            return 15;
         }
      }

      if (entity instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         ItemStack itemStack = entityLivingBase.getHeldItem();
         int i = getLightLevel(itemStack);
         ItemStack itemStack2 = entityLivingBase.getEquipmentInSlot(4);
         int j = getLightLevel(itemStack2);
         return Math.max(i, j);
      } else if (entity instanceof EntityItem) {
         EntityItem entityItem = (EntityItem)entity;
         ItemStack itemStack3 = getItemStack(entityItem);
         return getLightLevel(itemStack3);
      } else {
         return 0;
      }
   }

   public static void removeLights(RenderGlobal renderGlobal) {
      synchronized (f) {
         Collection collection = f.values();
         Iterator iterator2 = collection.iterator();

         while (iterator2.hasNext()) {
            DynamicLight dynamicLight = (DynamicLight)iterator2.next();
            iterator2.remove();
            dynamicLight.updateLitChunks(renderGlobal);
         }
      }
   }

   public static void clear() {
      synchronized (f) {
         f.clear();
      }
   }

   public static int getCount() {
      synchronized (f) {
         return f.size();
      }
   }

   public static ItemStack getItemStack(EntityItem entityItem) {
      return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
   }
}
