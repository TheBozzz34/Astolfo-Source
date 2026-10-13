package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectorRaw {
   private static final long a = 139436697422967L;

   public static Field getField(Class var0, Class var1) {
      try {
         Field[] var5 = var0.getDeclaredFields();

         for (int var6 = 0; var6 < var5.length; var6++) {
            Field var7 = var5[var6];
            if (var7.getType() == var1) {
               var7.setAccessible(true);
               return var7;
            }
         }

         return null;
      } catch (Exception var8) {
         return null;
      }
   }

   public static Field[] getFields(Class var0, Class var1) {
      try {
         Field[] var2 = var0.getDeclaredFields();
         return getFields(var2, var1);
      } catch (Exception var3) {
         return null;
      }
   }

   public static Field[] getFields(Field[] var0, Class var1) {
      try {
         ArrayList var5 = new ArrayList();

         for (int var6 = 0; var6 < var0.length; var6++) {
            Field var7 = var0[var6];
            if (var7.getType() == var1) {
               var7.setAccessible(true);
               var5.add(var7);
            }
         }

         return (java.lang.reflect.Field[])(var5.toArray(new Field[var5.size()]));
      } catch (Exception var8) {
         return null;
      }
   }

   public static Field[] getFieldsAfter(Class var0, Field var1, Class var2) {
      try {
         Field[] var6 = var0.getDeclaredFields();
         List var7 = Arrays.asList(var6);
         int var8 = var7.indexOf(var1);
         if (var8 < 0) {
            return new Field[0];
         }

         List var9 = var7.subList(var8 + 1, var7.size());
         Field[] var10 = (java.lang.reflect.Field[])(var9.toArray(new Field[var9.size()]));
         return getFields(var10, var2);
      } catch (Exception var11) {
         return null;
      }
   }

   public static Field[] getFields(Object var0, Field[] var1, Class var2, Object var3) {
      try {
         ArrayList var7 = new ArrayList();

         for (int var8 = 0; var8 < var1.length; var8++) {
            Field var9 = var1[var8];
            if (var9.getType() == var2) {
               boolean var10 = Modifier.isStatic(var9.getModifiers());
               if ((var0 != null || var10) && (var0 == null || !var10)) {
                  var9.setAccessible(true);
                  Object var11 = var9.get(var0);
                  if (var11 == var3) {
                     var7.add(var9);
                  } else if (var11 != null && var3 != null && var11.equals(var3)) {
                     var7.add(var9);
                  }
               }
            }
         }

         return (java.lang.reflect.Field[])(var7.toArray(new Field[var7.size()]));
      } catch (Exception var12) {
         return null;
      }
   }

   public static Field getField(Class var0, Class var1, int var2) {
      Field[] var6 = getFields(var0, var1);
      return var2 >= 0 && var2 < var6.length ? var6[var2] : null;
   }

   public static Field getFieldAfter(Class var0, Field var1, Class var2, int var3) {
      Field[] var7 = getFieldsAfter(var0, var1, var2);
      return var3 >= 0 && var3 < var7.length ? var7[var3] : null;
   }

   public static Object getFieldValue(Object var0, Class var1, Class var2) {
      ReflectorField var6 = getReflectorField(var1, var2);
      return var6 == null ? null : (!var6.exists() ? null : Reflector.getFieldValue(var0, var6));
   }

   public static Object getFieldValue(Object var0, Class var1, Class var2, int var3) {
      ReflectorField var7 = getReflectorField(var1, var2, var3);
      return var7 == null ? null : (!var7.exists() ? null : Reflector.getFieldValue(var0, var7));
   }

   public static boolean setFieldValue(Object var0, Class var1, Class var2, Object var3) {
      ReflectorField var7 = getReflectorField(var1, var2);
      return var7 == null ? false : (!var7.exists() ? false : Reflector.setFieldValue(var0, var7, var3));
   }

   public static boolean setFieldValue(Object var0, Class var1, Class var2, int var3, Object var4) {
      ReflectorField var8 = getReflectorField(var1, var2, var3);
      return var8 == null ? false : (!var8.exists() ? false : Reflector.setFieldValue(var0, var8, var4));
   }

   public static ReflectorField getReflectorField(Class var0, Class var1) {
      Field var4 = getField(var0, var1);
      if (var4 == null) {
         return null;
      }

      ReflectorClass var5 = new ReflectorClass(var0);
      return new ReflectorField(var5, var4.getName());
   }

   public static ReflectorField getReflectorField(Class var0, Class var1, int var2) {
      Field var5 = getField(var0, var1, var2);
      if (var5 == null) {
         return null;
      }

      ReflectorClass var6 = new ReflectorClass(var0);
      return new ReflectorField(var6, var5.getName());
   }
}
