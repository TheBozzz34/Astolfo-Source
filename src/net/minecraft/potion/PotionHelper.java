package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.IntegerCache;

public class PotionHelper {
   public static final String unusedString = null;
   public static final String sugarEffect = "-0+1-2-3&4-4+13";
   public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
   public static final String spiderEyeEffect = "-0-1+2-3&4-4+13";
   public static final String fermentedSpiderEyeEffect = "-0+3-4+13";
   public static final String speckledMelonEffect = "+0-1+2-3&4-4+13";
   public static final String blazePowderEffect = "+0-1-2+3&4-4+13";
   public static final String magmaCreamEffect = "+0+1-2-3&4-4+13";
   public static final String redstoneEffect = "-5+6-7";
   public static final String glowstoneEffect = "+5-6-7";
   public static final String gunpowderEffect = "+14&13-13";
   public static final String goldenCarrotEffect = "-0+1+2-3+13&4-4";
   public static final String pufferfishEffect = "+0-1+2+3+13&4-4";
   public static final String rabbitFootEffect = "+0+1-2+3&4-4+13";
   private static final Map<Integer, String> potionRequirements = Maps.newHashMap();
   private static final Map<Integer, String> potionAmplifiers = Maps.newHashMap();
   private static final Map<Integer, Integer> DATAVALUE_COLORS = Maps.newHashMap();
   private static final String[] potionPrefixes = new String[]{
      "potion.prefix.mundane",
      "potion.prefix.uninteresting",
      "potion.prefix.bland",
      "potion.prefix.clear",
      "potion.prefix.milky",
      "potion.prefix.diffuse",
      "potion.prefix.artless",
      "potion.prefix.thin",
      "potion.prefix.awkward",
      "potion.prefix.flat",
      "potion.prefix.bulky",
      "potion.prefix.bungling",
      "potion.prefix.buttered",
      "potion.prefix.smooth",
      "potion.prefix.suave",
      "potion.prefix.debonair",
      "potion.prefix.thick",
      "potion.prefix.elegant",
      "potion.prefix.fancy",
      "potion.prefix.charming",
      "potion.prefix.dashing",
      "potion.prefix.refined",
      "potion.prefix.cordial",
      "potion.prefix.sparkling",
      "potion.prefix.potent",
      "potion.prefix.foul",
      "potion.prefix.odorless",
      "potion.prefix.rank",
      "potion.prefix.harsh",
      "potion.prefix.acrid",
      "potion.prefix.gross",
      "potion.prefix.stinky"
   };
   private static final long b = 22561189058296L;

   public static boolean checkFlag(int var0, int var1) {
      return (var0 & 1 << var1) != 0;
   }

   private static int isFlagSet(int var0, int var1) {
      return checkFlag(var0, var1) ? 1 : 0;
   }

   private static int isFlagUnset(int var0, int var1) {
      return checkFlag(var0, var1) ? 0 : 1;
   }

   public static int getPotionPrefixIndex(int var0) {
      return getPotionPrefixIndexFlags(var0, 5, 4, 3, 2, 1);
   }

   public static int calcPotionLiquidColor(Collection<PotionEffect> var0) {
      int var4 = 3694022;
      if (var0 != null && !var0.isEmpty()) {
         float var5 = 0.0F;
         float var6 = 0.0F;
         float var7 = 0.0F;
         float var8 = 0.0F;

         for (Object var10 : var0) {
            PotionEffect var11 = (PotionEffect)var10;
            if (var11.getIsShowParticles()) {
               int var12 = Potion.potionTypes[var11.getPotionID()].getLiquidColor();
               if (Config.isCustomColors()) {
                  var12 = CustomColors.getPotionColor(var11.getPotionID(), var12);
               }

               for (int var13 = 0; var13 <= var11.getAmplifier(); var13++) {
                  var5 += (var12 >> 16 & 0xFF) / 255.0F;
                  var6 += (var12 >> 8 & 0xFF) / 255.0F;
                  var7 += (var12 >> 0 & 0xFF) / 255.0F;
                  var8++;
               }
            }
         }

         if (var8 == 0.0F) {
            return 0;
         }

         var5 = var5 / var8 * 255.0F;
         var6 = var6 / var8 * 255.0F;
         var7 = var7 / var8 * 255.0F;
         return (int)var5 << 16 | (int)var6 << 8 | (int)var7;
      } else {
         if (Config.isCustomColors()) {
            var4 = CustomColors.getPotionColor(0, var4);
         }

         return var4;
      }
   }

