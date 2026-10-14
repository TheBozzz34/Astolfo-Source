package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.Minecraft;

public class FieldLocatorActionKeyF3 implements IFieldLocator {
   @Override
   public Field getField() {
      Class<Minecraft> class2 = Minecraft.class;
      Field field2 = this.getFieldRenderChunksMany();
      if (field2 == null) {
         Config.log("(Reflector) Field not present: " + class2.getName() + ".actionKeyF3 (field renderChunksMany not found)");
         return null;
      } else {
         Field field3 = ReflectorRaw.getFieldAfter(Minecraft.class, field2, boolean.class, 0);
         if (field3 == null) {
            Config.log("(Reflector) Field not present: " + class2.getName() + ".actionKeyF3");
            return null;
         } else {
            return field3;
         }
      }
   }

   private Field getFieldRenderChunksMany() {
      Minecraft minecraft = Minecraft.getMinecraft();
      boolean bl = minecraft.renderChunksMany;
      Field[] fields = Minecraft.class.getDeclaredFields();
      minecraft.renderChunksMany = true;
      Field[] fields2 = ReflectorRaw.getFields(minecraft, fields, boolean.class, Boolean.TRUE);
      minecraft.renderChunksMany = false;
      Field[] fields3 = ReflectorRaw.getFields(minecraft, fields, boolean.class, Boolean.FALSE);
      minecraft.renderChunksMany = bl;
      HashSet hashSet = new HashSet<>(Arrays.asList(fields2));
      HashSet hashSet2 = new HashSet<>(Arrays.asList(fields3));
      HashSet hashSet3 = new HashSet(hashSet);
      hashSet3.retainAll(hashSet2);
      Field[] fields4 = (java.lang.reflect.Field[])(hashSet3.toArray(new Field[hashSet3.size()]));
      return fields4.length != 1 ? null : fields4[0];
   }
}
