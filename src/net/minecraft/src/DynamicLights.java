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
   private static final long a = 106608858704114L;

   public static void P(Entity var0, RenderGlobal var1) {
   }

   public static void z(Entity var0, RenderGlobal var1) {
      synchronized (f) {
         DynamicLight var6 = f.remove(net.minecraft.util.IntegerCache.getInteger(var0.getEntityId()));
         if (var6 != null) {
            var6.updateLitChunks(var1);
         }
      }
   }

   public static void update(RenderGlobal var0) {
      long var4 = System.currentTimeMillis();
      if (var4 >= r + 50L) {
         r = var4;
         synchronized (f) {
            updateMapDynamicLights(var0);
            if (f.size() > 0) {
               for (DynamicLight var8 : f.values()) {
                  var8.update(var0);
               }
            }
         }
      }
   }

   private static void updateMapDynamicLights(RenderGlobal var0) {
      WorldClient var4 = var0.getWorld();
      if (var4 != null) {
         for (Entity var6 : var4.getLoadedEntityList()) {
            int var7 = getLightLevel(var6);
            if (var7 > 0) {
               Integer var8 = net.minecraft.util.IntegerCache.getInteger(var6.getEntityId());
               DynamicLight var9 = f.get(var8);
               if (var9 == null) {
                  var9 = new DynamicLight(var6);
                  f.put(var8, var9);
               }
            } else {
               Integer var10 = net.minecraft.util.IntegerCache.getInteger(var6.getEntityId());
               DynamicLight var12 = f.remove(var10);
               if (var12 != null) {
                  var12.updateLitChunks(var0);
               }
            }
         }
      }
   }

   public static int getCombinedLight(BlockPos var0, int var1) {
      double var2 = getLightLevel(var0);
      return getCombinedLight(var2, var1);
   }

   public static int getCombinedLight(Entity var0, int var1) {
      double var2 = getLightLevel(var0);
      return getCombinedLight(var2, var1);
   }

   public static int getCombinedLight(double var0, int var2) {
      if (var0 > 0.0) {
         int var6 = (int)(var0 * 16.0);
         int var7 = var2 & 0xFF;
         if (var6 > var7) {
            var2 &= -256;
            var2 |= var6;
         }
      }

      return var2;
   }

   public static double getLightLevel(BlockPos var0) {
      double var4 = 0.0;
      synchronized (f) {
         for (DynamicLight var8 : f.values()) {
            int var9 = var8.getLastLightLevel();
            if (var9 > 0) {
               double var10 = var8.B();
               double var12 = var8.r();
               double var14 = var8.f();
               double var16 = var0.getX() - var10;
               double var18 = var0.getY() - var12;
               double var20 = var0.getZ() - var14;
               double var22 = var16 * var16 + var18 * var18 + var20 * var20;
               if (var8.isUnderwater() && !Config.V()) {
                  var9 = Config.limit(var9 - 2, 0, 15);
                  var22 *= 2.0;
               }

               if (var22 <= 56.25) {
                  double var24 = Math.sqrt(var22);
                  double var26 = 1.0 - var24 / 7.5;
                  double var28 = var26 * var9;
                  if (var28 > var4) {
                     var4 = var28;
                  }
               }
            }
         }
      }

      return Config.limit(var4, 0.0, 15.0);
   }

   public static int getLightLevel(ItemStack var0) {
      if (var0 == null) {
         return 0;
      }

      Item var4 = var0.getItem();
      if (var4 instanceof ItemBlock) {
         ItemBlock var5 = (ItemBlock)var4;
         Block var6 = var5.getBlock();
         if (var6 != null) {
            return var6.getLightValue();
         }
      }

      return var4 == Items.lava_bucket
         ? Blocks.lava.getLightValue()
         : (
            var4 == Items.blaze_rod || var4 == Items.blaze_powder
               ? 10
               : (
                  var4 == Items.glowstone_dust
                     ? 8
                     : (
                        var4 == Items.prismarine_crystals
                           ? 8
                           : (var4 == Items.magma_cream ? 8 : (var4 == Items.nether_star ? Blocks.beacon.getLightValue() / 2 : 0))
                     )
               )
         );
   }

   public static int getLightLevel(Entity var0) {
      if (var0 == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
         return 0;
      }

      if (var0 instanceof EntityPlayer) {
         EntityPlayer var4 = (EntityPlayer)var0;
         if (var4.isSpectator()) {
            return 0;
         }
      }

      if (var0.isBurning()) {
         return 15;
      }

      if (var0 instanceof EntityFireball) {
         return 15;
      }

      if (var0 instanceof EntityTNTPrimed) {
         return 15;
      }

      if (var0 instanceof EntityBlaze) {
         EntityBlaze var13 = (EntityBlaze)var0;
         return var13.func_70845_n() ? 15 : 10;
      }

      if (var0 instanceof EntityMagmaCube) {
         EntityMagmaCube var12 = (EntityMagmaCube)var0;
         return var12.squishFactor > 0.6 ? 13 : 8;
      }

      if (var0 instanceof EntityCreeper) {
         EntityCreeper var9 = (EntityCreeper)var0;
         if (var9.getCreeperFlashIntensity(0.0F) > 0.001) {
            return 15;
         }
      }

      if (var0 instanceof EntityLivingBase) {
         EntityLivingBase var11 = (EntityLivingBase)var0;
         ItemStack var14 = var11.getHeldItem();
         int var6 = getLightLevel(var14);
         ItemStack var7 = var11.getEquipmentInSlot(4);
         int var8 = getLightLevel(var7);
         return Math.max(var6, var8);
      } else if (var0 instanceof EntityItem) {
         EntityItem var10 = (EntityItem)var0;
         ItemStack var5 = getItemStack(var10);
         return getLightLevel(var5);
      } else {
         return 0;
      }
   }

   public static void removeLights(RenderGlobal var0) {
      synchronized (f) {
         Collection var5 = f.values();
         Iterator var6 = var5.iterator();

         while (var6.hasNext()) {
            DynamicLight var7 = (DynamicLight)var6.next();
            var6.remove();
            var7.updateLitChunks(var0);
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

   public static ItemStack getItemStack(EntityItem var0) {
      return var0.getDataWatcher().getWatchableObjectItemStack(10);
   }

   static {
      long var0 = a ^ 74668808572200L;
      long var10000 = var0 ^ 9189614441925196700L;
      var10000 = var0 ^ 889390479442330192L;
      var10000 = var0 ^ 5604660363056514199L;
      var10000 = var0 ^ 9189614441925196700L;
      var10000 = var0 ^ 9189614441925196700L;
      var10000 = var0 ^ 6284842428959961993L;
      var10000 = var0 ^ 889390479442330192L;
   }
}