   public static boolean getAreAmbient(Collection<PotionEffect> var0) {
      for (Object var5 : var0) {
         if (!((PotionEffect)var5).getIsAmbient()) {
            return false;
         }
      }

      return true;
   }

   public static int getLiquidColor(int var0, boolean var1) {
      Integer var5 = IntegerCache.I(var0);
      if (!var1) {
         if (DATAVALUE_COLORS.containsKey(var5)) {
            return DATAVALUE_COLORS.get(var5);
         }

         int var6 = calcPotionLiquidColor(getPotionEffects(var5, false));
         DATAVALUE_COLORS.put(var5, var6);
         return var6;
      } else {
         return calcPotionLiquidColor(getPotionEffects(var5, true));
      }
   }

   public static String getPotionPrefix(int var0) {
      int var1 = getPotionPrefixIndex(var0);
      return potionPrefixes[var1];
   }

   private static int getPotionEffect(boolean var0, boolean var1, boolean var2, int var3, int var4, int var5, int var6) {
      int var10 = 0;
      if (var0) {
         var10 = isFlagUnset(var6, var4);
      } else if (var3 != -1) {
         if (var3 == 0 && countSetFlags(var6) == var4) {
            var10 = 1;
         } else if (var3 == 1 && countSetFlags(var6) > var4) {
            var10 = 1;
         } else if (var3 == 2 && countSetFlags(var6) < var4) {
            var10 = 1;
         }
      } else {
         var10 = isFlagSet(var6, var4);
      }

      if (var1) {
         var10 *= var5;
      }

      if (var2) {
         var10 *= -1;
      }

      return var10;
   }

   private static int countSetFlags(int var0) {
      int var4;
      for (var4 = 0; var0 > 0; var4++) {
         var0 &= var0 - 1;
      }

      return var4;
   }

