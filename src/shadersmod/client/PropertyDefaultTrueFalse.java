package shadersmod.client;

import net.minecraft.src.Lang;

public class PropertyDefaultTrueFalse extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"default", "true", "false"};
   public static final String[] USER_VALUES = new String[]{"Default", "ON", "OFF"};
   private static final long b = 107112739004810L;

   public PropertyDefaultTrueFalse(String var1, String var2, int var3) {
      super(var1, PROPERTY_VALUES, var2, USER_VALUES, var3);
   }

   @Override
   public String getUserValue() {
      return this.isDefault() ? Lang.getDefault() : (this.isTrue() ? Lang.getOn() : (this.isFalse() ? Lang.getOff() : super.getUserValue()));
   }

   public boolean isDefault() {
      return this.getValue() == 0;
   }

   public boolean isTrue() {
      return this.getValue() == 1;
   }

   public boolean isFalse() {
      return this.getValue() == 2;
   }
}
