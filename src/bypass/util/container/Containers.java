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

   public static final <E> ListContainer<E> g(List<E> list) {
      return new ListContainer<>(list);
   }

   public static final <E> FilteredContainer<E> G(Predicate<E> predicate) {
      return new FilteredContainer<>(g(), predicate, false);
   }

   public static final <E> FilteredContainer<E> Z(Predicate<E> predicate, boolean bl) {
      return new FilteredContainer<>(g(), predicate, bl);
   }

   public static final <E> FilteredContainer<E> s(List<E> list, Predicate<E> predicate) {
      return M(list, predicate, false);
   }

   public static final <E> FilteredContainer<E> M(List<E> list, Predicate<E> predicate, boolean bl) {
      return new FilteredContainer<>(g(list), predicate, bl);
   }

   public static final <A, B> ConvertingListContainer<A, B> q(Function<A, B> function, Function<B, A> function2) {
      return new ConvertingListContainer<>(g(), function, function2);
   }

   public static final <A, B> ConvertingListContainer<A, B> x(List<B> list, Function<A, B> function, Function<B, A> function2) {
      return new ConvertingListContainer<>(g(list), function, function2);
   }

   public static final <K, V> MapContainer<K, V> x() {
      return new MapContainer<>();
   }

   public static final <K, V> MapContainer<K, V> X(Map<K, V> map) {
      return new MapContainer<>(map);
   }

   public static final <K, V> FilteredMapContainer<K, V> M(Predicate<K> predicate, Predicate<V> predicate2) {
      return t(predicate, predicate2, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> t(Predicate<K> predicate, Predicate<V> predicate2, boolean bl) {
      return new FilteredMapContainer<>(x(), predicate, predicate2, bl);
   }

   public static final <K, V> FilteredMapContainer<K, V> h(Map<K, V> map, Predicate<K> predicate, Predicate<V> predicate2) {
      return M(map, predicate, predicate2, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> M(Map<K, V> map, Predicate<K> predicate, Predicate<V> predicate2, boolean bl) {
      return new FilteredMapContainer<>(X(map), predicate, predicate2, bl);
   }
}