   private static int parsePotionEffects(String var0, int var1, int var2, int var3) {
      if (var1 < var0.length() && var2 >= 0 && var1 < var2) {
         int var7 = var0.indexOf(124, var1);
         if (var7 >= 0 && var7 < var2) {
            int var20 = parsePotionEffects(var0, var1, var7 - 1, var3);
            if (var20 > 0) {
               return var20;
            }

            int var22 = parsePotionEffects(var0, var7 + 1, var2, var3);
            return var22 > 0 ? var22 : 0;
         } else {
            int var8 = var0.indexOf(38, var1);
            if (var8 >= 0 && var8 < var2) {
               int var21 = parsePotionEffects(var0, var1, var8 - 1, var3);
               if (var21 <= 0) {
                  return 0;
               }

               int var23 = parsePotionEffects(var0, var8 + 1, var2, var3);
               return var23 <= 0 ? 0 : (var21 > var23 ? var21 : var23);
            } else {
               boolean var9 = false;
               boolean var10 = false;
               boolean var11 = false;
               boolean var12 = false;
               boolean var13 = false;
               byte var14 = -1;
               int var15 = 0;
               int var16 = 0;
               int var17 = 0;

               for (int var18 = var1; var18 < var2; var18++) {
                  char var19 = var0.charAt(var18);
                  if (var19 >= '0' && var19 <= '9') {
                     if (var9) {
                        var16 = var19 - '0';
                        var10 = true;
                     } else {
                        var15 *= 10;
                        var15 += var19 - '0';
                        var11 = true;
                     }
                  } else if (var19 == '*') {
                     var9 = true;
                  } else if (var19 == '!') {
                     if (var11) {
                        var17 += getPotionEffect(var12, var10, var13, var14, var15, var16, var3);
                        var12 = false;
                        var13 = false;
                        var9 = false;
                        var10 = false;
                        var11 = false;
                        var16 = 0;
                        var15 = 0;
                        var14 = -1;
                     }

                     var12 = true;
                  } else if (var19 == '-') {
                     if (var11) {
                        var17 += getPotionEffect(var12, var10, var13, var14, var15, var16, var3);
                        var12 = false;
                        var13 = false;
                        var9 = false;
                        var10 = false;
                        var11 = false;
                        var16 = 0;
                        var15 = 0;
                        var14 = -1;
                     }

                     var13 = true;
                  } else if (var19 != '=' && var19 != '<' && var19 != '>') {
                     if (var19 == '+' && var11) {
                        var17 += getPotionEffect(var12, var10, var13, var14, var15, var16, var3);
                        var12 = false;
                        var13 = false;
                        var9 = false;
                        var10 = false;
                        var11 = false;
                        var16 = 0;
                        var15 = 0;
                        var14 = -1;
                     }
                  } else {
                     if (var11) {
                        var17 += getPotionEffect(var12, var10, var13, var14, var15, var16, var3);
                        var12 = false;
                        var13 = false;
                        var9 = false;
                        var10 = false;
                        var11 = false;
                        var16 = 0;
                        var15 = 0;
                        var14 = -1;
                     }

                     if (var19 == '=') {
                        var14 = 0;
                     } else if (var19 == '<') {
                        var14 = 2;
                     } else if (var19 == '>') {
                        var14 = 1;
                     }
                  }
               }

               if (var11) {
                  var17 += getPotionEffect(var12, var10, var13, var14, var15, var16, var3);
               }

               return var17;
            }
         }
      } else {
         return 0;
      }
   }

   public static List<PotionEffect> getPotionEffects(int var0, boolean var1) {
      ArrayList var5 = null;

      for (Potion var9 : Potion.potionTypes) {
         if (var9 != null && (!var9.isUsable() || var1)) {
            String var10 = potionRequirements.get(var9.getId());
            if (var10 != null) {
               int var11 = parsePotionEffects(var10, 0, var10.length(), var0);
               if (var11 > 0) {
                  int var12 = 0;
                  String var13 = potionAmplifiers.get(var9.getId());
                  if (var13 != null) {
                     var12 = parsePotionEffects(var13, 0, var13.length(), var0);
                     if (var12 < 0) {
                        var12 = 0;
                     }
                  }

                  if (var9.isInstant()) {
                     var11 = 1;
                  } else {
                     var11 = 1200 * (var11 * 3 + (var11 - 1) * 2);
                     var11 >>= var12;
                     var11 = (int)Math.round(var11 * var9.getEffectiveness());
                     if ((var0 & 16384) != 0) {
                        var11 = (int)Math.round(var11 * 0.75 + 0.5);
                     }
                  }

                  if (var5 == null) {
                     var5 = Lists.newArrayList();
                  }

                  PotionEffect var14 = new PotionEffect(var9.getId(), var11, var12);
                  if ((var0 & 16384) != 0) {
                     var14.setSplashPotion(true);
                  }

                  var5.add(var14);
               }
            }
         }
      }

      return var5;
   }

   private static int brewBitOperations(int var0, int var1, boolean var2, boolean var3, boolean var4) {
      if (var4) {
         if (!checkFlag(var0, var1)) {
            return 0;
         }
      } else if (var2) {
         var0 &= ~(1 << var1);
      } else if (var3) {
         if ((var0 & 1 << var1) == 0) {
            var0 |= 1 << var1;
         } else {
            var0 &= ~(1 << var1);
         }
      } else {
         var0 |= 1 << var1;
      }

      return var0;
   }

