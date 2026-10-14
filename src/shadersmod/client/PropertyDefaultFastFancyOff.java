package shadersmod.client;

import net.minecraft.src.Config;

public class PropertyDefaultFastFancyOff extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"default", "fast", "fancy", "off"};
   public static final String[] USER_VALUES = new String[]{"Default", "Fast", "Fancy", "OFF"};
   private static final long b = 129821826018998L;

   public PropertyDefaultFastFancyOff(String propertyName, String userName, int defaultValue) {
      super(propertyName, PROPERTY_VALUES, userName, USER_VALUES, defaultValue);
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
   public boolean setPropertyValue(String propVal) {
      if (Config.equals(propVal, "none")) {
         propVal = "off";
      }

      return super.setPropertyValue(propVal);
   }
}
