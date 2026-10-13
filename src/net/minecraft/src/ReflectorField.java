package net.minecraft.src;

import java.lang.reflect.Field;

public class ReflectorField {
   private IFieldLocator fieldLocator = null;
   private boolean checked = false;
   private Field targetField = null;
   private static final long a = 105370625710687L;

   public ReflectorField(ReflectorClass var1, String var2) {
      this(new FieldLocatorName(var1, var2));
   }

   public ReflectorField(ReflectorClass var1, Class var2) {
      this(var1, var2, 0);
   }

   public ReflectorField(ReflectorClass var1, Class var2, int var3) {
      this(new FieldLocatorType(var1, var2, var3));
   }

   public ReflectorField(Field var1) {
      this(new FieldLocatorFixed(var1));
   }

   public ReflectorField(IFieldLocator var1) {
      this.fieldLocator = var1;
      this.getTargetField();
   }

   public Field getTargetField() {
      if (this.checked) {
         return this.targetField;
      }

      this.checked = true;
      this.targetField = this.fieldLocator.getField();
      if (this.targetField != null) {
         this.targetField.setAccessible(true);
      }

      return this.targetField;
   }

   public Object getValue() {
      return Reflector.getFieldValue(null, this);
   }

   public void setValue(Object var1) {
      Reflector.setFieldValue(null, this, var1);
   }

   public void setValue(Object var1, Object var2) {
      Reflector.setFieldValue(var1, this, var2);
   }

   public boolean exists() {
      return this.getTargetField() != null;
   }
}
