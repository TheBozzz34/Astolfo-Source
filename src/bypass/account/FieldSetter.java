package bypass.account;

import java.lang.reflect.Field;

public class FieldSetter {
   private String D;
   private Class<?> P;
   private static final long a = 35121540462273L;
   private static final long c = -6896822431961317393L;

   public FieldSetter(String var1) {
      try {
         this.P = Class.forName(var1);
      } catch (ClassNotFoundException var3) {
         var3.printStackTrace();
      }
   }

   public void k(String var1, Object var2) throws NoSuchFieldException, IllegalAccessException {
      Field var7 = this.P.getDeclaredField(var1);
      var7.setAccessible(true);
      Field var8 = Field.class.getDeclaredField("modifiers");
      var8.setAccessible(true);
      var8.setInt(var7, var7.getModifiers() & (int)c);
      var7.set(null, var2);
   }
}
