package net.minecraft.src;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;

public class Matches {
   private static final long a = 61400101845240L;

   public static boolean block(BlockStateBase var0, MatchBlock[] var1) {
      if (var1 == null) {
         return true;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         MatchBlock var6 = var1[var5];
         if (var6.matches(var0)) {
            return true;
         }
      }

      return false;
   }

   public static boolean block(int var0, int var1, MatchBlock[] var2) {
      if (var2 == null) {
         return true;
      }

      for (int var6 = 0; var6 < var2.length; var6++) {
         MatchBlock var7 = var2[var6];
         if (var7.matches(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean blockId(int var0, MatchBlock[] var1) {
      if (var1 == null) {
         return true;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         MatchBlock var6 = var1[var5];
         if (var6.getBlockId() == var0) {
            return true;
         }
      }

      return false;
   }

   public static boolean metadata(int var0, int[] var1) {
      if (var1 == null) {
         return true;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var1[var5] == var0) {
            return true;
         }
      }

      return false;
   }

   public static boolean sprite(TextureAtlasSprite var0, TextureAtlasSprite[] var1) {
      if (var1 == null) {
         return true;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var1[var5] == var0) {
            return true;
         }
      }

      return false;
   }

   public static boolean biome(BiomeGenBase var0, BiomeGenBase[] var1) {
      if (var1 == null) {
         return true;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var1[var5] == var0) {
            return true;
         }
      }

      return false;
   }
}
