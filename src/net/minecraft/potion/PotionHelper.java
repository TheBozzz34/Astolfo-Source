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

   public static boolean checkFlag(int i, int j) {
      return (i & 1 << j) != 0;
   }

   private static int isFlagSet(int i, int j) {
      return checkFlag(i, j) ? 1 : 0;
   }

   private static int isFlagUnset(int i, int j) {
      return checkFlag(i, j) ? 0 : 1;
   }

   public static int getPotionPrefixIndex(int dataValue) {
      return getPotionPrefixIndexFlags(dataValue, 5, 4, 3, 2, 1);
   }

   public static int calcPotionLiquidColor(Collection<PotionEffect> collection) {
      int i = 3694022;
      if (collection != null && !collection.isEmpty()) {
         float f = 0.0F;
         float g = 0.0F;
         float h = 0.0F;
         float f2 = 0.0F;

         for (Object object : collection) {
            PotionEffect potionEffect = (PotionEffect)object;
            if (potionEffect.getIsShowParticles()) {
               int j = Potion.potionTypes[potionEffect.getPotionID()].getLiquidColor();
               if (Config.isCustomColors()) {
                  j = CustomColors.getPotionColor(potionEffect.getPotionID(), j);
               }

               for (int k = 0; k <= potionEffect.getAmplifier(); k++) {
                  f += (j >> 16 & 0xFF) / 255.0F;
                  g += (j >> 8 & 0xFF) / 255.0F;
                  h += (j >> 0 & 0xFF) / 255.0F;
                  f2++;
               }
            }
         }

         if (f2 == 0.0F) {
            return 0;
         }

         f = f / f2 * 255.0F;
         g = g / f2 * 255.0F;
         h = h / f2 * 255.0F;
         return (int)f << 16 | (int)g << 8 | (int)h;
      } else {
         if (Config.isCustomColors()) {
            i = CustomColors.getPotionColor(0, i);
         }

         return i;
      }
   }

   public static boolean getAreAmbient(Collection<PotionEffect> potionEffects) {
      for (Object object : potionEffects) {
         if (!((PotionEffect)object).getIsAmbient()) {
            return false;
         }
      }

      return true;
   }

   public static int getLiquidColor(int dataValue, boolean bypassCache) {
      Integer integer = IntegerCache.I(dataValue);
      if (!bypassCache) {
         if (DATAVALUE_COLORS.containsKey(integer)) {
            return DATAVALUE_COLORS.get(integer);
         }

         int i = calcPotionLiquidColor(getPotionEffects(integer, false));
         DATAVALUE_COLORS.put(integer, i);
         return i;
      } else {
         return calcPotionLiquidColor(getPotionEffects(integer, true));
      }
   }

   public static String getPotionPrefix(int dataValue) {
      int i = getPotionPrefixIndex(dataValue);
      return potionPrefixes[i];
   }

   private static int getPotionEffect(boolean bl, boolean bl2, boolean bl3, int i, int j, int k, int l) {
      int m = 0;
      if (bl) {
         m = isFlagUnset(l, j);
      } else if (i != -1) {
         if (i == 0 && countSetFlags(l) == j) {
            m = 1;
         } else if (i == 1 && countSetFlags(l) > j) {
            m = 1;
         } else if (i == 2 && countSetFlags(l) < j) {
            m = 1;
         }
      } else {
         m = isFlagSet(l, j);
      }

      if (bl2) {
         m *= k;
      }

      if (bl3) {
         m *= -1;
      }

      return m;
   }

   private static int countSetFlags(int i) {
      int j;
      for (j = 0; i > 0; j++) {
         i &= i - 1;
      }

      return j;
   }

   private static int parsePotionEffects(String string, int i, int j, int k) {
      if (i < string.length() && j >= 0 && i < j) {
         int l = string.indexOf(124, i);
         if (l >= 0 && l < j) {
            int m = parsePotionEffects(string, i, l - 1, k);
            if (m > 0) {
               return m;
            }

            int n = parsePotionEffects(string, l + 1, j, k);
            return n > 0 ? n : 0;
         } else {
            int i2 = string.indexOf(38, i);
            if (i2 >= 0 && i2 < j) {
               int i3 = parsePotionEffects(string, i, i2 - 1, k);
               if (i3 <= 0) {
                  return 0;
               }

               int i4 = parsePotionEffects(string, i2 + 1, j, k);
               return i4 <= 0 ? 0 : (i3 > i4 ? i3 : i4);
            } else {
               boolean bl = false;
               boolean bl2 = false;
               boolean bl3 = false;
               boolean bl4 = false;
               boolean bl5 = false;
               byte b = -1;
               int i5 = 0;
               int i6 = 0;
               int i7 = 0;

               for (int i8 = i; i8 < j; i8++) {
                  char c = string.charAt(i8);
                  if (c >= '0' && c <= '9') {
                     if (bl) {
                        i6 = c - '0';
                        bl2 = true;
                     } else {
                        i5 *= 10;
                        i5 += c - '0';
                        bl3 = true;
                     }
                  } else if (c == '*') {
                     bl = true;
                  } else if (c == '!') {
                     if (bl3) {
                        i7 += getPotionEffect(bl4, bl2, bl5, b, i5, i6, k);
                        bl4 = false;
                        bl5 = false;
                        bl = false;
                        bl2 = false;
                        bl3 = false;
                        i6 = 0;
                        i5 = 0;
                        b = -1;
                     }

                     bl4 = true;
                  } else if (c == '-') {
                     if (bl3) {
                        i7 += getPotionEffect(bl4, bl2, bl5, b, i5, i6, k);
                        bl4 = false;
                        bl5 = false;
                        bl = false;
                        bl2 = false;
                        bl3 = false;
                        i6 = 0;
                        i5 = 0;
                        b = -1;
                     }

                     bl5 = true;
                  } else if (c != '=' && c != '<' && c != '>') {
                     if (c == '+' && bl3) {
                        i7 += getPotionEffect(bl4, bl2, bl5, b, i5, i6, k);
                        bl4 = false;
                        bl5 = false;
                        bl = false;
                        bl2 = false;
                        bl3 = false;
                        i6 = 0;
                        i5 = 0;
                        b = -1;
                     }
                  } else {
                     if (bl3) {
                        i7 += getPotionEffect(bl4, bl2, bl5, b, i5, i6, k);
                        bl4 = false;
                        bl5 = false;
                        bl = false;
                        bl2 = false;
                        bl3 = false;
                        i6 = 0;
                        i5 = 0;
                        b = -1;
                     }

                     if (c == '=') {
                        b = 0;
                     } else if (c == '<') {
                        b = 2;
                     } else if (c == '>') {
                        b = 1;
                     }
                  }
               }

               if (bl3) {
                  i7 += getPotionEffect(bl4, bl2, bl5, b, i5, i6, k);
               }

               return i7;
            }
         }
      } else {
         return 0;
      }
   }

   public static List<PotionEffect> getPotionEffects(int i, boolean bl) {
      ArrayList arrayList = null;

      for (Potion potion : Potion.potionTypes) {
         if (potion != null && (!potion.isUsable() || bl)) {
            String string = potionRequirements.get(potion.getId());
            if (string != null) {
               int j = parsePotionEffects(string, 0, string.length(), i);
               if (j > 0) {
                  int k = 0;
                  String string2 = potionAmplifiers.get(potion.getId());
                  if (string2 != null) {
                     k = parsePotionEffects(string2, 0, string2.length(), i);
                     if (k < 0) {
                        k = 0;
                     }
                  }

                  if (potion.isInstant()) {
                     j = 1;
                  } else {
                     j = 1200 * (j * 3 + (j - 1) * 2);
                     j >>= k;
                     j = (int)Math.round(j * potion.getEffectiveness());
                     if ((i & 16384) != 0) {
                        j = (int)Math.round(j * 0.75 + 0.5);
                     }
                  }

                  if (arrayList == null) {
                     arrayList = Lists.newArrayList();
                  }

                  PotionEffect potionEffect = new PotionEffect(potion.getId(), j, k);
                  if ((i & 16384) != 0) {
                     potionEffect.setSplashPotion(true);
                  }

                  arrayList.add(potionEffect);
               }
            }
         }
      }

      return arrayList;
   }

   private static int brewBitOperations(int i, int j, boolean bl, boolean bl2, boolean bl3) {
      if (bl3) {
         if (!checkFlag(i, j)) {
            return 0;
         }
      } else if (bl) {
         i &= ~(1 << j);
      } else if (bl2) {
         if ((i & 1 << j) == 0) {
            i |= 1 << j;
         } else {
            i &= ~(1 << j);
         }
      } else {
         i |= 1 << j;
      }

      return i;
   }

   public static int applyIngredient(int i, String string) {
      byte b = 0;
      int j = string.length();
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      boolean bl4 = false;
      int k = 0;

      for (int l = b; l < j; l++) {
         char c = string.charAt(l);
         if (c >= '0' && c <= '9') {
            k *= 10;
            k += c - '0';
            bl = true;
         } else if (c == '!') {
            if (bl) {
               i = brewBitOperations(i, k, bl3, bl2, bl4);
               bl4 = false;
               bl2 = false;
               bl3 = false;
               bl = false;
               k = 0;
            }

            bl2 = true;
         } else if (c == '-') {
            if (bl) {
               i = brewBitOperations(i, k, bl3, bl2, bl4);
               bl4 = false;
               bl2 = false;
               bl3 = false;
               bl = false;
               k = 0;
            }

            bl3 = true;
         } else if (c == '+') {
            if (bl) {
               i = brewBitOperations(i, k, bl3, bl2, bl4);
               bl4 = false;
               bl2 = false;
               bl3 = false;
               bl = false;
               k = 0;
            }
         } else if (c == '&') {
            if (bl) {
               i = brewBitOperations(i, k, bl3, bl2, bl4);
               bl4 = false;
               bl2 = false;
               bl3 = false;
               bl = false;
               k = 0;
            }

            bl4 = true;
         }
      }

      if (bl) {
         i = brewBitOperations(i, k, bl3, bl2, bl4);
      }

      return i & 32767;
   }

   public static int getPotionPrefixIndexFlags(int i, int j, int k, int l, int m, int n) {
      return (checkFlag(i, j) ? 16 : 0) | (checkFlag(i, k) ? 8 : 0) | (checkFlag(i, l) ? 4 : 0) | (checkFlag(i, m) ? 2 : 0) | (checkFlag(i, n) ? 1 : 0);
   }

   public static void clearPotionColorCache() {
      DATAVALUE_COLORS.clear();
   }

   static {
      long l = b ^ 89265280586811L;
      long m = 3132457776577540036L ^ l;
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