   public static int applyIngredient(int var0, String var1) {
      byte var5 = 0;
      int var6 = var1.length();
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      boolean var10 = false;
      int var11 = 0;

      for (int var12 = var5; var12 < var6; var12++) {
         char var13 = var1.charAt(var12);
         if (var13 >= '0' && var13 <= '9') {
            var11 *= 10;
            var11 += var13 - '0';
            var7 = true;
         } else if (var13 == '!') {
            if (var7) {
               var0 = brewBitOperations(var0, var11, var9, var8, var10);
               var10 = false;
               var8 = false;
               var9 = false;
               var7 = false;
               var11 = 0;
            }

            var8 = true;
         } else if (var13 == '-') {
            if (var7) {
               var0 = brewBitOperations(var0, var11, var9, var8, var10);
               var10 = false;
               var8 = false;
               var9 = false;
               var7 = false;
               var11 = 0;
            }

            var9 = true;
         } else if (var13 == '+') {
            if (var7) {
               var0 = brewBitOperations(var0, var11, var9, var8, var10);
               var10 = false;
               var8 = false;
               var9 = false;
               var7 = false;
               var11 = 0;
            }
         } else if (var13 == '&') {
            if (var7) {
               var0 = brewBitOperations(var0, var11, var9, var8, var10);
               var10 = false;
               var8 = false;
               var9 = false;
               var7 = false;
               var11 = 0;
            }

            var10 = true;
         }
      }

      if (var7) {
         var0 = brewBitOperations(var0, var11, var9, var8, var10);
      }

      return var0 & 32767;
   }

   public static int getPotionPrefixIndexFlags(int var0, int var1, int var2, int var3, int var4, int var5) {
      return (checkFlag(var0, var1) ? 16 : 0)
         | (checkFlag(var0, var2) ? 8 : 0)
         | (checkFlag(var0, var3) ? 4 : 0)
         | (checkFlag(var0, var4) ? 2 : 0)
         | (checkFlag(var0, var5) ? 1 : 0);
   }

   public static void clearPotionColorCache() {
      DATAVALUE_COLORS.clear();
   }

   static {
      long var20 = b ^ 89265280586811L;
      long var10000 = 3132457776577540036L ^ var20;
      potionRequirements.put(Potion.regeneration.getId(), "0 & !1 & !2 & !3 & 0+6");
      potionRequirements.put(Potion.moveSpeed.getId(), "!0 & 1 & !2 & !3 & 1+6");
      potionRequirements.put(Potion.fireResistance.getId(), "0 & 1 & !2 & !3 & 0+6");
      potionRequirements.put(Potion.heal.getId(), "0 & !1 & 2 & !3");
      potionRequirements.put(Potion.poison.getId(), "!0 & !1 & 2 & !3 & 2+6");
      potionRequirements.put(Potion.weakness.getId(), "!0 & !1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.harm.getId(), "!0 & !1 & 2 & 3");
      potionRequirements.put(Potion.moveSlowdown.getId(), "!0 & 1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.damageBoost.getId(), "0 & !1 & !2 & 3 & 3+6");
      potionRequirements.put(Potion.nightVision.getId(), "!0 & 1 & 2 & !3 & 2+6");
      potionRequirements.put(Potion.invisibility.getId(), "!0 & 1 & 2 & 3 & 2+6");
      potionRequirements.put(Potion.waterBreathing.getId(), "0 & !1 & 2 & 3 & 2+6");
      potionRequirements.put(Potion.jump.getId(), "0 & 1 & !2 & 3 & 3+6");
      potionAmplifiers.put(Potion.moveSpeed.getId(), "5");
      potionAmplifiers.put(Potion.digSpeed.getId(), "5");
      potionAmplifiers.put(Potion.damageBoost.getId(), "5");
      potionAmplifiers.put(Potion.regeneration.getId(), "5");
      potionAmplifiers.put(Potion.harm.getId(), "5");
      potionAmplifiers.put(Potion.heal.getId(), "5");
      potionAmplifiers.put(Potion.resistance.getId(), "5");
      potionAmplifiers.put(Potion.poison.getId(), "5");
      potionAmplifiers.put(Potion.jump.getId(), "5");
   }
}
