package shadersmod.client;

import net.minecraft.src.Config;

public class PropertyDefaultFastFancyOff extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"default", "fast", "fancy", "off"};
   public static final String[] USER_VALUES = new String[]{"Default", "Fast", "Fancy", "OFF"};
   private static final long b = 129821826018998L;

   public PropertyDefaultFastFancyOff(String var1, String var2, int var3) {
      super(var1, PROPERTY_VALUES, var2, USER_VALUES, var3);
   }

   public boolean E() {
      return this.getValue() == 0;
   }

   public boolean B() {
      return this.getValue() == 1;
   }

   public boolean K() {
      return this.getValue() == 2;
   }

   public boolean a() {
      return this.getValue() == 3;
   }

   @Override
   public boolean setPropertyValue(String var1) {
      if (Config.equals(var1, "none")) {
         var1 = "off";
      }

      return super.setPropertyValue(var1);
   }
}
