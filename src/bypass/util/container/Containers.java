package bypass.util.container;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class Containers {
   private static final long a = 1563613572023L;

   public static final <E> ListContainer<E> g() {
      return new ListContainer<>();
   }

   public static final <E> ListContainer<E> g(List<E> var0) {
      return new ListContainer<>(var0);
   }

   public static final <E> FilteredContainer<E> G(Predicate<E> var0) {
      return new FilteredContainer<>(g(), var0, false);
   }

   public static final <E> FilteredContainer<E> Z(Predicate<E> var0, boolean var1) {
      return new FilteredContainer<>(g(), var0, var1);
   }

   public static final <E> FilteredContainer<E> s(List<E> var0, Predicate<E> var1) {
      return M(var0, var1, false);
   }

   public static final <E> FilteredContainer<E> M(List<E> var0, Predicate<E> var1, boolean var2) {
      return new FilteredContainer<>(g(var0), var1, var2);
   }

   public static final <A, B> ConvertingListContainer<A, B> q(Function<A, B> var0, Function<B, A> var1) {
      return new ConvertingListContainer<>(g(), var0, var1);
   }

   public static final <A, B> ConvertingListContainer<A, B> x(List<B> var0, Function<A, B> var1, Function<B, A> var2) {
      return new ConvertingListContainer<>(g(var0), var1, var2);
   }

   public static final <K, V> MapContainer<K, V> x() {
      return new MapContainer<>();
   }

   public static final <K, V> MapContainer<K, V> X(Map<K, V> var0) {
      return new MapContainer<>(var0);
   }

   public static final <K, V> FilteredMapContainer<K, V> M(Predicate<K> var0, Predicate<V> var1) {
      return t(var0, var1, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> t(Predicate<K> var0, Predicate<V> var1, boolean var2) {
      return new FilteredMapContainer<>(x(), var0, var1, var2);
   }

   public static final <K, V> FilteredMapContainer<K, V> h(Map<K, V> var0, Predicate<K> var1, Predicate<V> var2) {
      return M(var0, var1, var2, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> M(Map<K, V> var0, Predicate<K> var1, Predicate<V> var2, boolean var3) {
      return new FilteredMapContainer<>(X(var0), var1, var2, var3);
   }
}
