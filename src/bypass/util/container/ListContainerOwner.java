package bypass.util.container;

import java.util.List;

public class ListContainerOwner<T> extends InitializedContainerOwner<T> {
   private final List<T> y;

   public ListContainerOwner(List<T> list) {
      this.y = list;
      this.onInit();
   }

   @Override
   public void onInit() {
      try {
         this.y.stream().forEach(value -> this.T.q((T[])(new Object[]{value})));
      } catch (Exception exception) {
      }
   }
}
