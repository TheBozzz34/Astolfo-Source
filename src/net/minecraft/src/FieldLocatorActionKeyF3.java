package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.Minecraft;

public class FieldLocatorActionKeyF3 implements IFieldLocator {
   private static final long a = 137952645474901L;

   @Override
   public Field getField() {
      Class<Minecraft> var4 = Minecraft.class;
      Field var5 = this.getFieldRenderChunksMany();
      if (var5 == null) {
         Config.log("(Reflector) Field not present: " + var4.getName() + ".actionKeyF3 (field renderChunksMany not found)");
         return null;
      } else {
         Field var6 = ReflectorRaw.getFieldAfter(Minecraft.class, var5, boolean.class, 0);
         if (var6 == null) {
            Config.log("(Reflector) Field not present: " + var4.getName() + ".actionKeyF3");
            return null;
         } else {
            return var6;
         }
      }
   }

   private Field getFieldRenderChunksMany() {
      Minecraft var4 = Minecraft.getMinecraft();
      boolean var5 = var4.renderChunksMany;
      Field[] var6 = Minecraft.class.getDeclaredFields();
      var4.renderChunksMany = true;
      Field[] var7 = ReflectorRaw.getFields(var4, var6, boolean.class, Boolean.TRUE);
      var4.renderChunksMany = false;
      Field[] var8 = ReflectorRaw.getFields(var4, var6, boolean.class, Boolean.FALSE);
      var4.renderChunksMany = var5;
      HashSet var9 = new HashSet<>(Arrays.asList(var7));
      HashSet var10 = new HashSet<>(Arrays.asList(var8));
      HashSet var11 = new HashSet(var9);
      var11.retainAll(var10);
      Field[] var12 = (java.lang.reflect.Field[])(var11.toArray(new Field[var11.size()]));
      return var12.length != 1 ? null : var12[0];
   }
}
