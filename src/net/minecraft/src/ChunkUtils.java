package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.world.chunk.Chunk;

public class ChunkUtils {
   private static Field fieldHasEntities = null;
   private static boolean fieldHasEntitiesMissing = false;
   private static final long a = 97049233684275L;

   public static boolean hasEntities(Chunk chunk) {
      if (fieldHasEntities == null) {
         if (fieldHasEntitiesMissing) {
            return true;
         }

         fieldHasEntities = findFieldHasEntities(chunk);
         if (fieldHasEntities == null) {
            fieldHasEntitiesMissing = true;
            return true;
         }
      }

      try {
         return fieldHasEntities.getBoolean(chunk);
      } catch (Exception exception) {
         Config.warn("Error calling Chunk.hasEntities");
         Config.warn(exception.getClass().getName() + " " + exception.getMessage());
         fieldHasEntitiesMissing = true;
         return true;
      }
   }

   private static Field findFieldHasEntities(Chunk chunk) {
      try {
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         Field[] fields = Chunk.class.getDeclaredFields();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == boolean.class) {
               field.setAccessible(true);
               arrayList.add(field);
               arrayList2.add(field.get(chunk));
            }
         }

         chunk.setHasEntities(false);
         ArrayList arrayList3 = new ArrayList();

         for (Object object : arrayList) {
            arrayList3.add(((Field)object).get(chunk));
         }

         chunk.setHasEntities(true);
         ArrayList arrayList4 = new ArrayList();

         for (Object object2 : arrayList) {
            arrayList4.add(((Field)object2).get(chunk));
         }

         ArrayList arrayList5 = new ArrayList();

         for (int j = 0; j < arrayList.size(); j++) {
            Field field2 = (Field)arrayList.get(j);
            Boolean boolean2 = (Boolean)arrayList3.get(j);
            Boolean boolean3 = (Boolean)arrayList4.get(j);
            if (!boolean2 && boolean3) {
               arrayList5.add(field2);
               Boolean boolean4 = (Boolean)arrayList2.get(j);
               field2.set(chunk, boolean4);
            }
         }

         if (arrayList5.size() == 1) {
            return (Field)arrayList5.get(0);
         }
      } catch (Exception exception) {
         Config.warn(exception.getClass().getName() + " " + exception.getMessage());
      }

      Config.warn("Error finding Chunk.hasEntities");
      return null;
   }
}
