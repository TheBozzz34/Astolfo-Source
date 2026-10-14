package net.minecraft.src;

import net.minecraft.client.renderer.GlStateManager;

public class Blender {
   public static final int BLEND_ALPHA = 0;
   public static final int BLEND_ADD = 1;
   public static final int BLEND_SUBSTRACT = 2;
   public static final int BLEND_MULTIPLY = 3;
   public static final int BLEND_DODGE = 4;
   public static final int BLEND_BURN = 5;
   public static final int BLEND_SCREEN = 6;
   public static final int BLEND_OVERLAY = 7;
   public static final int BLEND_REPLACE = 8;
   public static final int BLEND_DEFAULT = 1;
   private static final long b = 125099542188790L;

   public static int parseBlend(String str) {
      if (str == null) {
         return 1;
      }

      str = str.toLowerCase().trim();
      if (str.equals("alpha")) {
         return 0;
      }

      if (str.equals("add")) {
         return 1;
      }

      if (str.equals("subtract")) {
         return 2;
      }

      if (str.equals("multiply")) {
         return 3;
      }

      if (str.equals("dodge")) {
         return 4;
      }

      if (str.equals("burn")) {
         return 5;
      }

      if (str.equals("screen")) {
         return 6;
      }

      if (str.equals("overlay")) {
         return 7;
      }

      if (str.equals("replace")) {
         return 8;
      }

      Config.warn("Unknown blend: " + str);
      return 1;
   }

   public static void setupBlend(int blend, float brightness) {
      long l = b ^ 109951973336739L;
      switch (blend) {
         case 0:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            long m = 5496290096778020069L ^ l;
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0F, 1.0F, 1.0F, brightness);
            break;
         case 1:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            long n = 5496290096778020069L ^ l;
            GlStateManager.blendFunc(770, 1);
            GlStateManager.color(1.0F, 1.0F, 1.0F, brightness);
            break;
         case 2:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            long l2 = 3053005766412172941L ^ l;
            GlStateManager.blendFunc(775, 0);
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
            break;
         case 3:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            long l3 = 8769878985459675894L ^ l;
            GlStateManager.blendFunc(774, 771);
            GlStateManager.color(brightness, brightness, brightness, brightness);
            break;
         case 4:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 1);
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
            break;
         case 5:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(0, 769);
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
            break;
         case 6:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 769);
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
            break;
         case 7:
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            long l4 = 6897688568257617263L ^ l;
            GlStateManager.blendFunc(774, 768);
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
            break;
         case 8:
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, brightness);
      }

      GlStateManager.enableTexture2D();
   }

   public static void clearBlend(float rainBrightness) {
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      GlStateManager.color(1.0F, 1.0F, 1.0F, rainBrightness);
   }

   static {
      long l = b ^ 59891862505950L;
      long m = l ^ 1243232283727057597L;
      m = l ^ 4140675952995868507L;
      m = l ^ 2460985279192287900L;
      m = l ^ 8746573612015405545L;
      m = l ^ 6115514171016456520L;
      m = l ^ 338628925286904563L;
      m = l ^ 1130001480011713178L;
   }
}
