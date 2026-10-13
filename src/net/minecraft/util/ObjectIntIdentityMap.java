package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
   private final IdentityHashMap<T, Integer> identityMap;
   private final List<T> objectList;
   private static final long a = 80494896267593L;
   private static final long b = -6357912441287867904L;

   public ObjectIntIdentityMap() {
      this.identityMap = new IdentityHashMap<>((int)b);
      this.objectList = Lists.newArrayList();
   }

   public void put(T var1, int var2) {
      this.identityMap.put((T)var1, var2);

      while (this.objectList.size() <= var2) {
         this.objectList.add(null);
      }

      this.objectList.set(var2, (T)var1);
   }

   public int get(T var1) {
      Integer var4 = this.identityMap.get(var1);
      return var4 == null ? -1 : var4;
   }

   public final T getByValue(int var1) {
      return var1 >= 0 && var1 < this.objectList.size() ? this.objectList.get(var1) : null;
   }

   @Override
   public Iterator<T> iterator() {
      return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
   }

   public List getObjectList() {
      return this.objectList;
   }
}
