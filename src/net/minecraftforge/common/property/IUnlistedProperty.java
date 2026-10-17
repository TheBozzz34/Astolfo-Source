package net.minecraftforge.common.property;

public interface IUnlistedProperty<V> {
   String getName();

   boolean isValid(V object);

   Class<V> getType();

   String valueToString(V object);
}
