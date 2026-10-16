package bypass.util.container;

import java.util.List;

public class ListContainerOwner<T> extends InitializedContainerOwner<T> {
   private final List<T> initialItems;

   public ListContainerOwner(List<T> list) {
      this.initialItems = list;
      this.onInit();
   }

   @Override
   public void onInit() {
      try {
         this.initialItems.stream().forEach(value -> this.mutator.addAll((T[])(new Object[]{value})));
      } catch (Exception exception) {
      }
   }
}
