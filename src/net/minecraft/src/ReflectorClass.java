package net.minecraft.src;

public class ReflectorClass {
   private String targetClassName = null;
   private boolean checked = false;
   private Class targetClass = null;
   private static final long a = 18149157250304L;

   public ReflectorClass(String var1) {
      this(var1, false);
   }

   public ReflectorClass(String var1, boolean var2) {
      this.targetClassName = var1;
      if (!var2) {
         Class var5 = this.getTargetClass();
      }
   }

   public ReflectorClass(Class var1) {
      this.targetClass = var1;
      this.targetClassName = var1.getName();
      this.checked = true;
   }

   public Class getTargetClass() {
      if (this.checked) {
         return this.targetClass;
      }

      this.checked = true;

      try {
         this.targetClass = Class.forName(this.targetClassName);
      } catch (ClassNotFoundException var4) {
         Config.log("(Reflector) Class not present: " + this.targetClassName);
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return this.targetClass;
   }

   public boolean exists() {
      return this.getTargetClass() != null;
   }

   public String getTargetClassName() {
      return this.targetClassName;
   }

   public boolean isInstance(Object var1) {
      return this.getTargetClass() == null ? false : this.getTargetClass().isInstance(var1);
   }

   public ReflectorField makeField(String var1) {
      return new ReflectorField(this, var1);
   }

   public ReflectorMethod makeMethod(String var1) {
      return new ReflectorMethod(this, var1);
   }

   public ReflectorMethod makeMethod(String var1, Class[] var2) {
      return new ReflectorMethod(this, var1, var2);
   }

   public ReflectorMethod makeMethod(String var1, Class[] var2, boolean var3) {
      return new ReflectorMethod(this, var1, var2, var3);
   }
}
