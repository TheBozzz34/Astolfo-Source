package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
   private final IAttribute field_180373_a;
   private final String unlocalizedName;
   private final double defaultValue;
   private boolean shouldWatch;
   private static final long a = 56878298955109L;

   public BaseAttribute(IAttribute iAttribute, String unlocalizedNameIn, double defaultValueIn) {
      this.field_180373_a = iAttribute;
      this.unlocalizedName = unlocalizedNameIn;
      this.defaultValue = defaultValueIn;
      if (unlocalizedNameIn == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   @Override
   public String getAttributeUnlocalizedName() {
      return this.unlocalizedName;
   }

   @Override
   public double getDefaultValue() {
      return this.defaultValue;
   }

   @Override
   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   public BaseAttribute setShouldWatch(boolean shouldWatchIn) {
      this.shouldWatch = shouldWatchIn;
      return this;
   }

   @Override
   public IAttribute func_180372_d() {
      return this.field_180373_a;
   }

   @Override
   public int hashCode() {
      return this.unlocalizedName.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof IAttribute && this.unlocalizedName.equals(((IAttribute)object).getAttributeUnlocalizedName());
   }
}
