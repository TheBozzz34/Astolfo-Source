package bypass.util.container;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class Containers {
   public static final <E> ListContainer<E> createList() {
      return new ListContainer<>();
   }

   public static final <E> ListContainer<E> createListFrom(List<E> list) {
      return new ListContainer<>(list);
   }

   public static final <E> FilteredContainer<E> createFilteredList(Predicate<E> predicate) {
      return new FilteredContainer<>(createList(), predicate, false);
   }

   public static final <E> FilteredContainer<E> createFilteredListTracking(Predicate<E> predicate, boolean bl) {
      return new FilteredContainer<>(createList(), predicate, bl);
   }

   public static final <E> FilteredContainer<E> createFilteredListFrom(List<E> list, Predicate<E> predicate) {
      return createFilteredListFromTracking(list, predicate, false);
   }

   public static final <E> FilteredContainer<E> createFilteredListFromTracking(List<E> list, Predicate<E> predicate, boolean bl) {
      return new FilteredContainer<>(createListFrom(list), predicate, bl);
   }

   public static final <A, B> ConvertingListContainer<A, B> createConvertingList(Function<A, B> function, Function<B, A> function2) {
      return new ConvertingListContainer<>(createList(), function, function2);
   }

   public static final <A, B> ConvertingListContainer<A, B> createConvertingListFrom(List<B> list, Function<A, B> function, Function<B, A> function2) {
      return new ConvertingListContainer<>(createListFrom(list), function, function2);
   }

   public static final <K, V> MapContainer<K, V> createMap() {
      return new MapContainer<>();
   }

   public static final <K, V> MapContainer<K, V> createMapFrom(Map<K, V> map) {
      return new MapContainer<>(map);
   }

   public static final <K, V> FilteredMapContainer<K, V> createFilteredMap(Predicate<K> predicate, Predicate<V> predicate2) {
      return createFilteredMapTracking(predicate, predicate2, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> createFilteredMapTracking(Predicate<K> predicate, Predicate<V> predicate2, boolean bl) {
      return new FilteredMapContainer<>(createMap(), predicate, predicate2, bl);
   }

   public static final <K, V> FilteredMapContainer<K, V> createFilteredMapFrom(Map<K, V> map, Predicate<K> predicate, Predicate<V> predicate2) {
      return createFilteredMapFromTracking(map, predicate, predicate2, false);
   }

   public static final <K, V> FilteredMapContainer<K, V> createFilteredMapFromTracking(
      Map<K, V> map, Predicate<K> predicate, Predicate<V> predicate2, boolean bl
   ) {
      return new FilteredMapContainer<>(createMapFrom(map), predicate, predicate2, bl);
   }
}
