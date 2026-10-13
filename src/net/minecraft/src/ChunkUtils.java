package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.world.chunk.Chunk;

public class ChunkUtils {
   private static Field fieldHasEntities = null;
   private static boolean fieldHasEntitiesMissing = false;
   private static final long a = 97049233684275L;

   public static boolean hasEntities(Chunk var0) {
      if (fieldHasEntities == null) {
         if (fieldHasEntitiesMissing) {
            return true;
         }

         fieldHasEntities = findFieldHasEntities(var0);
         if (fieldHasEntities == null) {
            fieldHasEntitiesMissing = true;
            return true;
         }
      }

      try {
         return fieldHasEntities.getBoolean(var0);
      } catch (Exception var5) {
         Config.warn("Error calling Chunk.hasEntities");
         Config.warn(var5.getClass().getName() + " " + var5.getMessage());
         fieldHasEntitiesMissing = true;
         return true;
      }
   }

   private static Field findFieldHasEntities(Chunk var0) {
      try {
         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();
         Field[] var6 = Chunk.class.getDeclaredFields();

         for (int var7 = 0; var7 < var6.length; var7++) {
            Field var8 = var6[var7];
            if (var8.getType() == boolean.class) {
               var8.setAccessible(true);
               var4.add(var8);
               var5.add(var8.get(var0));
            }
         }

         var0.setHasEntities(false);
         ArrayList var16 = new ArrayList();

         for (Object var9 : var4) {
            var16.add(((Field)var9).get(var0));
         }

         var0.setHasEntities(true);
         ArrayList var18 = new ArrayList();

         for (Object var10 : var4) {
            var18.add(((Field)var10).get(var0));
         }

         ArrayList var20 = new ArrayList();

         for (int var21 = 0; var21 < var4.size(); var21++) {
            Field var11 = (Field)var4.get(var21);
            Boolean var12 = (Boolean)var16.get(var21);
            Boolean var13 = (Boolean)var18.get(var21);
            if (!var12 && var13) {
               var20.add(var11);
               Boolean var14 = (Boolean)var5.get(var21);
               var11.set(var0, var14);
            }
         }

         if (var20.size() == 1) {
            return (Field)var20.get(0);
         }
      } catch (Exception var15) {
         Config.warn(var15.getClass().getName() + " " + var15.getMessage());
      }

      Config.warn("Error finding Chunk.hasEntities");
      return null;
   }
}
