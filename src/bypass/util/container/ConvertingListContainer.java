package bypass.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ConvertingListContainer<A, B> extends ListContainer<B> implements ContainerSupplier<ListContainer<B>> {
   private ListContainer<B> source;
   private Function<A, B> converter;
   private Function<B, A> backConverter;

   public ConvertingListContainer(ListContainer<B> listContainer, Function<A, B> function, Function<B, A> function2) {
      super(listContainer.getValue());
      this.source = listContainer;
      this.converter = function;
      this.backConverter = function2;
   }

   public void convertAndAddEach(A... objects) {
      for (Object object : objects) {
         super.add(this.convert((A)object));
      }
   }

   public boolean convertAndAdd(A object) {
      return this.add(this.convert((A)object));
   }

   public void convertAndInsert(int i, A object) {
      super.insertAt(i, this.convert((A)object));
   }

   public boolean convertAndAddAll(Collection<? extends A> collection) {
      ArrayList arrayList = new ArrayList();
      collection.stream().forEach(value -> arrayList.add(this.convert((A)value)));
      return super.addAllFrom(arrayList);
   }

   public boolean convertAndAddAll(int i, Collection<? extends A> collection) {
      ArrayList arrayList = new ArrayList();
      collection.stream().forEach(value -> arrayList.add(this.convert((A)value)));
      return super.addAllAt(i, arrayList);
   }

   private B convert(A object) {
      if (this.converter == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.converter.apply((A)object);
      }
   }

   private A convertBack(B object) {
      if (this.backConverter == null) {
         throw new NullPointerException("The function that converts given element into another can't be null.");
      } else {
         return this.backConverter.apply((B)object);
      }
   }

   public List<A> createUnconvertedList() {
      ArrayList arrayList = new ArrayList();
      this.getValue().stream().forEach(value -> arrayList.add(this.convertBack((B)value)));
      return arrayList;
   }

   public Function<A, B> getConverter() {
      return this.converter;
   }

   public Function<A, B> getForwardConverter() {
      return this.converter;
   }

   public ListContainer<B> getContainer() {
      return this.source;
   }
}
