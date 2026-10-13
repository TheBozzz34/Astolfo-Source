package shadersmod.client;

import java.util.Properties;
import net.minecraft.src.Config;
import org.apache.commons.lang3.ArrayUtils;

public class Property {
   private int[] values = null;
   private int defaultValue = 0;
   private String propertyName = null;
   private String[] propertyValues = null;
   private String userName = null;
   private String[] userValues = null;
   private int value = 0;
   private static final long a = 57042648926996L;

   public Property(String var1, String[] var2, String var3, String[] var4, int var5) {
      this.propertyName = var1;
      this.propertyValues = var2;
      this.userName = var3;
      this.userValues = var4;
      this.defaultValue = var5;
      if (var2.length != var4.length) {
         throw new IllegalArgumentException("Property and user values have different lengths: " + var2.length + " != " + var4.length);
      }

      if (var5 >= 0 && var5 < var2.length) {
         this.value = var5;
      } else {
         throw new IllegalArgumentException("Invalid default value: " + var5);
      }
   }

   public boolean setPropertyValue(String var1) {
      if (var1 == null) {
         this.value = this.defaultValue;
         return false;
      }

      this.value = ArrayUtils.indexOf(this.propertyValues, var1);
      if (this.value >= 0 && this.value < this.propertyValues.length) {
         return true;
      }

      this.value = this.defaultValue;
      return false;
   }

   public void N() {
      this.value++;
      if (this.value < 0 || this.value >= this.propertyValues.length) {
         this.value = 0;
      }
   }

   public void setValue(int var1) {
      this.value = var1;
      if (this.value < 0 || this.value >= this.propertyValues.length) {
         this.value = this.defaultValue;
      }
   }

   public int getValue() {
      return this.value;
   }

   public String getUserValue() {
      return this.userValues[this.value];
   }

   public String getPropertyValue() {
      return this.propertyValues[this.value];
   }

   public String d() {
      return this.userName;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public void resetValue() {
      this.value = this.defaultValue;
   }

   public boolean loadFrom(Properties var1) {
      this.resetValue();
      if (var1 == null) {
         return false;
      }

      String var5 = var1.getProperty(this.propertyName);
      return var5 == null ? false : this.setPropertyValue(var5);
   }

   public void saveTo(Properties var1) {
      if (var1 != null) {
         var1.setProperty(this.getPropertyName(), this.getPropertyValue());
      }
   }

   @Override
   public String toString() {
      return "" + this.propertyName + "=" + this.getPropertyValue() + " [" + Config.arrayToString(this.propertyValues) + "], value: " + this.value;
   }
}
